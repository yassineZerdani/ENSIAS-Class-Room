package com.example.ensiasclassroom.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ensiasclassroom.GroupListActivity;
import com.example.ensiasclassroom.databinding.CrContainerBinding;
import com.example.ensiasclassroom.databinding.GroupContainerBinding;
import com.example.ensiasclassroom.listeners.GroupListener;
import com.example.ensiasclassroom.models.CR;
import com.example.ensiasclassroom.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

private final List<Group> crs;
private final GroupListener crListener;

public GroupAdapter(List<Group> crs, GroupListener crListener) {
        this.crs = crs;
        this.crListener = crListener;
        }

@NonNull
@Override
public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupContainerBinding crContainerBinding = GroupContainerBinding.inflate(
        LayoutInflater.from(parent.getContext()),
        parent,
        false
        );

        return new GroupAdapter.GroupViewHolder(crContainerBinding);
        }

@Override
public void onBindViewHolder(@NonNull GroupAdapter.GroupViewHolder holder, int position) {
        holder.setUserData(crs.get(position));
        }

@Override
public int getItemCount() {

        return crs.size();
        }

class GroupViewHolder extends RecyclerView.ViewHolder {
    GroupContainerBinding binding;

    GroupViewHolder(GroupContainerBinding usersContainerBinding) {
        super(usersContainerBinding.getRoot());
        binding = usersContainerBinding;
    }

    void setUserData(Group user) {
        binding.description.setText(user.description);
        binding.titre.setText(user.titre);
        binding.getRoot().setOnClickListener(v -> crListener.onGroupClicked(user));
    }
}
}
