package fri.muc.peterus.muc_hw.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.Constants;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;
import fri.muc.peterus.muc_hw.helpers.SensingTriggerHelper;

/**
 * Created by peterus on 4.11.2015.
 */
public class LocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private DBOpenHelper mDatabaseHelper;
    private String mAction;

    public LocationIntentService() {
        super("LocationIntentService");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    protected synchronized void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.reconnect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startLocationUpdates();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(LocationIntentService.this, "On location changed", Toast.LENGTH_LONG).show();
        if (location != null){
            long triggerId = SensingTriggerHelper.getSensingTriggerId();
            setMAction((float) location.getLatitude(), (float) location.getLongitude());
            Log.d("LocationIntentService", "On location changed: " + location.getLatitude() + "," + location.getLongitude() + ":" + mAction);
            recordLocData(location.getLatitude(), location.getLongitude(), mAction, triggerId);
        }
        stopLocationUpdates();
    }

    private void recordLocData(double lat, double lng, String label, long triggerId){
        Log.d("LocationIntentService", "Storing:"+ lat + " " + lng + " " + label);
        SQLiteDatabase writableDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.LAT, lat);
        values.put(DBOpenHelper.LNG, lng);
        values.put(DBOpenHelper.LABEL, label);
        values.put(DBOpenHelper.TRIGGER_ID, triggerId);
        writableDatabase.insert(DBOpenHelper.TABLE_NAME, null, values);
        Cursor c = writableDatabase.rawQuery("select * from " + DBOpenHelper.TABLE_NAME, new String[]{});
        int count = c.getCount();
        c.close();
        writableDatabase.close();
        Log.d("LocationIntentService", "COUNT:" + count);

        // We cluster after X readings.
        if (count == Constants.CLUST_THRESHOLD){
            Intent intent = new Intent(ApplicationContext.getContext(), LocationMachineLearningIntentService.class);
            startService(intent);
        }
    }

    private static float distMeters(float lat1, float lng1, float lat2, float lng2){
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private void setMAction(float currLat, float currLng){
        if(LocationMachineLearningIntentService.isLocationClusteringTrained()){
            SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
            float workLat = settings.getFloat("workLat", -1);
            float workLng = settings.getFloat("workLng", -1);
            float sleepLat = settings.getFloat("sleepLat", -1);
            float sleepLng = settings.getFloat("sleepLng", -1);
            float dist2work = distMeters(workLat, workLng, currLat, currLng);
            float dist2home = distMeters(sleepLat, sleepLng, currLat, currLng);

            if (dist2home < 200){
                mAction = "sleep";
            }
            else if (dist2work < 200){
                mAction = "work";
            }
            else{
                mAction = "other";
            }

        }
        else {
            Calendar now = Calendar.getInstance();
            Calendar workStart = Calendar.getInstance();
            workStart.set(Calendar.HOUR_OF_DAY, Constants.WORK_START);
            Calendar workEnd = Calendar.getInstance();
            workEnd.set(Calendar.HOUR_OF_DAY, Constants.WORK_STOP);
            Calendar sleepStart = Calendar.getInstance();
            sleepStart.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_START);
            Calendar sleepEnd = Calendar.getInstance();
            sleepEnd.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_STOP);

            if (now.before(workEnd) && workStart.before(now))
                mAction = "work";

            else if (now.before(sleepEnd) && sleepStart.before(now))
                mAction = "sleep";
            else{
                mAction = "other";
            }
        }
    }
}
