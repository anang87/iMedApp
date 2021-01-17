package com.imedappagkn.imedapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imedappagkn.imedapp.MainActivity.firebase;

public class logCalories extends AppCompatActivity {
    private EditText caloriesInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_calories);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

    }

    public void logCaloriesValueClicked(View view) {
        caloriesInput = findViewById(R.id.caloriesInput);

        if (TextUtils.isEmpty(caloriesInput.getText().toString())) {
            caloriesInput.setError("No Value");
        }

        else {

            firebase.child("Users").child("TEST1").child("Nutrition").child("Calories").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int currentValue = snapshot.getValue(Integer.class);

                    EditText caloriesInput = findViewById(R.id.caloriesInput);
                    String rawCalValue = caloriesInput.getText().toString();
                    int calValue = Integer.parseInt(rawCalValue);

                    firebase.child("Users").child("TEST1").child("Nutrition").child("Calories").setValue(calValue + currentValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        Toast toast = Toast.makeText(this, "Logged", Toast.LENGTH_SHORT);
        toast.show();


        Intent intent = new Intent(this, nutritionActivity.class);
        startActivity(intent);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public void logNutritionHomeClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void scanFoodButtonClicked(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        InputImage image = InputImage.fromBitmap(bitmap, 90);

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(List<ImageLabel> imageLabels) {
                for (ImageLabel label : imageLabels) {
                    String text = label.getText();
                    float confidence = label.getConfidence();
                    int index = label.getIndex();

                    System.out.println(text);
                    System.out.println(confidence);

                    String foodLabelRaw = "Banana"; //imageResults.get(0); -> Hardcoded for design/testing purposes only

                    final int caloriesInFood = 100; // -> Would be determined through food database api in real implementation
                    // Hardcoded for design/testing/demo purposes only

                    // API call is below -> Commented out for demo purposes only

//                    URL url = null;
//                    try {
//                        url = new URL("https://api.edamam.com/api/food-database/v2/parser?ingr=" +foodLabelRaw + "&app_id={https://www.google.com/search?client=safari&rls=en&q=49229ed2378bacf3c3f9768b4c48aedc&ie=UTF-8&oe=UTF-8}&app_key={cd068bb3}");
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                    HttpURLConnection con = null;
//                    try {
//                        con = (HttpURLConnection) url.openConnection();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        con.setRequestMethod("GET");
//                    } catch (ProtocolException e) {
//                        e.printStackTrace();
//                    }

//                    Map<String, String> parameters = new HashMap<>();
//                    parameters.put("param1", "val");
//
//                    con.setDoOutput(true);
//                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
//                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//                    out.flush();
//                    out.close();


                    Toast.makeText(logCalories.this, "Logged " + caloriesInFood + " Calories For " + foodLabelRaw, Toast.LENGTH_LONG).show();

                    firebase.child("Users").child("TEST1").child("Nutrition").child("Calories").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int currentValue = snapshot.getValue(Integer.class);

                            firebase.child("Users").child("TEST1").child("Nutrition").child("Calories").setValue(caloriesInFood + currentValue);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed");
            }
        });



    }
}