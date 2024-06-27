package com.example.maizedisease;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maizedisease.ml.MaizeDiseaseModel;
import com.google.android.material.navigation.NavigationView;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final float CONFIDENCE_THRESHOLD = 0.80f;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1 MB

    private ImageView imageView, backButton, logoutButton;
    private LinearLayout messageLayout, home, info, profile, view;
    private TextView resultTextView, signup;
    private Button predictButton;

    private Bitmap imageBitmap;
    private MaizeDiseaseModel model;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Void> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.outputTextView);
        predictButton = findViewById(R.id.predictButton);
        messageLayout = findViewById(R.id.messagingLayout);
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);
        home = findViewById(R.id.homelayout);
        view = findViewById(R.id.viewlayout);
        info = findViewById(R.id.infolayout);
        profile = findViewById(R.id.profilelayout);

        Button captureButton = findViewById(R.id.captureButton);
        Button uploadButton = findViewById(R.id.button);
        predictButton.setEnabled(false);

        captureButton.setOnClickListener(v -> captureImage());
        uploadButton.setOnClickListener(v -> pickImage());
        predictButton.setOnClickListener(v -> predictDisease());
        messageLayout.setOnClickListener(v -> messaging());
        backButton.setOnClickListener(v -> onBack());
        logoutButton.setOnClickListener(v -> logout());
        home.setOnClickListener(v -> Home());
        // Initially disable the click listener for 'view'
        view.setOnClickListener(null);
        profile.setOnClickListener(v -> Profile());
        info.setOnClickListener(v -> information());

        // Set action bar color
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lightgreen)));
        }

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightgreen));
        }

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            imageBitmap = compressImage(imageBitmap); // Compress the image
                            imageView.setImageBitmap(imageBitmap);
                            predictButton.setEnabled(true); // Enable predict button after image is loaded
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image", e);
                            showErrorToast("Error loading image");
                        }
                    }
                });

        captureImageLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        imageBitmap = compressImage(bitmap); // Compress the image
                        imageView.setImageBitmap(imageBitmap);
                        predictButton.setEnabled(true); // Enable predict button after image is captured
                    }
                });

        // Request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }

        // Monitor changes in resultTextView to conditionally enable click listener for 'view'
        Set<String> validOutputs = new HashSet<>(Arrays.asList("blight", "common rust", "gray leaf spot"));
        resultTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text change
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the new text is one of the valid outputs
                if (validOutputs.contains(s.toString().toLowerCase())) {
                    // Enable the click listener if the text is valid
                    view.setOnClickListener(v -> results());
                } else {
                    // Disable the click listener if the text is not valid
                    view.setOnClickListener(null);
                }
            }
        });
    }

    private void information() {
        Intent intent4 = new Intent(MainActivity.this, info.class);
        startActivity(intent4);
    }

    private void Profile() {
        Intent intent4 = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent4);
    }

    private void Home() {
        Intent intent4 = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent4);
    }

    private void results() {
        Intent intent4 = new Intent(MainActivity.this, ResultsActivity.class);
        startActivity(intent4);
    }

    private void back() {
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            captureImageLauncher.launch(null);
        } else {
            requestCameraPermission();
        }
    }

    private void pickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*");
        } else {
            requestStoragePermission();
        }
    }

    private void predictDisease() {
        if (imageBitmap != null) {
            resultTextView.setText("Processing...");
            predictButton.setEnabled(false); // Disable predict button during processing

            new Thread(() -> {
                try {
                    model = MaizeDiseaseModel.newInstance(MainActivity.this);

                    // Resize the input image to match the expected input shape
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 256, 256, true);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(resizedBitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    MaizeDiseaseModel.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    int[] outputArray = outputFeature0.getIntArray();
                    int outputIndex = 0;
                    float maxConfidence = outputFeature0.getFloatArray()[0];

                    for (int i = 1; i < outputArray.length; i++) {
                        if (outputFeature0.getFloatArray()[i] > maxConfidence) {
                            maxConfidence = outputFeature0.getFloatArray()[i];
                            outputIndex = i;
                        }
                    }

                    String outputLabel = getLabelForIndex(outputIndex);

                    float finalMaxConfidence = maxConfidence;
                    runOnUiThread(() -> {
                        predictButton.setEnabled(true); // Enable predict button after processing
                        if (finalMaxConfidence >= CONFIDENCE_THRESHOLD) {
                            resultTextView.setText(outputLabel);

                            // Create an ArrayList of FungicideModel objects based on the predicted disease
                            ArrayList<FungicideModel> fungicideList = new ArrayList<>();
                            switch (outputLabel) {
                                case "Blight":
                                    fungicideList.add(new FungicideModel(R.drawable.blight, getString(R.string.blight), getString(R.string.mancoflo_esc)));
                                    break;
                                case "Common Rust":
                                    fungicideList.add(new FungicideModel(R.drawable.spot, getString(R.string.common_rust), getString(R.string.amistartop)));
                                    break;
                                case "Gray Leaf Spot":
                                    fungicideList.add(new FungicideModel(R.drawable.spot, getString(R.string.gray_leaf_spot), getString(R.string.amistartop)));
                                    break;
                            }
                            // Start ResultsActivity with prediction result and fungicide recommendations
                            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                            intent.putExtra("ResultActivity", outputLabel);
                            intent.putParcelableArrayListExtra("fungicideList", fungicideList);
                            startActivity(intent);
                        } else {
                            resultTextView.setText("Please try again with a different image.");
                        }
                    });
                } catch (Exception e) {
                    handlePredictionError(e);
                } finally {
                    if (model != null) {
                        model.close();
                    }
                }
            }).start();
        } else {
            showErrorToast("Please upload an image first");
        }
    }

    private String getLabelForIndex(int index) {
        switch (index) {
            case 0:
                return "Blight";
            case 1:
                return "Common Rust";
            case 2:
                return "Gray Leaf Spot";
            case 3:
                return "Healthy";
            default:
                return "Unknown";
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                showErrorToast("Storage permission denied. Cannot access images.");
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                showErrorToast("Camera permission denied. Cannot capture images.");
            }
        }
    }

    private Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > MAX_IMAGE_SIZE && options > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        byte[] data = baos.toByteArray();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void handlePredictionError(Exception e) {
        runOnUiThread(() -> {
            predictButton.setEnabled(true);
            resultTextView.setText("Error predicting disease: " + e.getMessage());
            Log.e(TAG, "Error predicting disease", e);
        });
    }

    private void messaging() {
        Intent intent = new Intent(MainActivity.this, DisplayUsers.class);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        startActivity(intent);
    }

    public void onBack() {
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        startActivity(intent);
    }
}
