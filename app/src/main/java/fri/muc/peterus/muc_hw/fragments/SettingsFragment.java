package fri.muc.peterus.muc_hw.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.StorageHelpers;
import fri.muc.peterus.muc_hw.helpers.Validation;
import fri.muc.peterus.muc_hw.receivers.LocationSensingAlarmReceiver;

/**
 * Created by peterus on 22.10.2015.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profileImageView;
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText emailEdit;
    private EditText samplingIntervalEdit;
    private Button confirmButton;

    private boolean formErrors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFields();

        confirmButton.setOnClickListener(onConfirmClick);

        profileImageView.setOnClickListener(onPhotoClick);
    }

    private View.OnClickListener onConfirmClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            formErrors = false;

            String firstNameText = firstNameEdit.getText().toString();
            String lastNameText = lastNameEdit.getText().toString();
            String emailText = emailEdit.getText().toString();
            String samplingIntervalText = samplingIntervalEdit.getText().toString();

            // Check for errors.
            firstNameEdit.setError(null);
            if (!Validation.isNotBlank(firstNameText)){
                formErrors = true;
                firstNameEdit.setError(getResources().getString(R.string.first_name_error));
            }

            lastNameEdit.setError(null);
            if (!Validation.isNotBlank(lastNameText)){
                formErrors = true;
                lastNameEdit.setError(getResources().getString(R.string.last_name_error));
            }

            emailEdit.setError(null);
            if (!Validation.isEmailAddress(emailText)){
                formErrors = true;
                emailEdit.setError(getResources().getString(R.string.email_error));
            }

            samplingIntervalEdit.setError(null);
            if (!Validation.isPositiveInteger(samplingIntervalText)){
                formErrors = true;
                samplingIntervalEdit.setError(getResources().getString(R.string.sampling_interval_error));
            }

            // Update preferences if no errors.
            if (!formErrors){
                SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, getActivity().MODE_PRIVATE);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putString("firstName", firstNameText);
                settingsEditor.putString("lastName", lastNameText);
                int samplingInterval = Integer.parseInt(samplingIntervalText);
                settingsEditor.putInt("samplingInterval", samplingInterval);
                settingsEditor.putString("email", emailText);
                settingsEditor.commit();
            }
        }
    };

    private View.OnClickListener onPhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(imageBitmap);
            String profileImagePath = saveProfilePicture(imageBitmap);

            SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor settingsEditor = settings.edit();
            settingsEditor.putString("profileImagePath", profileImagePath);
            settingsEditor.commit();
        }
    }

    private String saveProfilePicture(Bitmap imageBitmap){
        if (StorageHelpers.isExternalStorageWritable()){
            File f = StorageHelpers.getAlbumStorageDir("MUC Profile");
            try {
                FileOutputStream out = new FileOutputStream(f);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e("SettingsFragment", "Profile picture not saved.");
            }
        }
        else{
            String fileName = getActivity().getFilesDir() + "/profilePic";
            try {
                FileOutputStream out = new FileOutputStream(new File(fileName));
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                return fileName;
            } catch (IOException e) {
                Log.e("SettingsFragment", "Profile picture not saved.");
            }
        }
        return "";
    }


    private void initFields() {
        // Init fields.
        profileImageView = (ImageView) getView().findViewById(R.id.settings_profile_imageView);
        firstNameEdit = (EditText) getView().findViewById(R.id.settings_first_name_edit);
        lastNameEdit = (EditText) getView().findViewById(R.id.settings_last_name_edit);
        emailEdit = (EditText) getView().findViewById(R.id.settings_email_edit);
        samplingIntervalEdit = (EditText) getView().findViewById(R.id.settings_sampling_interval_edit);
        confirmButton = (Button) getView().findViewById(R.id.settings_confirm_button);

        // Get stored data.
        SharedPreferences settings = getActivity().getSharedPreferences(RegistrationActivity.ACC_PREFS, getActivity().MODE_PRIVATE);
        String firstName = settings.getString("firstName", "Default Name");
        String lastName = settings.getString("lastName", "Default Name");
        String email = settings.getString("email", "default@email.com");
        int samplingInterval = settings.getInt("samplingInterval", -1);
        String profileImagePath = settings.getString("profileImagePath", "");

        // Update fields.
        firstNameEdit.setText(firstName);
        lastNameEdit.setText(lastName);
        emailEdit.setText(email);
        if (samplingInterval != -1)
            samplingIntervalEdit.setText(Integer.toString(samplingInterval));

        if (!profileImagePath.equals("")) {
            Bitmap profileImage = StorageHelpers.getProfilePicture(profileImagePath);
            profileImageView.setImageBitmap(profileImage);
        }

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("samplingInterval")) {
                    Toast.makeText(ApplicationContext.getContext(), "On samplingIntervalChanged", Toast.LENGTH_LONG).show();
                    LocationSensingAlarmReceiver.startAlarm(ApplicationContext.getContext());
                }
            }
        };
        settings.registerOnSharedPreferenceChangeListener(listener);
    }
}
