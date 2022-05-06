package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.ensiasclassroom.adapters.ProfesseurAdapter;
import com.example.ensiasclassroom.databinding.ActivityProfesseursListBinding;
import com.example.ensiasclassroom.listeners.ProfesseurListener;
import com.example.ensiasclassroom.models.Professeur;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfesseursListActivity extends AppCompatActivity implements ProfesseurListener {

    private ActivityProfesseursListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfesseursListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        setListeners();
        getUsers();
    }

    private void setListeners(){

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.addProfessor.setOnClickListener(v -> startActivity(new Intent(this, AddProfessorActivity.class)));
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_PROFESSORS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_PROFESSOR_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Professeur> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            Professeur user = new Professeur();
                            user.nom = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_FIRST_NAME);
                            user.prenom = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_LAST_NAME);
                            user.photo = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_IMAGE);
                            user.departement = queryDocumentSnapshot.getString(Constants.KEY_PROFESSOR_DEPARTEMENT);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0){
                            ProfesseurAdapter usersController = new ProfesseurAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersController);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(Professeur user) {
        Intent intent = new Intent(this, ProfessorDetailsActivity.class);
        intent.putExtra(Constants.KEY_PROFESSOR, user);
        startActivity(intent);
        finish();
    }
}