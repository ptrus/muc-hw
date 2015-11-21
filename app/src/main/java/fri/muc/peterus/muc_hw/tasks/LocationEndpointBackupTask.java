package fri.muc.peterus.muc_hw.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.datastore.locationEndpoint.LocationEndpoint;
import com.google.datastore.locationEndpoint.model.Location;

import java.io.IOException;

import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;

/**
 * Created by peterus on 21.11.2015.
 */
public class LocationEndpointBackupTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "LocationEndpointTask";
    private static LocationEndpoint mApiService = null;

    private String query = "SELECT *" +
            " FROM " + DBOpenHelper.TABLE_NAME + " AS loc" +
            " WHERE loc." + DBOpenHelper.BACKEDUP + " = 0";

    protected Void doInBackground(Void... params) {
        Log.d(TAG, "STARTED");
        if (mApiService == null) {
            LocationEndpoint.Builder builder = new LocationEndpoint.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://muci-ptr.appspot.com/_ah/api")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            mApiService = builder.build();
        }

        DBOpenHelper mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
        SQLiteDatabase writableDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor c = writableDatabase.rawQuery(query, null);

        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        String ownerEmail = settings.getString("email", "-1");

        while(c.moveToNext()){
            Log.d(TAG, "Backed up location:" + c.getLong(c.getColumnIndex(DBOpenHelper._ID)));
            Location tmp = new Location();
            tmp.setId(c.getLong(c.getColumnIndex(DBOpenHelper._ID)));
            tmp.setLabel(c.getString(c.getColumnIndex(DBOpenHelper.LABEL)));
            tmp.setLat(c.getFloat(c.getColumnIndex(DBOpenHelper.LAT)));
            tmp.setLng(c.getFloat(c.getColumnIndex(DBOpenHelper.LNG)));
            tmp.setOwnerEmail(ownerEmail);
            tmp.setTriggerId(c.getLong(c.getColumnIndex(DBOpenHelper.TRIGGER_ID)));
            try {
                mApiService.insertLocation(tmp).execute();
                // Set backedup to true:
                ContentValues cv = new ContentValues();
                cv.put(DBOpenHelper.BACKEDUP, 1);
                writableDatabase.update(DBOpenHelper.TABLE_NAME, cv, "_id = ?", new String[]{Long.toString(c.getLong(c.getColumnIndex(DBOpenHelper._ID)))});
            } catch (IOException e) {
                Log.d(TAG, "Backup failed:" + e.getMessage());
            }
        }
        c.close();
        writableDatabase.close();
        mDatabaseHelper.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(TAG, "Backup complete");
    }
}
