package com.example.qrcodeapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScanQRActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    
    private DecoratedBarcodeView barcodeScannerView;
    private LinearLayout resultLayout;
    private TextView resultTextView;
    private MaterialButton copyButton;
    private MaterialButton shareResultButton;
    private MaterialButton scanAgainButton;
    
    private String scannedResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        initializeViews();
        setClickListeners();
        
        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkCameraPermission();
    }

    private void initializeViews() {
        barcodeScannerView = findViewById(R.id.barcodeScannerView);
        resultLayout = findViewById(R.id.resultLayout);
        resultTextView = findViewById(R.id.resultTextView);
        copyButton = findViewById(R.id.copyButton);
        shareResultButton = findViewById(R.id.shareResultButton);
        scanAgainButton = findViewById(R.id.scanAgainButton);
    }

    private void setClickListeners() {
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard();
            }
        });

        shareResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResult();
            }
        });

        scanAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanAgain();
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show explanation dialog
                showPermissionExplanationDialog();
            } else {
                // Request permission directly
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission already granted
            startScanning();
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.camera_permission_required))
                .setMessage("Camera permission is required to scan QR codes. Please grant permission to continue.")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    ActivityCompat.requestPermissions(ScanQRActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    finish();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                startScanning();
            } else {
                // Permission denied
                Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startScanning() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null) {
                    scannedResult = result.getText();
                    showResult(scannedResult);
                }
            }
        });
        
        barcodeScannerView.resume();
    }

    private void showResult(String result) {
        // Pause scanning
        barcodeScannerView.pause();
        
        // Show result
        resultTextView.setText(result);
        resultLayout.setVisibility(View.VISIBLE);
        
        // Hide scanner view
        barcodeScannerView.setVisibility(View.GONE);
    }

    private void scanAgain() {
        // Hide result
        resultLayout.setVisibility(View.GONE);
        
        // Show scanner view
        barcodeScannerView.setVisibility(View.VISIBLE);
        
        // Resume scanning
        barcodeScannerView.resume();
    }

    private void copyToClipboard() {
        if (scannedResult != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("QR Code Result", scannedResult);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareResult() {
        if (scannedResult != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, scannedResult);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Scan Result");
            startActivity(Intent.createChooser(shareIntent, "Share QR Code Result"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeScannerView != null && barcodeScannerView.getVisibility() == View.VISIBLE) {
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeScannerView != null) {
            barcodeScannerView.pause();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}