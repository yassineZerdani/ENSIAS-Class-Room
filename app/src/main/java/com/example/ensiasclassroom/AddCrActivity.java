package com.example.ensiasclassroom;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.ensiasclassroom.databinding.ActivityAddCrBinding;
import com.example.ensiasclassroom.databinding.ActivityAddProfessorBinding;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddCrActivity extends AppCompatActivity {

    private ActivityAddCrBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.addProf.setOnClickListener(v -> {
            if(isValidServiceDetails()){
                addCR();
            }
            binding.addProf.setOnClickListener(d -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            });
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addCR(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> service = new HashMap<>();
        int hour = binding.heureDebut.getHour();
        int min = binding.heureDebut.getMinute();
        String time = hour+":"+min;
        service.put(Constants.KEY_CR_HOUR, time);
        service.put(Constants.KEY_CR_DESCR, binding.description.getText().toString());
        database.collection(Constants.KEY_COLLECTION_CR).add(service).addOnSuccessListener(documentReference -> {
            Intent intent = new Intent(getApplicationContext(), CoursListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).addOnFailureListener(exception -> {
            showToast(exception.getMessage());
        });
    }

    private Boolean isValidServiceDetails(){

        if(binding.description.getText().toString().trim().isEmpty()){
            showToast("Enter Service Name");
            return false;
        }
        else if(binding.description.getText().toString().trim().isEmpty()){
            showToast("Enter Service Description");
            return false;
        }
        else {
            return true;
        }
    }
}