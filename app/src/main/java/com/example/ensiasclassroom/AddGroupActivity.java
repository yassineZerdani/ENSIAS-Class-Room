package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.ensiasclassroom.adapters.EtudiantAdapter;
import com.example.ensiasclassroom.databinding.ActivityAddCrBinding;
import com.example.ensiasclassroom.databinding.ActivityAddEtudiantsToGroupBinding;
import com.example.ensiasclassroom.databinding.ActivityAddGroupBinding;
import com.example.ensiasclassroom.listeners.EtudiantListener;
import com.example.ensiasclassroom.models.Etudiant;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity implements EtudiantListener {

    private ActivityAddGroupBinding binding1;
    private ActivityAddEtudiantsToGroupBinding binding2;
    private PreferenceManager preferenceManager;
    public LinkedList<String> etudiants = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding1 = ActivityAddGroupBinding.inflate(getLayoutInflater());
        binding2 = ActivityAddEtudiantsToGroupBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding1.next.setOnClickListener(v -> {
            setContentView(binding2.getRoot());
            getUsers();
        });

        binding2.accept.setOnClickListener( v -> {
            if(isValidServiceDetails()) {
                addGroup();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addGroup(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> service = new HashMap<>();
        service.put(Constants.KEY_GROUP_TITLE, binding1.nom.getText().toString());
        service.put(Constants.KEY_GROUP_DESCR, binding1.description.getText().toString());
        service.put(Constants.KEY_GROUP_RTUDIANTS_LIST, etudiants);
        database.collection(Constants.KEY_COLLECTION_GROUP).add(service).addOnSuccessListener(documentReference -> {
            Intent intent = new Intent(getApplicationContext(), ProfessorMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).addOnFailureListener(exception -> {
            showToast(exception.getMessage());
        });
    }

    private Boolean isValidServiceDetails(){

        if(binding1.description.getText().toString().trim().isEmpty()){
            showToast("Enter Service Name");
            return false;
        }
        else if(binding1.description.getText().toString().trim().isEmpty()){
            showToast("Enter Service Description");
            return false;
        }
        else {
            return true;
        }
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
                            binding2.usersRecyclerView.setAdapter(usersController);
                            binding2.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding2.textErrorMessage.setText(String.format("%s", "No User Available"));
        binding2.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding2.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding2.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(Etudiant user) {
        String id = user.id;
        etudiants.add(user.id);
    }

}