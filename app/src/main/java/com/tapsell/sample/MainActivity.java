package com.tapsell.sample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ir.tapsell.tapsellvideosdk.developer.CheckCtaAvailabilityResponseHandler;
import ir.tapsell.tapsellvideosdk.developer.DeveloperInterface;

public class MainActivity extends AppCompatActivity {

    // tapsell application key obtained from developer dashboard (http://tapsell.ir/panel.html#/developer/dashboard)
    public static final String tapsellAppKey = "mpqoomqpdorkfpgtqflrgcgjoimlobgpgtgtfnoslgngeogljgfdbrmhtglmldaikdkled";

    // A request code for getting result of tapsell activities
    public static final int tapsellRequestCode = DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE;

    private Button btnShowAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeveloperInterface.getInstance(MainActivity.this).init(tapsellAppKey, MainActivity.this);

        Button btnCheckCTAAvailability = (Button) findViewById(R.id.btnCheckCTAAvailability);
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

        btnShowAd = (Button) findViewById(R.id.btnShowAd);
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
}
