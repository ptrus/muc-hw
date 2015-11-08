package fri.muc.peterus.muc_hw.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

        SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, getActivity().MODE_PRIVATE);
        float workLat = settings.getFloat("workLat", -1);
        float workLng = settings.getFloat("workLng", -1);
        float sleepLat = settings.getFloat("sleepLat", -1);
        float sleepLng = settings.getFloat("sleepLng", -1);

        Log.d(TAG, "workLat:" + workLat + " workLng:" + workLng + "  sleepLat:" + sleepLat + "   sleepLng:" + sleepLng);

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



}
