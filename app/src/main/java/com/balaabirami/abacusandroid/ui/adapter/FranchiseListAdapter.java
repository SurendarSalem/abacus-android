package com.balaabirami.abacusandroid.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.balaabirami.abacusandroid.databinding.FranchiseItemBinding;
import com.balaabirami.abacusandroid.databinding.StudentItemBinding;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;


public class FranchiseListAdapter extends RecyclerView.Adapter<FranchiseListAdapter.ViewHolder> {

    private final List<User> users;
    FranchiseClickListener franchiseClickListener;

    public FranchiseListAdapter(List<User> students, FranchiseClickListener franchiseClickListener) {
        this.users = students;
        this.franchiseClickListener = franchiseClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FranchiseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = users.get(position);
        holder.tvName.setText(users.get(position).getName());
        holder.tvEmail.setText(users.get(position).getEmail());
        if (holder.user.isApproved()) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.llApproved.setVisibility(View.VISIBLE);
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.llApproved.setVisibility(View.GONE);
        }
        holder.btnApprove.setOnClickListener(view -> franchiseClickListener.onApproveClicked(holder.user));
        holder.itemView.setOnClickListener(view -> franchiseClickListener.onFranchiseClicked(holder.user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvEmail;
        public final AppCompatButton btnApprove;
        public final LinearLayoutCompat llApproved;
        public User user;

        public ViewHolder(FranchiseItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvName;
            tvEmail = binding.tvEmail;
            btnApprove = binding.btnApprove;
            llApproved = binding.llApproved;
        }
    }

    public interface FranchiseClickListener {
        void onFranchiseClicked(User user);

        void onApproveClicked(User user);
    }
}