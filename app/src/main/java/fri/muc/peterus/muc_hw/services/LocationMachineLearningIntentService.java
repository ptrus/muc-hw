package fri.muc.peterus.muc_hw.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;
import si.uni_lj.fri.lrss.machinelearningtoolkit.MachineLearningManager;
import si.uni_lj.fri.lrss.machinelearningtoolkit.classifier.DensityClustering;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.ClassifierConfig;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.Constants;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.Feature;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.FeatureNominal;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.FeatureNumeric;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.Instance;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.MLException;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.Signature;
import si.uni_lj.fri.lrss.machinelearningtoolkit.utils.Value;

/**
 * Created by peterus on 6.11.2015.
 */
public class LocationMachineLearningIntentService extends IntentService {
    private final static String TAG = "LocationMachineLearning";
    private final static String QUERY = "SELECT * FROM " + DBOpenHelper.TABLE_NAME + " AS loc " +
            "WHERE loc.label IS ? OR " +
            " loc.label IS ?;";
    private MachineLearningManager mlManager;
    private Context mContext;
    private DensityClustering mClassifier;
    private DBOpenHelper mDatabaseHelper;

    public LocationMachineLearningIntentService() {
        super("LocationMachineLearningIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = ApplicationContext.getContext();
        mDatabaseHelper = new DBOpenHelper(mContext);
        initManager();
        initClustering();
        train();
    }

    private void initManager() {
        try {
            mlManager = MachineLearningManager.getMLManager(ApplicationContext.getContext());

        } catch (MLException e) {
            e.printStackTrace();
        }
    }

    private void initClustering(){
        Feature lng = new FeatureNumeric("lng");
        Feature lat = new FeatureNumeric("lat");

        ArrayList<String> locValues = new ArrayList<>();
        // Add values
        locValues.add("sleep");
        locValues.add("work");

        Feature loc = new FeatureNominal("loc", locValues);

        ArrayList<Feature> features = new ArrayList<>();
        features.add(lng);
        features.add(lat);
        features.add(loc);

        Signature sig = new Signature(features, features.size() -1);
        ClassifierConfig cfg = new ClassifierConfig();
        cfg.addParam(Constants.MAX_CLUSTER_DISTANCE, 0.5);
        cfg.addParam(Constants.MIN_INCLUSION_PERCENT, 0.5);

        try {
            mClassifier = (DensityClustering) mlManager.addClassifier(Constants.TYPE_DENSITY_CLUSTER, sig, cfg, "location");
        } catch (MLException e) {
            e.printStackTrace();
        }
    }

    private void train(){
        Log.d(TAG, "Started training");
        ArrayList<Instance> instanceQ = new ArrayList<>();

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(QUERY, new String[]{"sleep", "work"});
        Log.d(TAG, "Going through cursor");
        while(c.moveToNext()){
            int latId = c.getColumnIndex(DBOpenHelper.LAT);
            int lngId = c.getColumnIndex(DBOpenHelper.LNG);
            int labelId = c.getColumnIndex(DBOpenHelper.LABEL);

            double lat = c.getDouble(latId);
            double lng = c.getDouble(lngId);
            String label = c.getString(labelId);

            ArrayList<Value> instanceValues = new ArrayList<>();
            Value latVal = new Value(lat, Value.NUMERIC_VALUE);
            Value lngVal = new Value(lng, Value.NUMERIC_VALUE);
            Value labelVal = new Value(label, Value.NOMINAL_VALUE);
            instanceValues.add(latVal);
            instanceValues.add(lngVal);
            instanceValues.add(labelVal);
            Instance instance = new Instance(instanceValues);
            instanceQ.add(instance);
        }
        c.close();
        db.close();
        Log.d(TAG, "Starting training");
        try {
            mClassifier.train(instanceQ);
            Log.d(TAG, "Getting centroids");
            HashMap<String, double[]> centroids = mClassifier.getCentroids();

            double[] sleepCentroids = centroids.get("sleep");
            double[] workCentroids = centroids.get("work");

            Log.d(TAG, "Setting centroids");
            SharedPreferences settings = mContext.getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor settingsEditor = settings.edit();
            settingsEditor.putBoolean("locationClusteringTrained", true);
            settingsEditor.putFloat("sleepLat", (float) sleepCentroids[0]);
            settingsEditor.putFloat("sleepLng", (float) sleepCentroids[1]);
            settingsEditor.putFloat("workLat", (float) workCentroids[0]);
            settingsEditor.putFloat("workLng", (float) workCentroids[1]);
            settingsEditor.commit();
            Log.d(TAG, "Finished traniing");

        } catch (MLException e) {
            e.printStackTrace();
        }

    }

    public static boolean isLocationClusteringTrained(){
        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        return settings.getBoolean("locationClusteringTrained", false);
    }

    public static void forgetClustering(){
        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putBoolean("locationClusteringTrained", false);
        settingsEditor.putFloat("sleepLat", (float) -1);
        settingsEditor.putFloat("sleepLng", (float) -1);
        settingsEditor.putFloat("workLat", (float) -1);
        settingsEditor.putFloat("workLng", (float) -1);
        settingsEditor.commit();
    }
}
