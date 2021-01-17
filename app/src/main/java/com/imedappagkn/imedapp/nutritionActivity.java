package com.imedappagkn.imedapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import static com.imedappagkn.imedapp.MainActivity.firebase;

public class nutritionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );


        firebase.child("Users").child("TEST1").child("Nutrition").child("Calories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long calorieVal = snapshot.getValue(long.class);


                TextView calorieView = findViewById(R.id.textView21);

                calorieView.setText("" + calorieVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Read User's Nutritional Values From Firebase

        firebase.child("Users").child("TEST1").child("Nutrition").addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> nutritionalValues = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot val : snapshot.getChildren()) {
                    nutritionalValues.add(val.getValue().toString());
                }

                updateValues(nutritionalValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void nutritionHomeClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void logButtonClicked(View view){
        Intent intent = new Intent(this, logCalories.class);
        startActivity(intent);
    }



    public void updateValues(ArrayList<String> arrayList){
        int calories = Integer.parseInt(arrayList.get(0));
        int caloriesGoal = Integer.parseInt(arrayList.get(1));
        int water = Integer.parseInt(arrayList.get(2));
        int weight = Integer.parseInt(arrayList.get(3));

        // setNutritionValues()  -> Set text values on nutrition screen. For design purposes only at the moment
        System.out.println(calories);
        System.out.println(caloriesGoal);
        System.out.println(water);
        System.out.println(weight);

    }


}