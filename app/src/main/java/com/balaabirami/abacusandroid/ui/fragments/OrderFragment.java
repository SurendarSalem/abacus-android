package com.balaabirami.abacusandroid.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentOrderBinding;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.activities.PaymentActivity;
import com.balaabirami.abacusandroid.ui.adapter.LevelAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {


    FragmentOrderBinding binding;
    List<String> states = new ArrayList<>();
    Student student;
    private OrderViewModel orderViewModel;
    private StudentListViewModel studentListViewModel;
    //private LevelAdapter levelAdapter;
    List<String> items = new ArrayList<>();
    Order order = new Order();
    private List<Level> levels;

    public OrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            student = getArguments().getParcelable("student");
            order.setCurrentLevel(student.getLevel());
            order.setStudentId(student.getStudentId());
        }
        order.setOrderId(Order.createOrderId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);
        binding.setUser(student);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
        orderViewModel.getLevels().observe(getViewLifecycleOwner(), levels -> {
            this.levels = levels;
            if (student.getProgram().getCourse() == Program.Course.AA) {
                if (student.getLevel().getType() == Level.Type.LEVEL3 || student.getLevel().getType() == Level.Type.LEVEL5 || student.getLevel().getType() == Level.Type.LEVEL5) {
                    binding.cbGraduate.setChecked(true);
                } else if (student.getLevel().getType() == Level.Type.LEVEL6) {
                    binding.cbMaster.setChecked(true);
                }
            } else if (student.getProgram().getCourse() == Program.Course.MA) {
                if (student.getLevel().getType() == Level.Type.LEVEL2 || student.getLevel().getType() == Level.Type.LEVEL3) {
                    binding.cbGraduate.setChecked(true);
                } else if (student.getLevel().getType() == Level.Type.LEVEL4 || student.getLevel().getType() == Level.Type.LEVEL5 || student.getLevel().getType() == Level.Type.LEVEL6) {
                    binding.cbMaster.setChecked(true);
                }
            }
        });
        orderViewModel.getFutureLevels(student.getLevel()).observe(getViewLifecycleOwner(), level -> {
            binding.etFutureLevel.setText(level.getName());
            order.setOrderLevel(level);
        });
        orderViewModel.getBooks(student.getProgram()).observe(getViewLifecycleOwner(), books -> {
            for (String book : books) {
                AppCompatCheckBox cb = new AppCompatCheckBox(requireContext());
                cb.setText(book);
                cb.setOnCheckedChangeListener((compoundButton, checked) -> {
                    if (checked) {
                        order.getBooks().remove(book);
                        order.getBooks().add(book);
                    } else {
                        order.getBooks().remove(book);
                    }
                });
                binding.llBooks.addView(cb);
            }
        });
        orderViewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                showProgress(false);
                Snackbar.make(getView(), "Order completed!", BaseTransientBottomBar.LENGTH_SHORT).show();
                student.setLevel(order.getOrderLevel());
                studentListViewModel.updateStudent(student);
            } else if (result.status == Status.LOADING) {
                showProgress(true);
            } else {
                showProgress(false);
                Snackbar.make(getView(), result.message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        binding.btnOrder.setOnClickListener(view -> {
            if (Order.isValid(order)) {
                UIUtils.hideKeyboardFrom(getActivity());
                openPaymentActivityForResult();
            } else {
                UIUtils.hideKeyboardFrom(getActivity());
                UIUtils.showSnack(Objects.requireNonNull(getActivity()), Order.error);
            }
        });
        binding.cbGraduate.setOnCheckedChangeListener(this);
        binding.cbMaster.setOnCheckedChangeListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            if (compoundButton.getId() == R.id.cb_graduate) {
                order.setCertificate("G");
            } else if (compoundButton.getId() == R.id.cb_master) {
                order.setCertificate("M");
            }
        }
    }

    public void showProgress(boolean show) {
        ((HomeActivity) getActivity()).showProgress(show);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    //Intent data = result.getData();
                    orderViewModel.order(order);
                }
            });

    public void openPaymentActivityForResult() {
        Intent intent = new Intent(requireContext(), PaymentActivity.class);
        intent.putExtra("order", order);
        someActivityResultLauncher.launch(intent);
    }
}