package com.example.ensiasclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ensiasclassroom.adapters.CRAdapter;
import com.example.ensiasclassroom.adapters.GroupAdapter;
import com.example.ensiasclassroom.databinding.ActivityCoursListBinding;
import com.example.ensiasclassroom.databinding.ActivityGroupListBinding;
import com.example.ensiasclassroom.listeners.GroupListener;
import com.example.ensiasclassroom.models.CR;
import com.example.ensiasclassroom.models.Group;
import com.example.ensiasclassroom.utilities.Constants;
import com.example.ensiasclassroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements GroupListener {

    private ActivityGroupListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){

        String role = preferenceManager.getString(Constants.KEY_ROLE);

        if(role == "admin"){
            binding.addGroup.setVisibility(View.GONE);
        }
        else{
            binding.addGroup.setVisibility(View.VISIBLE);
        }

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.addGroup.setOnClickListener(v -> startActivity(new Intent(this, AddGroupActivity.class)));
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_GROUP)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_GROUP_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Group> crs = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            Group cr = new Group();
                            cr.titre = queryDocumentSnapshot.getString(Constants.KEY_GROUP_TITLE);
                            cr.description = queryDocumentSnapshot.getString(Constants.KEY_GROUP_DESCR);
                            cr.id = queryDocumentSnapshot.getId();
                            crs.add(cr);
                        }
                        if(crs.size() > 0){
                            GroupAdapter usersController = new GroupAdapter(crs, this);
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

    //@Override
    public void onCRClicked(Group user) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.putExtra(Constants.KEY_PROFESSOR, user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onGroupClicked(Group group) {

    }
}