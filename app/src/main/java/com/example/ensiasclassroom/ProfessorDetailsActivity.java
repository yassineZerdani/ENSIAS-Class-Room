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

import com.example.ensiasclassroom.databinding.ActivityProfessorDetailsBinding;
import com.example.ensiasclassroom.listeners.ProfesseurListener;
import com.example.ensiasclassroom.models.Professeur;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfessorDetailsActivity extends AppCompatActivity implements ProfesseurListener {

    private ActivityProfessorDetailsBinding binding;
    private Professeur professeur;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessorDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        loadServiceDetails();
        binding.sendMail.setOnClickListener( v -> sendEmail());
        binding.sendSMS.setOnClickListener( v -> sendSMS());
        binding.sendCall.setOnClickListener( v -> sendCall());
        binding.delete.setOnClickListener(v -> {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_PROFESSORS).document(professeur.id).delete();
            Intent intent = new Intent(getApplicationContext(), ProfesseursListActivity.class);
            startActivity(intent);
            finish();
        });

        String role = preferenceManager.getString(Constants.KEY_ROLE);


        if(role.equals("admin")){
            binding.delete.setVisibility(View.VISIBLE);
        }

    }



    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadServiceDetails(){
        professeur = (Professeur) getIntent().getSerializableExtra(Constants.KEY_PROFESSOR);
        binding.imageService.setImageBitmap(getBitmapFromEncodedString(professeur.photo));
        binding.textName.setText("Pr "+professeur.prenom+" "+professeur.nom);
        binding.textDescription.setText("Departement "+professeur.departement);
        binding.negociate.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
            intent.putExtra(Constants.KEY_PROFESSOR, professeur);
            startActivity(intent);
            finish();
        });
    }

    private Professeur getUser(String userId){
        Professeur user = new Professeur();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_PROFESSORS)
                .get()
                .addOnCompleteListener(task -> {

                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (userId.equals(queryDocumentSnapshot.getId())) {
                            continue;
                        }

                        user.nom = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_FIRST_NAME);
                        //user.mail = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_EMAIL);
                        user.photo = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_IMAGE);
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                        user.id = queryDocumentSnapshot.getId();

                    }
                });
        return user;
    }


    private void getUsers(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_PROFESSORS)
                .get()
                .addOnCompleteListener(task -> {

                    String currentUserId = preferenceManager.getString(Constants.KEY_ETUDIANT_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Professeur> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            if(binding.textAuthor.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            Professeur user = new Professeur();
                            user.nom = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_FIRST_NAME);
                            user.prenom = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_LAST_NAME);
                            user.photo = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_IMAGE);
                            user.tel = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_PHONE);
                            user.departement = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_DEPARTEMENT);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0){
                            binding.negociate.setOnClickListener(v -> onUserClicked(users.get(0)));
                        }
                    }
                });
    }

    public void sendSMS(){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , new String("0984768:738478"));
        smsIntent.putExtra("sms_body"  , "uilhiuh");
    }

    public void sendCall(){

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+"8802177690"));//change the number
    }



    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"yassinezerdani.gd@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Objet...");
        i.putExtra(Intent.EXTRA_TEXT   , "Message...");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onUserClicked(Professeur professeur) {
        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
        intent.putExtra(Constants.KEY_PROFESSOR, professeur);
        startActivity(intent);
        finish();
    }
}