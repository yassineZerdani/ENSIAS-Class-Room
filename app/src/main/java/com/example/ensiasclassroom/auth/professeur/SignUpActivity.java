package com.example.ensiasclassroom.auth.professeur;

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
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.ensiasclassroom.MainActivity;
import com.example.ensiasclassroom.databinding.ActivitySignUpBinding;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.signup.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signUp();
            }
            binding.textAddImage.setOnClickListener(d -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            });
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_PROFESSOR_FIRST_NAME, binding.firstNameSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_LAST_NAME, binding.lastNameSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_DEPARTEMENT, binding.departementSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_PHONE, binding.phoneSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_EMAIL, binding.emailSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_PASSWORD, binding.passwordSignup.getText().toString());
        user.put(Constants.KEY_PROFESSOR_IMAGE, encodedImage);
        user.put(Constants.KEY_ROLE, "professeur");
        database.collection(Constants.KEY_COLLECTION_PROFESSORS).add(user).addOnSuccessListener(documentReference -> {
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
            preferenceManager.putString(Constants.KEY_PROFESSOR_ID, documentReference.getId());
            preferenceManager.putString(Constants.KEY_PROFESSOR_FIRST_NAME, binding.firstNameSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_LAST_NAME, binding.lastNameSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_DEPARTEMENT, binding.departementSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_EMAIL, binding.emailSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_PASSWORD, binding.passwordSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_PHONE, binding.phoneSignup.getText().toString());
            preferenceManager.putString(Constants.KEY_PROFESSOR_IMAGE, encodedImage);
            preferenceManager.putString(Constants.KEY_ROLE, "professeur");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).addOnFailureListener(exception -> {
            showToast(exception.getMessage());
        });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() + previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("Select Profile Image");
            return false;
        }
        else if(binding.firstNameSignup.getText().toString().trim().isEmpty()){
            showToast("Enter Your First Name");
            return false;
        }
        else if(binding.lastNameSignup.getText().toString().trim().isEmpty()){
            showToast("Enter Your Last Name");
            return false;
        }
        else if(binding.emailSignup.getText().toString().trim().isEmpty()){
            showToast("Enter Your Email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailSignup.getText().toString()).matches()){
            showToast("Email is Invalid");
            return false;
        }
        else if(binding.passwordSignup.getText().toString().trim().isEmpty()){
            showToast("Enter Your Password");
            return false;
        }
        else if(binding.password2Signup.getText().toString().trim().isEmpty()){
            showToast("Confirm Your Password");
            return false;
        }
        else if(!binding.password2Signup.getText().toString().equals(binding.passwordSignup.getText().toString())){
            showToast("Password Not Confirmed");
            return false;
        }
        else {
            return true;
        }
    }
}