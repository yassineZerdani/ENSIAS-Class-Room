package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ensiasclassroom.adapters.EtudiantAdapter;
import com.example.ensiasclassroom.databinding.ActivityEtudiantsListBinding;
import com.example.ensiasclassroom.listeners.EtudiantListener;
import com.example.ensiasclassroom.models.Etudiant;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class EtudiantsListActivity extends AppCompatActivity implements EtudiantListener {

    private ActivityEtudiantsListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEtudiantsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        String role = preferenceManager.getString(Constants.KEY_ROLE);

        if(role.equals("admin")){
            binding.addProfessor.setVisibility(View.VISIBLE);
        }
        binding.addProfessor.setOnClickListener(v -> startActivity(new Intent(this, AddEtudiantActivity.class)));
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ETUDIANT)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_ETUDIANT_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Etudiant> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
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
                            EtudiantAdapter usersController = new EtudiantAdapter(users, this);
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
    public void onUserClicked(Etudiant user) {
        Intent intent = new Intent(getApplicationContext(), EtudiantDetailsActivity.class);
        intent.putExtra(Constants.KEY_ETUDIANT, user);
        startActivity(intent);
        finish();
    }
}