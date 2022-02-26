package com.balaabirami.abacusandroid.ui.fragments;

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
import com.balaabirami.abacusandroid.databinding.FragmentStockListBinding;
import com.balaabirami.abacusandroid.databinding.FragmentStockUpdateBinding;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.StockListAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StockUpdateFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "ActionBottomDialog";
    private StockListAdapter.StockClickListener mListener;
    FragmentStockUpdateBinding binding;
    Stock stock;
    private StockListAdapter.StockClickListener stockClickListener;

    public static StockUpdateFragment newInstance() {
        return new StockUpdateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stock = getArguments().getParcelable("stock");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_update, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setStock(stock);
        binding.tvQuantity.setText(String.valueOf(stock.getQuantity()));
        binding.btnAdd.setOnClickListener(this);
        binding.btnRemove.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int qtyInput = Integer.parseInt(binding.etQuantity.getText().toString());
        if (view.getId() == R.id.btn_add) {
            stock.setQuantity(stock.getQuantity() + qtyInput);
            stockClickListener.onStockAdded(stock, qtyInput);
        } else if (view.getId() == R.id.btn_remove) {
            stock.setQuantity(stock.getQuantity() - qtyInput);
            stockClickListener.onStockRemoved(stock, qtyInput);
        }

    }

    public void setStockClickListener(StockListAdapter.StockClickListener stockClickListener) {
        this.stockClickListener = stockClickListener;
    }
}