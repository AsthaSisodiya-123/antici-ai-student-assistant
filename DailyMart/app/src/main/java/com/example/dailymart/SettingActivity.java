package com.example.dailymart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class SettingActivity extends BaseActivity {
    ImageView ivTextDetectionImage;
    Button btnCaptureImage;
    TextView tvExtractText;
    Bitmap bitMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        ivTextDetectionImage=findViewById(R.id.ivTextDectionImage);
        btnCaptureImage=findViewById(R.id.btnCaptureImage);
        tvExtractText=findViewById(R.id.tvExtractText);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},999);
        }
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,999);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==999 && resultCode==RESULT_OK && data!=null)
        {
            bitMap=(Bitmap)data.getExtras().get("data");
            ivTextDetectionImage.setImageBitmap(bitMap);
            runTextRecogination(bitMap);
        }
    }

    private void runTextRecogination(Bitmap bitMap) {
        InputImage image=InputImage.fromBitmap(bitMap,0);
        TextRecognizer recognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image).addOnSuccessListener(this::processTextRecogitionResult).addOnFailureListener(e->
                Toast.makeText(this,""+e.toString(),Toast.LENGTH_SHORT).show());
    }

    private void processTextRecogitionResult(Text text) {

        StringBuilder stringBuilder=new StringBuilder();
        for(Text.TextBlock block : text.getTextBlocks())
        {
            stringBuilder.append(block.getText()).append("\n");
        }
        tvExtractText.setText(stringBuilder.toString());
    }
}