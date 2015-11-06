package fri.muc.peterus.muc_hw.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.Constants;
import fri.muc.peterus.muc_hw.helpers.LocationsSQLiteOpenHelper;

/**
 * Created by peterus on 4.11.2015.
 */
public class LocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationsSQLiteOpenHelper mDatabaseHelper;
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
        Log.d("LocationService", "Stop location updates");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Calendar now = Calendar.getInstance();
        Calendar workStart = Calendar.getInstance();
        workStart.set(Calendar.HOUR_OF_DAY, Constants.WORK_START);
        mAction = now.before(workStart) ? "sleep" : "work";
        startLocationUpdates();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mDatabaseHelper = new LocationsSQLiteOpenHelper(ApplicationContext.getContext());
        Log.d("LocationService", "CREATED");

    }

    @Override
    public void onDestroy() {
        Log.d("LocationService", "DESTROYED");

        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LocationService", "CONNECTED");
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
        Log.d("LocationIntentService", "On location changed: " + location.getLatitude() + "," + location.getLongitude());
        if (location != null)
            recordLocData(location.getLatitude(), location.getLongitude(), mAction);
        stopLocationUpdates();
    }

    private void recordLocData(double lat, double lng, String label){
        Log.d("LocationIntentService", "Storing:"+ lat + " " + lng + " " + label);
        SQLiteDatabase writableDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationsSQLiteOpenHelper.LAT, lat);
        values.put(LocationsSQLiteOpenHelper.LNG, lng);
        values.put(LocationsSQLiteOpenHelper.LABEL, label);
        writableDatabase.insert(LocationsSQLiteOpenHelper.TABLE_NAME, null, values);
        writableDatabase.close();
    }
}
