package fri.muc.peterus.muc_hw.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by peterus on 4.11.2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBOpenHelper";

    public final static String DATABASE_NAME = "sensing_db";
    public final static int DATABASE_VERSION = 3;
    public final static String TABLE_NAME = "locations";
    public final static String LAT = "lat";
    public final static String LNG = "lng";
    public final static String TRIGGER_ID = "triggerId";
    public final static String LABEL = "label";
    public final static String _ID = "_id";

    public final static String TABLE_NAME2 = "connectivities";
    public final static String RSSI = "RSSI";
    public final static String SSID = "SSID";
    public final static String BSSID = "BSSID";

    final private static String CREATE_CMD =
            "CREATE TABLE "+TABLE_NAME+" (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LAT + " REAL NOT NULL,"
                    + LNG + " REAL NOT NULL,"
                    + TRIGGER_ID + " INTEGER NOT NULL,"
                    + LABEL + " TEXT NOT NULL)";

    final private static String CREATE_CMD2 =
            "CREATE TABLE "+TABLE_NAME2+" (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + RSSI + " TEXT NOT NULL,"
                    + SSID + " TEXT NOT NULL,"
                    + BSSID + " TEXT NOT NULL,"
                    + TRIGGER_ID + " INTEGER NOT NULL)";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
        db.execSQL(CREATE_CMD2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
