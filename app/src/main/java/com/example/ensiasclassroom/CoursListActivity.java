package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ensiasclassroom.adapters.CRAdapter;
import com.example.ensiasclassroom.adapters.EtudiantAdapter;
import com.example.ensiasclassroom.databinding.ActivityCoursListBinding;
import com.example.ensiasclassroom.listeners.CRListener;
import com.example.ensiasclassroom.models.CR;
import com.example.ensiasclassroom.models.Etudiant;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CoursListActivity extends AppCompatActivity implements CRListener {

    private ActivityCoursListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_CR)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_CR_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<CR> crs = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            CR cr = new CR();
                            cr.description = queryDocumentSnapshot.getString(Constants.KEY_CR_DESCR);
                            cr.heure_debut = queryDocumentSnapshot.getString(Constants.KEY_CR_HOUR);
                            cr.id = queryDocumentSnapshot.getId();
                            crs.add(cr);
                        }
                        if(crs.size() > 0){
                            CRAdapter usersController = new CRAdapter(crs, this);
                            binding.cRecyclerView.setAdapter(usersController);
                            binding.cRecyclerView.setVisibility(View.VISIBLE);
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
    public void onCRClicked(CR user) {
        Intent intent = new Intent(getApplicationContext(), ProfesseurChatActivity.class);
        //intent.putExtra(Constants.KEY_PROFESSOR, user);
        startActivity(intent);
        finish();
    }
}