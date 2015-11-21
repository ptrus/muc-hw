package fri.muc.peterus.muc_hw.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.adapters.SummaryListAdapter;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;
import fri.muc.peterus.muc_hw.tasks.AccessPointsCursorTask;
import fri.muc.peterus.muc_hw.tasks.RSSIQueryTask;

/**
 * Created by peterus on 22.10.2015.
 */

public class SummaryFragment extends Fragment implements RSSIQueryTask.TaskListener, AccessPointsCursorTask.TaskListener {
    private static final String TAG = "SummaryFragment";
    private TextView averageRSSIwork;
    private TextView averageRSSIhome;
    private ListView summaryConnection;
    private SummaryListAdapter summaryListAdapter;
    private SQLiteDatabase mReadableDatabase;
    private DBOpenHelper mDatabaseHelper;
    private Cursor mCursor;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        averageRSSIhome = (TextView) getView().findViewById(R.id.average_rssi_home_output);
        averageRSSIwork = (TextView) getView().findViewById(R.id.average_rssi_work_output);
        summaryConnection = (ListView) getView().findViewById(R.id.summary_listView);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCursor.moveToPosition(position);
                float lat = (float) mCursor.getDouble(4);
                float lng = (float) mCursor.getDouble(5);
                String ssid = mCursor.getString(2);

                SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("wifiLng", lng);
                editor.putFloat("wifiLat", lat);
                editor.putString("wifiSSID", ssid);
                editor.putBoolean("wifiShow", true);
                editor.apply();
                viewPager.setCurrentItem(0);
            }
        };
        summaryConnection.setOnItemClickListener(onItemClickListener);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCursor != null)
            mCursor.close();
        if (mReadableDatabase != null)
            mReadableDatabase.close();
        new RSSIQueryTask(this).execute("sleep");
        new RSSIQueryTask(this).execute("work");

        mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
        mReadableDatabase = mDatabaseHelper.getReadableDatabase();
        new AccessPointsCursorTask(this).execute(mReadableDatabase);

    }

    @Override
    public void beforeTaskStarts(double s) {

    }

    @Override
    public void onTaskCompleted(Pair<String, Double> s) {
        if (s.first.equals("sleep")){
            averageRSSIhome.setText(s.second.toString());
        }
        if (s.first.equals("work")){
            averageRSSIwork.setText(s.second.toString());
        }
    }

    @Override
    public void beforeTaskStarts() {

    }

    @Override
    public void onTaskCompleted(Cursor c) {
        if (c != null) {
            summaryListAdapter = new SummaryListAdapter(getActivity(), c, 0);
            summaryConnection.setAdapter(summaryListAdapter);
            mCursor = c;
        }
    }

    @Override
    public void onDestroy() {
        summaryConnection.setAdapter(null);
        if(mCursor != null) {
            mCursor.close();
        }
        mReadableDatabase.close();
        mDatabaseHelper.close();
        super.onDestroy();
    }

}
