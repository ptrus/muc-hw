package fri.muc.peterus.muc_hw.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.activities.MainActivity;
import fri.muc.peterus.muc_hw.activities.RegistrationActivity;


/**
 * Created by peterus on 23.10.2015.
 */

public class ConnectionMapFragment extends SupportMapFragment {
    public String TAG = "ConnectionMapFragment";
    private GoogleMap mMap;
    private MainActivity mMainActivity;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
                if (key.equals("wifiShow") && settings.getBoolean("wifiShow", false)) {
                    float wifiLat = settings.getFloat("wifiLat", -1);
                    float wifiLng = settings.getFloat("wifiLng", -1);
                    String wifiSSID = settings.getString("wifiSSID", "AP");

                    onWiFiSelected(wifiLat, wifiLng, wifiSSID);
                }
            }
        };
        SharedPreferences settings = mMainActivity.getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        settings.registerOnSharedPreferenceChangeListener(listener);
    }

    public void onResume() {
        super.onResume();

        SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        float workLat = settings.getFloat("workLat", -1);
        float workLng = settings.getFloat("workLng", -1);
        float sleepLat = settings.getFloat("sleepLat", -1);
        float sleepLng = settings.getFloat("sleepLng", -1);

        mMap = super.getMap();
        if (mMap != null){
            MarkerOptions markerWork = null;
            MarkerOptions markerSleep = null;

            if (workLat != -1 && workLng != -1) {
                markerWork = new MarkerOptions();

                markerWork.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_work));

                markerWork.title("Work");
                markerWork.position(new LatLng(workLat, workLng));
                mMap.addMarker(markerWork);
            }

            if (sleepLat != -1 && sleepLng != -1) {
                markerSleep = new MarkerOptions();

                markerSleep.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_home));

                markerSleep.title("Home");
                markerSleep.position(new LatLng(sleepLat, sleepLng));

                mMap.addMarker(markerSleep);
            }

            if (markerWork != null && markerSleep != null){
                LatLng center = new LatLng((workLat + sleepLat)/2, (workLng + sleepLng)/2);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 10.0f));
            }

        }
    }

    public void onPause() {
        SharedPreferences settings = mMainActivity.getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("wifiShow", false);
        editor.apply();

        super.onPause();
    }

    public void onWiFiSelected(float wifiLat, float wifiLng, String wifiSSID) {
        if (mMap != null){
            if (wifiLat != -1 && wifiLng != -1) {
                MarkerOptions markerWifi = new MarkerOptions();

                markerWifi.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_wifi));

                markerWifi.title(wifiSSID);
                markerWifi.position(new LatLng(wifiLat, wifiLng));

                mMap.addMarker(markerWifi);
            }
        }
    }
}
