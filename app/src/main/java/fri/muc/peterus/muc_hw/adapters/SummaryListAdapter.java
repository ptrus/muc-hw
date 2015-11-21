package fri.muc.peterus.muc_hw.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import java.text.DecimalFormat;

import fri.muc.peterus.muc_hw.R;

/**
 * Created by peterus on 20.11.2015.
 */
public class SummaryListAdapter extends CursorAdapter {
    public SummaryListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_ap, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView ssid = (TextView) view.findViewById(R.id.ap_ssid);
        TextView rssi = (TextView) view.findViewById(R.id.ap_rssi);
        TextView label = (TextView) view.findViewById(R.id.ap_label);

        String ssidStr = cursor.getString(2);
        double rssiDb = cursor.getDouble(1);
        String labelStr = cursor.getString(3);

        ssid.setText(ssidStr);
        DecimalFormat df = new DecimalFormat("#.00");
        rssi.setText(df.format(rssiDb));
        label.setText(labelStr);
    }
}
