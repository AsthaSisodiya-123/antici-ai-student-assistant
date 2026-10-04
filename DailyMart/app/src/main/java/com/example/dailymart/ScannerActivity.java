package com.example.dailymart;

import android.app.ComponentCaller;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScannerActivity extends BaseActivity {

TextView tvData;
AppCompatButton btnTapToScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        tvData=findViewById(R.id.tvData);
        btnTapToScan=findViewById(R.id.btnTapToScan);
        btnTapToScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator=new IntentIntegrator(ScannerActivity.this);
                intentIntegrator.setPrompt("Scan a QR Code");
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.initiateScan();
                intentIntegrator.setTorchEnabled(true);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        Toast.makeText(this, "Result"+result.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Result Content"+result.getContents(), Toast.LENGTH_SHORT).show();

        if(result!=null && result.getContents()!=null){
            tvData.setText(result.getContents());
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}