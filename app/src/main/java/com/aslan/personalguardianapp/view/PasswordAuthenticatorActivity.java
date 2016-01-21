package com.aslan.personalguardianapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.personalguardianapp.R;
import com.aslan.personalguardianapp.logic.TrackingService;
import com.aslan.personalguardianapp.util.Utility;


/**
 * The ACTIVITY of the application to authenticate the user to cancel SMS sending
 * this will be triggered when user touches cancel SMS notification
 *
 * @author Vishnuvathsasarma
 */

public class PasswordAuthenticatorActivity extends Activity {

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        showPasswordDialog();
    }

    private void showPasswordDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View loginView = inflater.inflate(
                R.layout.alert_activity_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loginView)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                EditText etxtPwd = (EditText) loginView
                                        .findViewById(R.id.etxtAlertPassword);
                                String password = etxtPwd.getText().toString();
                                Log.i("Pwd", password);
                                if (password.equalsIgnoreCase(Utility.getUserConf(getApplicationContext()).getPassword())) {
                                    if (TrackingService.smsCancelTimer != null) {
                                        TrackingService.smsCancelTimer.cancel();
                                        TrackingService.isMsgSent = false;
                                        TrackingService.deviationCount = 0;
                                        Log.i("Timer", "Cancelled");
                                        PasswordAuthenticatorActivity.this.finish();
                                    }
                                } else {
                                    showPasswordDialog();
                                    Toast.makeText(PasswordAuthenticatorActivity.this,
                                            "Password Incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // closes the activity
                                PasswordAuthenticatorActivity.this.finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
