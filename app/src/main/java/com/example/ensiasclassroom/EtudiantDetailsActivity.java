package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.ensiasclassroom.databinding.ActivityEtudiantDetailsBinding;
import com.example.ensiasclassroom.models.Etudiant;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EtudiantDetailsActivity extends AppCompatActivity {

    private ActivityEtudiantDetailsBinding binding;
    private Etudiant etudiant;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEtudiantDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        loadServiceDetails();

        String role = preferenceManager.getString(Constants.KEY_ROLE);

        if(role.equals("admin")){
            binding.delete.setVisibility(View.VISIBLE);
        }

        binding.sendMail.setOnClickListener( v -> sendEmail());
        binding.sendSMS.setOnClickListener( v -> sendSMS());
        binding.sendCall.setOnClickListener( v -> sendCall());
        binding.delete.setOnClickListener(v -> {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_ETUDIANT).document(etudiant.id).delete();
            Intent intent = new Intent(getApplicationContext(), EtudiantsListActivity.class);
            startActivity(intent);
            finish();
        });

    }



    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadServiceDetails(){
        etudiant = (Etudiant) getIntent().getSerializableExtra(Constants.KEY_ETUDIANT);
        binding.imageService.setImageBitmap(getBitmapFromEncodedString(etudiant.photo));
        binding.textName.setText(etudiant.prenom+" "+etudiant.nom);
        binding.negociate.setOnClickListener(v -> {
            Etudiant user = getUser(etudiant.id);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(Constants.KEY_ETUDIANT, user);
            startActivity(intent);
            finish();
        });
    }

    private Etudiant getUser(String userId){
        Etudiant user = new Etudiant();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ETUDIANT)
                .get()
                .addOnCompleteListener(task -> {

                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (userId.equals(queryDocumentSnapshot.getId())) {
                            continue;
                        }

                        user.nom = queryDocumentSnapshot.getString(Constants.KEY_ETUDIANT_FIRST_NAME);
                        //user.mail = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_EMAIL);
                        user.photo = queryDocumentSnapshot.getString(Constants.KEY_ETUDIANT_IMAGE);
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                        user.id = queryDocumentSnapshot.getId();

                    }
                });
        return user;
    }


    private void getUsers(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ETUDIANT)
                .get()
                .addOnCompleteListener(task -> {

                    String currentUserId = preferenceManager.getString(Constants.KEY_ETUDIANT_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Etudiant> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            if(binding.textAuthor.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            Etudiant user = new Etudiant();
                            user.nom = queryDocumentSnapshot.getString(Constants.KEY_ETUDIANT_FIRST_NAME);
                            user.prenom = queryDocumentSnapshot.getString(Constants.KEY_ETUDIANT_LAST_NAME);
                            user.photo = queryDocumentSnapshot.getString(Constants.KEY_ETUDIANT_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0){

                        }
                    }
                });
    }

    public void sendSMS(){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , new String("0123456789;3393993300"));
        smsIntent.putExtra("sms_body"  , "Test SMS to Angilla");
    }

    public void sendCall(){

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+"8802177690"));//change the number
    }

    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"yassinezerdani.gd@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}