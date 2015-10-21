package com.example.peterus.muc_hw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by peterus on 19.10.2015.
 */
public class RegistrationActivity extends AppCompatActivity {
    public static String ACC_PREFS = "AccountPrefs";

    private EditText occupationEdit;
    private EditText ageEdit;
    private RadioGroup sexRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText passwordRetypeEdit;
    private Button registerBtn;

    private boolean formErrors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        initFields();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegisterButton();
            }
        });
    }

    private void onClickRegisterButton() {
        formErrors = false;

        String occupationText = occupationEdit.getText().toString();
        String ageText = ageEdit.getText().toString();
        int sexId = sexRadioGroup.getCheckedRadioButtonId();

        String firstNameText = firstNameEdit.getText().toString();
        String lastNameText = lastNameEdit.getText().toString();
        String emailText = emailEdit.getText().toString();
        String passwordText = passwordEdit.getText().toString();
        String passwordRetypeText = passwordRetypeEdit.getText().toString();

        occupationEdit.setError(null);
        if (!Validation.isNotBlank(occupationText)){
            formErrors = true;
            occupationEdit.setError(getResources().getString(R.string.occupation_error));
        }

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

        ageEdit.setError(null);
        if (!Validation.isPositiveInteger(ageText)){
            formErrors = true;
            ageEdit.setError(getResources().getString(R.string.age_error));
        }

        passwordEdit.setError(null);
        if (!Validation.isCorrectLength(passwordText, 8, 16)){
            formErrors = true;
            passwordEdit.setError(getResources().getString(R.string.password_error));
        }

        passwordRetypeEdit.setError(null);
        if (!passwordText.equals(passwordRetypeText)){
            formErrors = true;
            passwordRetypeEdit.setError(getResources().getString(R.string.password_retype_error));
        }

        emailEdit.setError(null);
        if (!Validation.isEmailAddress(emailText)){
            formErrors = true;
            emailEdit.setError(getResources().getString(R.string.email_error));
        }

        maleRadioButton.setError(null);
        if (sexId == -1){
            formErrors = true;
            maleRadioButton.setError(getResources().getString(R.string.sex_error));
        }

        if (!formErrors){
            byte[] passwordMD5;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] passwordBytes = passwordText.getBytes();
                passwordMD5 = md.digest(passwordBytes);
            }
            catch(NoSuchAlgorithmException ne){
                Log.d("RegistrationActivity", "MD5 algorithm not available.");
                return;
            }
            UUID uuid = UUID.randomUUID();
            long unixTime = System.currentTimeMillis() / 1000L;

            SharedPreferences settings = getSharedPreferences(ACC_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor settingsEditor = settings.edit();
            settingsEditor.putString("firstName", firstNameText);
            settingsEditor.putString("lastName", lastNameText);
            settingsEditor.putString("occupation", occupationText);
            int age = Integer.parseInt(ageText);
            settingsEditor.putInt("age", age);
            String sex = sexId == R.id.female_button ? "F" : "M";
            settingsEditor.putString("sex", sex);
            settingsEditor.putString("email", emailText);
            settingsEditor.putString("md5", passwordMD5.toString());
            settingsEditor.putString("uuid", uuid.toString());
            settingsEditor.putLong("unixTime", unixTime);
            settingsEditor.commit();
        }
    }


    private void initFields() {
        occupationEdit = (EditText) findViewById(R.id.occupation_edit);
        ageEdit = (EditText) findViewById(R.id.age_edit);
        sexRadioGroup = (RadioGroup) findViewById(R.id.sex_radio_group);
        maleRadioButton = (RadioButton) findViewById(R.id.male_button);
        femaleRadioButton = (RadioButton) findViewById(R.id.female_button);
        firstNameEdit = (EditText) findViewById(R.id.first_name_edit);
        lastNameEdit = (EditText) findViewById(R.id.last_name_edit);
        emailEdit = (EditText) findViewById(R.id.email_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        passwordRetypeEdit = (EditText) findViewById(R.id.password_retype_edit);
        registerBtn = (Button) findViewById(R.id.register_button);
    }


}
