package fri.muc.peterus.muc_hw.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import fri.muc.peterus.muc_hw.R;

/**
 * Created by peterus on 18.11.2015.
 */
public class SummaryWiFiAdapter extends CursorAdapter {
    public SummaryWiFiAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.summary_layout, parent, false);
        return parent;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // SSID, RSSID, LABEL, from cursor

    }
}
