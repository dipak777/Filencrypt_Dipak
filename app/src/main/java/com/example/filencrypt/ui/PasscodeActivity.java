package com.example.filencrypt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.filencrypt.R;
import com.example.filencrypt.encryption.Cryptography;

public class PasscodeActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    public static String passcode;

    private EditText passcodeEditText;
    private EditText passcodeRepeatEditText;
    private SharedPreferences preferences;
    private boolean passcodeSet;

    static boolean ensurePasscode(Activity callee) {
        if (PasscodeActivity.passcode != null) return true;
        Intent intent = new Intent(callee, PasscodeActivity.class);
        callee.startActivityForResult(intent, 0);
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);


        this.preferences = this.getSharedPreferences("filencrypt", Context.MODE_PRIVATE);

        this.passcodeEditText = findViewById(R.id.passcode_edit_text);
        this.passcodeEditText.setOnEditorActionListener(this);

        this.passcodeRepeatEditText = findViewById(R.id.passcode_repeat_edit_text);
        this.passcodeRepeatEditText.setOnEditorActionListener(this);

        this.passcodeSet = this.preferences.contains("passcode");

        if (!this.passcodeSet) {
            this.passcodeEditText.setHint("Choose passcode");
            this.passcodeEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            this.passcodeRepeatEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false;

        String passcode = this.passcodeEditText.getText().toString();
        if (passcode.length() == 0) {
            this.passcodeRepeatEditText.setError("Input passcode.");
            return true;
        }

        if (this.passcodeSet && !verifyPasscode(passcode)) {
            this.passcodeEditText.setError("Wrong passcode.");
            this.passcodeEditText.setText("");
            return true;
        } else if (!this.passcodeSet) {
            String confirmPasscode = this.passcodeRepeatEditText.getText().toString();
            if (!passcode.equals(confirmPasscode)) {
                this.passcodeRepeatEditText.setError("Passcodes do not match.");
                return true;
            }
            storePasscode(passcode);
        }

        setResult(RESULT_OK);
        finishAfterTransition();
        return true;
    }

    private void storePasscode(String passcode) {
        Pair<String, String> newHash = Cryptography.hash(passcode, null);

        this.preferences
                .edit()
                .putString("passcode", newHash.first)
                .putString("salt", newHash.second)
                .commit();

        PasscodeActivity.passcode = passcode;
    }

    private boolean verifyPasscode(String passcode) {
        String currentHash = this.preferences.getString("passcode", null);
        String salt = this.preferences.getString("salt", null);
        Pair<String, String> newHash = Cryptography.hash(passcode, salt);

        if (!currentHash.equals(newHash.first)) return false;

        PasscodeActivity.passcode = passcode;
        return true;
    }
}
