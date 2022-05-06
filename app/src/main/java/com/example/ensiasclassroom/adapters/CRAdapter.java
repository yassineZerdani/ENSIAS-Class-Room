package com.example.ensiasclassroom.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ensiasclassroom.databinding.CrContainerBinding;
import com.example.ensiasclassroom.listeners.CRListener;
import com.example.ensiasclassroom.models.CR;

import java.util.List;

public class CRAdapter extends RecyclerView.Adapter<CRAdapter.CRViewHolder> {

    private final List<CR> crs;
    private final CRListener crListener;

    public CRAdapter(List<CR> crs, CRListener crListener) {
        this.crs = crs;
        this.crListener = crListener;
    }

    @NonNull
    @Override
    public CRAdapter.CRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CrContainerBinding crContainerBinding = CrContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new CRAdapter.CRViewHolder(crContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CRAdapter.CRViewHolder holder, int position) {
        holder.setUserData(crs.get(position));
    }

    @Override
    public int getItemCount() {

        return crs.size();
    }

    class CRViewHolder extends RecyclerView.ViewHolder {
        CrContainerBinding binding;

        CRViewHolder(CrContainerBinding usersContainerBinding) {
            super(usersContainerBinding.getRoot());
            binding = usersContainerBinding;
        }

        void setUserData(CR user) {
            binding.description.setText(user.description);
            binding.heureDebut.setText(user.heure_debut);
            binding.getRoot().setOnClickListener(v -> crListener.onCRClicked(user));
        }
    }

}
