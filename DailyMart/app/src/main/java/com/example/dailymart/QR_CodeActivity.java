package com.example.dailymart;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QR_CodeActivity extends BaseActivity {
    ImageView ivScanner;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        ivScanner=findViewById(R.id.ivScanner);

        try{
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.encodeBitmap(preferences.getString("username",""),
                    BarcodeFormat.QR_CODE,400,400);
            ivScanner.setImageBitmap(bitmap);
        } catch (Exception e) {

            Toast.makeText(this, ""+e.toString(), Toast.LENGTH_SHORT).show();
        }


    }
}