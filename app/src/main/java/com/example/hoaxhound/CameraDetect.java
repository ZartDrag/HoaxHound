package com.example.hoaxhound;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
public class CameraDetect extends AppCompatActivity {

    private ImageView ImgBox;
    private EditText TextBox;
    private ImageView captureBtn;
    // variable for our image bitmap.
    private Bitmap imageBitmap;
    private ProgressBar Loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detect);

        ImgBox = findViewById(R.id.pic_view);
        TextBox = findViewById(R.id.detected_text);
        captureBtn = findViewById(R.id.capture_button);
        LinearLayout predictBtn = findViewById(R.id.predict_button);

        captureBtn.setOnClickListener(view -> checkCameraPermission());
        Loading = findViewById(R.id.loading_bar);
        predictBtn.setOnClickListener(v -> predictText());
    }

//    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        // on below line we are calling a start activity
        // for result method to get the image captured.
        try {
            // in the method we are displaying an intent to capture our image.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);  //Deprecated Code
            CameraActivityResultLauncher.launch(takePictureIntent); //New Code
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Application Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,}, 1);
        } else
            dispatchTakePictureIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //New Code
    ActivityResultLauncher<Intent> CameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // on below line we are getting
                        // data from our bundles. .
                        assert result.getData() != null;
                        Bundle extras = result.getData().getExtras();
                        assert extras != null;
                        imageBitmap = (Bitmap) extras.get("data");

                        // below line is to set the
                        // image bitmap to our image.
                        ImgBox.setImageBitmap(imageBitmap);
                        captureBtn.setImageResource(R.drawable.recapture_icon);
                        detectTxt();
                    }
                    Log.i("TAG", String.valueOf(result));
                }
            });


    private void detectTxt() {
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image bitmap.
        try {

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

            // below line is to create a variable for detector and we
            // are getting vision text detector from our firebase vision.
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            // adding on success listener method to detect the text from image.
            // calling a method to process
            // our text after extracting.
            detector.processImage(image).addOnSuccessListener(this::processTxt).addOnFailureListener(e -> {
                // handling an error listener.
                Toast.makeText(CameraDetect.this, "Fail to detect the text from image..", Toast.LENGTH_SHORT).show();
            });
        } catch(NullPointerException ex){
            Toast.makeText(CameraDetect.this, "No Image Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void processTxt(FirebaseVisionText text) {
        // below line is to create a list of vision blocks which
        // we will get from our firebase vision text.
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();

        // checking if the size of the
        // block is not equal to zero.
        if (blocks.size() == 0) {
            // if the size of blocks is zero then we are displaying
            // a toast message as no text detected.
            Toast.makeText(CameraDetect.this, "No Text ", Toast.LENGTH_LONG).show();
            return;
        }
        // extracting data from each block using a for loop.
        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
            // below line is to get text
            // from each block.
            String txt = block.getText();

            // below line is to set our
            // string to our text view.
            TextBox.setText(txt);
        }
    }

    private void predictText(){
        Loading.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        Call<ApiResponseModel> call = apiService.postData("dummy");
        //send data as arguments here

        TextView ResultTextBox = findViewById(R.id.result_text_box);
        call.enqueue(new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseModel> call, @NonNull Response<ApiResponseModel> response) {
                if (response.isSuccessful()) {
                    ApiResponseModel data = response.body();
                    // Process the response data
                    assert data != null;
                    String text = "The News Article is " + data.getResponse();
                    ResultTextBox.setText(text);
                    Loading.setVisibility(View.GONE);
                } else {
                    // Handle error
                    Log.i("API Error", "No Response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                // Handle failure
                Log.i("API Error", "Api Error");
            }
        });
    }
}