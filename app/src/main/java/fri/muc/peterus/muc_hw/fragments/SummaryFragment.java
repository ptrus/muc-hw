package fri.muc.peterus.muc_hw.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.tasks.RSSIQueryTask;

/**
 * Created by peterus on 22.10.2015.
 */
public class SummaryFragment extends Fragment implements RSSIQueryTask.TaskListener {
    private static final String TAG = "SummaryFragment";
    private TextView averageRSSIwork;
    private TextView averageRSSIhome;
    private ListView summaryConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        averageRSSIhome = (TextView) getView().findViewById(R.id.average_rssi_home_output);
        averageRSSIwork = (TextView) getView().findViewById(R.id.average_rssi_work_output);

        new RSSIQueryTask(this).execute("sleep");
        new RSSIQueryTask(this).execute("work");
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
}
