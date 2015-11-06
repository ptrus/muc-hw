package fri.muc.peterus.muc_hw.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.activities.RegistrationActivity;

/**
 * Created by peterus on 23.10.2015.
 */
public class ConnectionMapFragment extends SupportMapFragment {
    public String TAG = "ConnectionMapFragment";
    private GoogleMap mMap;

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "IN CONNECTION MAP FRAGMENT.");

        SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, getActivity().MODE_PRIVATE);
        float workLat = settings.getFloat("workLat", 0);
        float workLng = settings.getFloat("workLng", 0);
        float sleepLat = settings.getFloat("sleepLat", 0);
        float sleepLng = settings.getFloat("sleepLng", 0);

        Log.d(TAG, "workLat:" + workLat + " workLng:" + workLng + "  sleepLat:" + sleepLat + "   sleepLng:" + sleepLng);

        mMap = super.getMap();
        if (mMap != null){
            MarkerOptions markerWork = new MarkerOptions();
            markerWork.title("Work");
            markerWork.position(new LatLng(workLat, workLng));

            MarkerOptions markerSleep = new MarkerOptions();
            markerSleep.title("Sleep");
            markerSleep.position(new LatLng(sleepLat, sleepLng));

            mMap.addMarker(markerWork);
            mMap.addMarker(markerSleep);
        }
    }



}
