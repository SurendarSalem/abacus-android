package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentFranchiseDetailsBinding;
import com.balaabirami.abacusandroid.databinding.FragmentStudentDetailsBinding;
import com.balaabirami.abacusandroid.model.Item;
import com.balaabirami.abacusandroid.model.User;

public class FranchiseDetailsFragment extends Fragment {


    private FragmentFranchiseDetailsBinding binding;
    User franchise;

    public FranchiseDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            franchise = getArguments().getParcelable("franchise");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_franchise_details, container, false);
        binding.setUser(franchise);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}