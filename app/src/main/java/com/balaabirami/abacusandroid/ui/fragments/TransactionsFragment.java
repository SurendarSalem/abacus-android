package com.balaabirami.abacusandroid.ui.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentTransactionsBinding;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.StudentListAdapter;
import com.balaabirami.abacusandroid.ui.adapter.TransactionsAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class TransactionsFragment extends Fragment {

    public static TransactionsFragment transactionsFragment;
    FragmentTransactionsBinding binding;
    List<StockTransaction> stockTransactions = new ArrayList<>();
    TransactionViewModel transactionViewModel;
    private TransactionsAdapter transactionsAdapter;

    public TransactionsFragment() {
    }

    public static TransactionsFragment newInstance() {
        if (transactionsFragment == null) {
            transactionsFragment = new TransactionsFragment();
        }
        return transactionsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionsAdapter = new TransactionsAdapter(stockTransactions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transactions, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions();
        transactionViewModel.getResult().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                if (!stockTransactions.contains(listResource.data)) {
                    stockTransactions.add(listResource.data);
                    transactionsAdapter.notifyItemInserted(stockTransactions.size() - 1);
                }
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
    }

    private void initViews() {
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(transactionsAdapter);
    }

    public void showProgress(boolean show) {
        ((HomeActivity) getActivity()).showProgress(show);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}