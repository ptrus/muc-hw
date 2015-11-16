package fri.muc.peterus.muc_hw.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by peterus on 4.11.2015.
 */
public class LocationsSQLiteOpenHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "locations_db";
    public final static int DATABASE_VERSION = 1;
    public final static String TABLE_NAME = "locations";
    public final static String LAT = "lat";
    public final static String LNG = "lng";
    public final static String TRIGGER_ID = "triggerId";
    public final static String LABEL = "label";
    public final static String _ID = "_id";

    final private static String CREATE_CMD =
            "CREATE TABLE "+TABLE_NAME+" (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LAT + " REAL NOT NULL,"
                    + LNG + " REAL NOT NULL,"
                    + TRIGGER_ID + " INTEGER NOT NULL,"
                    + LABEL + " TEXT NOT NULL)";

    public LocationsSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
