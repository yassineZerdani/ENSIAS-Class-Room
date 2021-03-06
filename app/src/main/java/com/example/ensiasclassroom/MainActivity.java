package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.ensiasclassroom.auth.SignInActivity;
import com.example.ensiasclassroom.databinding.ActivityMainBinding;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
        loading(false);

    }

    private void setListeners(){

        binding.logout.setOnClickListener(v -> signOut());
        //binding.objectsCount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ObjectsActivity.class)));
        //binding.servicesCount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ServicesActivity.class)));
        binding.professers.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfesseursListActivity.class)));
        binding.cours.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CoursListActivity.class)));
        binding.groups.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), GroupListActivity.class)));
        binding.etudiant.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), EtudiantsListActivity.class)));
        String role = preferenceManager.getString(Constants.KEY_ROLE);
        if(role == "etudiant"){
            binding.absence.setVisibility(View.GONE);
            binding.groups.setVisibility(View.GONE);
        }
        else if(role == "professeur"){

        }
        else if(role == "admin"){

        }
    }

    private void loadUserDetails(){
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_ETUDIANT_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_PROFESSORS).document(
                preferenceManager.getString(Constants.KEY_PROFESSOR_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(
                        e -> showToast("unable to update token")
                );
    }


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void signOut(){
        showToast("signed out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_PROFESSORS).document(
                preferenceManager.getString(Constants.KEY_PROFESSOR_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("unable to sign out"));
    }
}