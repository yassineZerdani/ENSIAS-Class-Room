package com.example.ensiasclassroom.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.example.ensiasclassroom.MainActivity;
import com.example.ensiasclassroom.WelcomeActivity;
import com.example.ensiasclassroom.databinding.ActivitySignInBinding;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        });
        binding.signin.setOnClickListener(v -> {
            if(isValidSignInDetails()){
                signInProfessor();
            }
        });
    }


    public boolean isStudentUserRegistered() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        final boolean[] state = new boolean[1];

        database.collection(Constants.KEY_COLLECTION_ETUDIANT)
                .whereEqualTo(Constants.KEY_ETUDIANT_EMAIL, binding.emailSignin.getText().toString())
                .whereEqualTo(Constants.KEY_ETUDIANT_PASSWORD, binding.passwordSignin.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        state[0] = true;

                    }else{

                        state[0] = false;
                    }
                });
        return state[0];
    };

    public boolean isProfessorRegistered() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        final boolean[] state = new boolean[1];

        database.collection(Constants.KEY_COLLECTION_PROFESSORS)
                .whereEqualTo(Constants.KEY_PROFESSOR_EMAIL, binding.emailSignin.getText().toString())
                .whereEqualTo(Constants.KEY_PROFESSOR_PASSWORD, binding.passwordSignin.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        state[0] = true;

                    }else{

                        state[0] = false;
                    }
                });
        return state[0];

    };

    private void signIn(){

        if(isStudentUserRegistered() == true){
            signInEtudiant();
        }
        else if(isProfessorRegistered() == true){
            signInProfessor();
        }
    }


    private void signInEtudiant() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_ETUDIANT)
                .whereEqualTo(Constants.KEY_ETUDIANT_EMAIL, binding.emailSignin.getText().toString())
                .whereEqualTo(Constants.KEY_ETUDIANT_PASSWORD, binding.passwordSignin.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_ETUDIANT_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_ETUDIANT_FIRST_NAME, documentSnapshot.getString(Constants.KEY_ETUDIANT_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_ETUDIANT_LAST_NAME, documentSnapshot.getString(Constants.KEY_ETUDIANT_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_ETUDIANT_IMAGE, documentSnapshot.getString(Constants.KEY_ETUDIANT_IMAGE));
                        preferenceManager.putString(Constants.KEY_ROLE, documentSnapshot.getString(Constants.KEY_ROLE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        showToast("unable to sign in");
                    }
                });
    }

    private void signInProfessor() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_PROFESSORS)
                .whereEqualTo(Constants.KEY_PROFESSOR_EMAIL, binding.emailSignin.getText().toString())
                .whereEqualTo(Constants.KEY_PROFESSOR_PASSWORD, binding.passwordSignin.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_PROFESSOR_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_PROFESSOR_FIRST_NAME, documentSnapshot.getString(Constants.KEY_PROFESSOR_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_PROFESSOR_LAST_NAME, documentSnapshot.getString(Constants.KEY_PROFESSOR_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_PROFESSOR_IMAGE, documentSnapshot.getString(Constants.KEY_PROFESSOR_IMAGE));
                        preferenceManager.putString(Constants.KEY_ROLE, documentSnapshot.getString(Constants.KEY_ROLE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        showToast("unable to sign in");
                    }
                });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){

        if(binding.emailSignin.getText().toString().trim().isEmpty()){
            showToast("Enter Your Email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailSignin.getText().toString()).matches()){
            showToast("Email is Invalid");
            return false;
        }
        else if(binding.passwordSignin.getText().toString().trim().isEmpty()){
            showToast("Enter Your Password");
            return false;
        }
        else {
            return true;
        }
    }
}