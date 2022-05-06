package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.ensiasclassroom.auth.SignInActivity;
import com.example.ensiasclassroom.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    public ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.etudiant.setOnClickListener( v -> startActivity(new Intent(getApplicationContext(), com.example.ensiasclassroom.auth.etudiant.SignUpActivity.class)));
        binding.professeur.setOnClickListener( v -> startActivity(new Intent(getApplicationContext(), com.example.ensiasclassroom.auth.professeur.SignUpActivity.class)));
        binding.welcomSignin.setOnClickListener( v -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
    }
}