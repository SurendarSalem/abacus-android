package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentStudentDetailsBinding;
import com.balaabirami.abacusandroid.model.Item;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.room.PendingOrder;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;

public class StudentDetailsFragment extends Fragment {


    private FragmentStudentDetailsBinding binding;
    Student student;
    private View view;

    public StudentDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            student = getArguments().getParcelable("student");
        }
        setHasOptionsMenu(false);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_student_details, container, false);
            binding.setUser(student);
            view = binding.getRoot();
            binding.setUser(student);
            bindData();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getAllOrders(student);
        orderViewModel.getStudentOrdersData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                binding.llOrders.removeAllViews();
                if (listResource.data != null && !listResource.data.isEmpty()) {
                    for (Order order : listResource.data) {
                        binding.llOrders.setVisibility(View.VISIBLE);
                        View historyView = getLayoutInflater().inflate(R.layout.layout_order_history_item, binding.container, false);
                        AppCompatTextView tvDate = historyView.findViewById(R.id.tv_date);
                        AppCompatTextView tvFromLevel = historyView.findViewById(R.id.tv_from);
                        AppCompatTextView tvToLevel = historyView.findViewById(R.id.tv_to);
                        tvDate.setText(order.getDate());
                        tvFromLevel.setText(order.getCurrentLevel().getName());
                        tvToLevel.setText(order.getOrderLevel().getName());
                        binding.llOrders.addView(historyView);
                    }
                }
            }
        });
    }

    private void bindData() {
        binding.cbAbacus.setChecked(student.getItems().contains(binding.cbAbacus.getText().toString()));
        binding.cbAbility.setChecked(student.getItems().contains(binding.cbAbility.getText().toString()));
        binding.cbBag.setChecked(student.getItems().contains(binding.cbBag.getText().toString()));
        binding.cbPencil.setChecked(student.getItems().contains(binding.cbPencil.getText().toString()));
        binding.cbProgressCard.setChecked(student.getItems().contains(binding.cbProgressCard.getText().toString()));
        if (student.getItems().contains(Item.SIZE.SIZE8.getSize())) {
            binding.rbTshirtSize8.setChecked(true);
            binding.rbTshirt.setChecked(true);
        } else if (student.getItems().contains(Item.SIZE.SIZE12.getSize())) {
            binding.rbTshirtSize12.setChecked(true);
            binding.rbTshirt.setChecked(true);
        } else if (student.getItems().contains(Item.SIZE.SIZE16.getSize())) {
            binding.rbTshirtSize16.setChecked(true);
            binding.rbTshirt.setChecked(true);
        }
        if (student.isCompletedCourse()) {
            binding.btnOrder.setEnabled(false);
            binding.btnOrder.setText("Course Completed!");
        }
        binding.btnOrder.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("student", student);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.orderFragment, bundle);
        });
    }
}