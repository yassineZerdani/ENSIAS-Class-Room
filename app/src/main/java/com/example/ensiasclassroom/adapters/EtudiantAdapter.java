package com.example.ensiasclassroom.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ensiasclassroom.databinding.UsersContainerBinding;
import com.example.ensiasclassroom.listeners.EtudiantListener;
import com.example.ensiasclassroom.models.Etudiant;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.UserViewHolder> {

    private final List<Etudiant> users;
    private final EtudiantListener userListener;

    public EtudiantAdapter(List<Etudiant> users, EtudiantListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public EtudiantAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsersContainerBinding usersContainerBinding = UsersContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new EtudiantAdapter.UserViewHolder(usersContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantAdapter.UserViewHolder holder, int position) {
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

        void setUserData(Etudiant user) {
            binding.textName.setText(user.nom+" "+user.prenom);
            binding.imageProfile.setImageBitmap(getUserImage(user.photo));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
