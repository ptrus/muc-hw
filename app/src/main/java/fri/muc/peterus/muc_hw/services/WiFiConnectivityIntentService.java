package fri.muc.peterus.muc_hw.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.util.Log;

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;
import fri.muc.peterus.muc_hw.helpers.SensingTriggerHelper;
import fri.muc.peterus.muc_hw.helpers.WiFiHelper;

/**
 * Created by peterus on 16.11.2015.
 */
public class WiFiConnectivityIntentService extends IntentService {
    private String TAG = "WiFiConnectivityIntentService";
    private DBOpenHelper mDatabaseHelper;

    public WiFiConnectivityIntentService() {
        super("WiFiConnectivityIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
        getConnectionData();
    }

    private void getConnectionData(){
        Context context = ApplicationContext.getContext();

        if (WiFiHelper.isConnected(context)) {
            WifiInfo wifiInfo = WiFiHelper.getWifiInfo(context);
            String SSID = wifiInfo.getSSID();
            String BSSID = wifiInfo.getBSSID();
            int RSSI = wifiInfo.getRssi();
            long triggerId = SensingTriggerHelper.getSensingTriggerId();
            recordConnectionData(SSID, BSSID, RSSI, triggerId);
        }
    }

    private void recordConnectionData(String SSID, String BSSID, int RSSI, long triggerId){
        Log.d(TAG, "Storing:"+ SSID + " " + BSSID + " " + RSSI + " " + triggerId);
        SQLiteDatabase writableDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.SSID, SSID);
        values.put(DBOpenHelper.BSSID, BSSID);
        values.put(DBOpenHelper.RSSI, RSSI);
        values.put(DBOpenHelper.TRIGGER_ID, triggerId);
        writableDatabase.insert(DBOpenHelper.TABLE_NAME2, null, values);
        writableDatabase.close();
    }
}
