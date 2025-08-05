package com.example.qrcodeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GenerateQRActivity extends AppCompatActivity {

    private TextInputEditText inputEditText;
    private MaterialButton generateButton;
    private ImageView qrCodeImageView;
    private LinearLayout buttonLayout;
    private MaterialButton saveButton;
    private MaterialButton shareButton;
    
    private Bitmap qrCodeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        initializeViews();
        setClickListeners();
        
        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        inputEditText = findViewById(R.id.inputEditText);
        generateButton = findViewById(R.id.generateButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        buttonLayout = findViewById(R.id.buttonLayout);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
    }

    private void setClickListeners() {
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQRCode();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });
    }

    private void generateQRCode() {
        String inputText = inputEditText.getText().toString().trim();
        
        if (TextUtils.isEmpty(inputText)) {
            Toast.makeText(this, getString(R.string.empty_input_error), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(inputText, BarcodeFormat.QR_CODE, 512, 512);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
            
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveQRCode() {
        if (qrCodeBitmap == null) {
            return;
        }

        try {
            String fileName = "QRCode_" + System.currentTimeMillis() + ".png";
            
            // For Android 10 and above, use MediaStore
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(this, getString(R.string.qr_saved_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                // For older Android versions
                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File qrCodeFile = new File(picturesDir, fileName);
                
                FileOutputStream outputStream = new FileOutputStream(qrCodeFile);
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                
                // Notify media scanner
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(qrCodeFile));
                sendBroadcast(mediaScanIntent);
                
                Toast.makeText(this, getString(R.string.qr_saved_success), Toast.LENGTH_SHORT).show();
            }
            
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.qr_save_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void shareQRCode() {
        if (qrCodeBitmap == null) {
            return;
        }

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), qrCodeBitmap, "QRCode", null);
            Uri uri = Uri.parse(path);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this QR Code!");
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing QR Code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}