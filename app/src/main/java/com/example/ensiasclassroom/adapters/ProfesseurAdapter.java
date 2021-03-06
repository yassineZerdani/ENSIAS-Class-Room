package com.example.ensiasclassroom.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ensiasclassroom.databinding.UsersContainerBinding;
import com.example.ensiasclassroom.listeners.ProfesseurListener;
import com.example.ensiasclassroom.models.Professeur;

import java.util.List;

public class ProfesseurAdapter extends RecyclerView.Adapter<ProfesseurAdapter.UserViewHolder> {

    private final List<Professeur> users;
    private final ProfesseurListener userListener;

    public ProfesseurAdapter(List<Professeur> users, ProfesseurListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsersContainerBinding usersContainerBinding = UsersContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(usersContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        UsersContainerBinding binding;

        UserViewHolder(UsersContainerBinding usersContainerBinding) {
            super(usersContainerBinding.getRoot());
            binding = usersContainerBinding;
        }

        void setUserData(Professeur user) {
            binding.textName.setText(user.nom+" "+user.prenom);
            binding.textEmail.setText("Departement "+user.departement);
            binding.imageProfile.setImageBitmap(getUserImage(user.photo));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
