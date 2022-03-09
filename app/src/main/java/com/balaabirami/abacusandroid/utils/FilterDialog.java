package com.balaabirami.abacusandroid.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.adapter.multiadapter.FilterAdapter;

import java.util.List;

public class FilterDialog extends AppCompatDialog {
    Spinner spStates, spFranchise, spStocks;
    FilterListener filterListener;
    AppCompatButton btnApply, btnClear;
    String[] filters = new String[2];
    private FilterAdapter<Stock> stocksAdapter;
    private FilterAdapter<User> franchiseAdapter;
    private FilterAdapter<State> stateAdapter;

    public FilterDialog(Context context) {
        super(context);
        initDialog(context);
        initViews();
    }

    private void initDialog(Context context) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.filter_dialog);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void initViews() {
        spStates = findViewById(R.id.sp_state);
        spFranchise = findViewById(R.id.sp_franchise);
        spStocks = findViewById(R.id.sp_students);
        btnApply = findViewById(R.id.btn_apply);
        btnClear = findViewById(R.id.btn_clear);
        btnApply.setOnClickListener(view -> {
            //filterListener.onFilterApplied(filters);
            List<Stock> selectedItems = null;
            List<State> selectedStates = (List<State>) stateAdapter.getSelectedObjects();
            List<User> selectedFranchises = (List<User>) franchiseAdapter.getSelectedObjects();
            if (stocksAdapter != null) {
                selectedItems = (List<Stock>) stocksAdapter.getSelectedObjects();
            }
            filterListener.onFilterApplied(selectedStates, selectedFranchises, selectedItems);
        });
        btnClear.setOnClickListener(view -> {
            spStates.setSelection(0);
            spFranchise.setSelection(0);
            stateAdapter.clearAll();
            franchiseAdapter.clearAll();
            if (stocksAdapter != null) {
                stocksAdapter.clearAll();
            }
            filterListener.onFilterCleared();
        });
    }

    public void setAdapters(List<State> states, List<User> franchises, List<Stock> stocks) {

        stateAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, states);
        spStates.setAdapter(stateAdapter);

        franchiseAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, franchises);
        spFranchise.setAdapter(franchiseAdapter);
        if (stocks == null) {
            spStocks.setVisibility(View.GONE);
        } else {
            Stock header = new Stock();
            header.setName("Select an Item");
            stocks.set(0, header);
            stocksAdapter = new FilterAdapter<>(getContext(), R.layout.multi_choice_spiiner_item, stocks);
            spStocks.setAdapter(stocksAdapter);
        }
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    public interface FilterListener {
        void onFilterCleared();

        void onFilterApplied(List<State> states, List<User> franchises, List<Stock> students);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == R.id.sp_state) {
                if (i > 0) {
                    filters[0] = (String) spStates.getAdapter().getItem(i);
                } else {
                    filters[0] = null;
                }
            } else if (adapterView.getId() == R.id.sp_franchise) {
                if (i > 0) {
                    filters[1] = ((User) spFranchise.getAdapter().getItem(i)).getId();
                } else {
                    filters[1] = null;
                }

            } else if (adapterView.getId() == R.id.sp_students) {
                if (i > 0) {
                    filters[1] = ((User) spFranchise.getAdapter().getItem(i)).getId();
                } else {
                    filters[1] = null;
                }

            }
            if (filters[0] != null || filters[1] != null) {
                btnClear.setVisibility(View.VISIBLE);
            } else {
                btnClear.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


}
