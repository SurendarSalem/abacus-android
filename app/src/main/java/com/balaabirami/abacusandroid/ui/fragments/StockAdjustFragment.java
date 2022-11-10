package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentStockAdjustBinding;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockAdjustment;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.StockRepository;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.fragments.FilterFragment.FilterSelectionListener;
import com.balaabirami.abacusandroid.viewmodel.EnrollViewModel;
import com.balaabirami.abacusandroid.viewmodel.StockAdjustViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StockAdjustFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    FragmentStockAdjustBinding binding;
    StockAdjustment stockAdjustment = new StockAdjustment();
    private StockAdjustViewModel stockAdjustViewModel;

    public StockAdjustFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockAdjustViewModel = new ViewModelProvider(this).get(StockAdjustViewModel.class);
        stockAdjustment.setId(StockAdjustment.createId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_adjust, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setStockAdjustment(stockAdjustment);
        initViews();
    }

    private void initViews() {
        FilterFragment filterFragment = new FilterFragment();
        filterFragment.setFilterSelectionListener(new FilterSelectionListener() {
            @Override
            public void onStudentSelected(@NonNull Student student) {
                binding.tlStudent.setText(student.getName());
                stockAdjustment.setStudent(student);
            }

            @Override
            public void onFranchiseSelected(@NonNull User franchise) {
                binding.tlFranchise.setText(franchise.getName());
                stockAdjustment.setFranchise(franchise);
            }
        });
        binding.tlStudent.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt(FilterFragment.FILTER_TYPE, FilterFragment.FILTER_STUDENT);
            filterFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().add(R.id.fragment_container, filterFragment).addToBackStack("filterFragment").commit();

        });
        binding.tlFranchise.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt(FilterFragment.FILTER_TYPE, FilterFragment.FILTER_FRANCHISE);
            filterFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().add(R.id.fragment_container, filterFragment).addToBackStack("filterFragment").commit();
        });

        for (Stock stock : StockRepository.getInstance().getStocks()) {
            View stockItem = getLayoutInflater().inflate(R.layout.stock_adjust_item, binding.llItems, false);
            AppCompatTextView tvName = stockItem.findViewById(R.id.tv_name);
            tvName.setText(stock.getName());
            AppCompatCheckBox cbSelected = stockItem.findViewById(R.id.cb_selected);
            AppCompatEditText etQty = stockItem.findViewById(R.id.et_qty);
            etQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    StockAdjustment.ItemDetail itemDetail = new StockAdjustment.ItemDetail();
                    itemDetail.setName(stock);
                    if (editable != null && editable.length() > 0) {
                        int qty = Integer.parseInt(editable.toString());
                        itemDetail.setQty(qty);
                        if (cbSelected.isChecked() && qty > 0) {
                            if (stockAdjustment.getItems() == null || stockAdjustment.getItems().isEmpty()) {
                                List<StockAdjustment.ItemDetail> itemDetails = new ArrayList<>();
                                itemDetails.add(itemDetail);
                                stockAdjustment.setItems(itemDetails);
                            } else {
                                if (!stockAdjustment.getItems().contains(itemDetail)) {
                                    stockAdjustment.getItems().add(itemDetail);
                                } else {
                                    stockAdjustment.getItems().get(stockAdjustment.getItems().indexOf(itemDetail)).setQty(qty);
                                }
                            }
                        } else {
                            if (stockAdjustment.getItems() != null && !stockAdjustment.getItems().isEmpty() && stockAdjustment.getItems().contains(itemDetail)) {
                                stockAdjustment.getItems().remove(itemDetail);
                            }
                        }
                    } else {
                        if (stockAdjustment.getItems() != null && !stockAdjustment.getItems().isEmpty() && stockAdjustment.getItems().contains(itemDetail)) {
                            stockAdjustment.getItems().remove(itemDetail);
                        }
                    }
                }
            });
            cbSelected.setOnCheckedChangeListener((compoundButton, b) -> {
                etQty.setEnabled(b);
            });
            binding.llItems.addView(stockItem);
            binding.rgOrderType.setOnCheckedChangeListener((radioGroup, id) -> {
                if (id == R.id.rb_damage) {
                    binding.tlRequestedBy.setVisibility(View.GONE);
                    binding.tlDamageDate.setVisibility(View.VISIBLE);
                    stockAdjustment.setOrderType(StockAdjustment.AdjustType.DAMAGE);
                } else if (id == R.id.rb_reissue) {
                    binding.tlRequestedBy.setVisibility(View.VISIBLE);
                    binding.tlDamageDate.setVisibility(View.GONE);
                    stockAdjustment.setOrderType(StockAdjustment.AdjustType.REISSUE);
                }
            });
            binding.rgPaymentType.setOnCheckedChangeListener((radioGroup, id) -> {
                if (id == R.id.rb_free) {
                    binding.tlAmountReceived.setVisibility(View.GONE);
                    binding.tlAmountDate.setVisibility(View.GONE);
                    stockAdjustment.setPaymentType(StockAdjustment.PaymentType.FREE);
                } else if (id == R.id.rb_cash) {
                    binding.tlAmountReceived.setVisibility(View.VISIBLE);
                    binding.tlAmountDate.setVisibility(View.VISIBLE);
                    stockAdjustment.setPaymentType(StockAdjustment.PaymentType.CASH);
                }
            });
        }
        binding.etOrderDate.addTextChangedListener(new DateTextWatchListener(binding.etOrderDate));
        binding.etAmountDate.addTextChangedListener(new DateTextWatchListener(binding.etAmountDate));
        binding.etDamageDate.addTextChangedListener(new DateTextWatchListener(binding.etDamageDate));
        binding.btnSubmit.setOnClickListener(view -> {
            String error = stockAdjustment.isValid();
            if (error.isEmpty()) {
                binding.setError(error);
                stockAdjustViewModel.createStockAdjust(stockAdjustment, StockRepository.getInstance().getStocks());
            } else {
                binding.setError(error);
            }
        });
        stockAdjustViewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                showProgress(false);
                Snackbar.make(binding.getRoot(), "Stock adjust Enrolled", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else if (result.status == Status.LOADING) {
                showProgress(true);
            } else {
                showProgress(false);
                Snackbar.make(binding.getRoot(), result.message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void updateItems(StockAdjustment.ItemDetail itemDetail) {

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    CompoundButton.OnCheckedChangeListener itemSelectListener = (compoundButton, checked) -> {
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    private class DateTextWatchListener implements TextWatcher {

        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();
        AppCompatEditText editText;

        DateTextWatchListener(AppCompatEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int pos, int i1, int i2) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                editText.setText(current);
                editText.setSelection(sel < current.length() ? sel : current.length());
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}