package com.tapsell.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ir.tapsell.tapsellvideosdk.developer.CheckCtaAvailabilityResponseHandler;
import ir.tapsell.tapsellvideosdk.developer.DeveloperInterface;

public class MainActivity extends AppCompatActivity {

    // tapsell application key obtained from developer dashboard (http://tapsell.ir/panel.html#/developer/dashboard)
    private static final String tapsellAppKey = "mpqoomqpdorkfpgtqflrgcgjoimlobgpgtgtfnoslgngeogljgfdbrmhtglmldaikdkled";

    // A request code for getting result of tapsell activities
    private static final int tapsellRequestCode = DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE;

    // Request code for checking whether the user has granted required permissions
    private static final int permissionsRequestCode = 123;

    private Button btnShowAd;
    private Button btnCheckCTAAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowAd = (Button) findViewById(R.id.btnShowAd);
        btnCheckCTAAvailability = (Button) findViewById(R.id.btnCheckCTAAvailability);

        btnShowAd.setEnabled(false);
        btnCheckCTAAvailability.setEnabled(false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int resultCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int resultReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (resultCoarseLocation == PackageManager.PERMISSION_GRANTED && resultReadPhoneState == PackageManager.PERMISSION_GRANTED)
            {
                onPermissionsGranted();
            }
            else {
                if( ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE) )
                {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, permissionsRequestCode);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    onPermissionsDenied();
                                    break;
                            }
                        }
                    };
                    new AlertDialog.Builder(this)
                            .setMessage("Tapsell requires permission to read your device Id and location for showing video ads.")
                            .setPositiveButton("OK", listener)
                            .setNegativeButton("Cancel", listener)
                            .create()
                            .show();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, permissionsRequestCode);
                }
            }
        } else {
            onPermissionsGranted();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == tapsellRequestCode)
        {
            boolean isConnected = data.getBooleanExtra(DeveloperInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE, false);
            boolean isAvailable = data.getBooleanExtra(DeveloperInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE, false);
            int award = data.getIntExtra(DeveloperInterface.TAPSELL_DIRECT_AWARD_RESPONSE, -1);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle( isConnected ? (isAvailable ? "Results" : "No ad available") : "No Network" )
                    .setMessage( isConnected ? (isAvailable ? (award>0 ? "Ad was shown, you got "+award+" points as reward. Yay!" : "Ad was shown but, unfortunately, you got no rewards... :-S") : "Unfortunately, No ad is available :-(" ) : "No network available, unable to show ad" )
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon( isConnected ? (isAvailable ? (award>0 ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert ) : android.R.drawable.ic_dialog_alert) : android.R.drawable.ic_dialog_alert)
                    .show();
            btnShowAd.setEnabled(true);
            System.err
                    .println(data
                            .hasExtra(DeveloperInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE));
            System.err
                    .println(data
                            .hasExtra(DeveloperInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE));
            System.err
                    .println(data
                            .hasExtra(DeveloperInterface.TAPSELL_DIRECT_AWARD_RESPONSE));
            System.err
                    .println(data
                            .getBooleanExtra(
                                    DeveloperInterface.TAPSELL_DIRECT_CONNECTED_RESPONSE, false));
            System.err
                    .println(data
                            .getBooleanExtra(
                                    DeveloperInterface.TAPSELL_DIRECT_AVAILABLE_RESPONSE, false));
            System.err
                    .println(data
                            .getIntExtra(
                                    DeveloperInterface.TAPSELL_DIRECT_AWARD_RESPONSE, -1));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == permissionsRequestCode)
        {
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onPermissionsGranted();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                onPermissionsDenied();
            }
            return;
        }
    }

    private void onPermissionsDenied()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        AlertDialog finishDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Tapsell requires permission to read your device Id and location for showing video ads. You can grant this permissions in your phone settings.")
                .setPositiveButton("OK", listener)
                .create();
        finishDialog.setCancelable(false);
        finishDialog.setCanceledOnTouchOutside(false);
        finishDialog.show();

    }

    private void onPermissionsGranted()
    {
        DeveloperInterface.getInstance(MainActivity.this).init(tapsellAppKey, MainActivity.this);

        btnShowAd.setEnabled(true);
        btnCheckCTAAvailability.setEnabled(true);

        btnCheckCTAAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setTitle("");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
                DeveloperInterface.getInstance(MainActivity.this)
                        .checkCtaAvailability(MainActivity.this,
                                DeveloperInterface.DEFAULT_MIN_AWARD,
                                DeveloperInterface.VideoPlay_TYPE_NON_SKIPPABLE,
                                new CheckCtaAvailabilityResponseHandler() {
                                    @Override
                                    public void onResponse(Boolean isConnected, Boolean isAvailable) {
                                        pDialog.dismiss();
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle( isConnected ? (isAvailable ? "CTA Available" : "CTA Unavailable") : "No Network" )
                                                .setMessage( isConnected ? (isAvailable ? "Ad is available ☻" : "Unfortunately, No ad is available" ) : "No network available" )
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setIcon( isConnected ? (isAvailable ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert) : android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                });
            }
        });

        btnShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowAd.setEnabled(false);
                DeveloperInterface.getInstance(MainActivity.this)
                        .showNewVideo(MainActivity.this,
                                tapsellRequestCode,
                                DeveloperInterface.DEFAULT_MIN_AWARD,
                                DeveloperInterface.VideoPlay_TYPE_NON_SKIPPABLE);
            }
        });
    }

}
