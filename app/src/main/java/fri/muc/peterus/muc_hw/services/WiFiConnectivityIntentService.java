package fri.muc.peterus.muc_hw.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;

/**
 * Created by peterus on 16.11.2015.
 */
public class WiFiConnectivityIntentService extends IntentService {
    private String TAG = "WiFiConnectivityIntentService";

    public WiFiConnectivityIntentService() {
        super("WiFiConnectivityIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getConnectionData();
    }

    private void getConnectionData(){


        Context context = ApplicationContext.getContext();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d(TAG, "Is conencted:" + isWiFiConnected());
        if (isWiFiConnected()) {
            String SSID = wifiInfo.getSSID();
            String BSSID = wifiInfo.getBSSID();
            int RSSI = wifiInfo.getRssi();
            recordConnectionData(SSID, BSSID, RSSI);
            Log.d(TAG, "ConnectionData: " + SSID + " " + BSSID + " " + RSSI);
        }
    }

    private boolean isWiFiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void recordConnectionData(String SSID, String BSSID, int RSSI){

    }
}
