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
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.CartOrder;
import com.balaabirami.abacusandroid.model.Certificate;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.activities.PaymentActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {


    FragmentOrderBinding binding;
    List<String> states = new ArrayList<>();
    Student student;
    private OrderViewModel orderViewModel;
    private StudentListViewModel studentListViewModel;
    List<String> items = new ArrayList<>();
    Order order = new Order();
    private List<Level> levels;
    User currentUser;
    private StockListViewModel stockListViewModel;
    private List<Stock> stocks = new ArrayList<>();
    private int retryCount = 0;

    public OrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = PreferenceHelper.getInstance(requireContext()).getCurrentUser();
        if (getArguments() != null) {
            student = getArguments().getParcelable("student");
            order.setCurrentLevel(student.getLevel());
            order.setStudentId(student.getStudentId());
            order.setFranchiseName(currentUser.getName());
            order.setState(currentUser.getState());
            order.setStudentName(student.getName());
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
        stockListViewModel = new ViewModelProvider(this).get(StockListViewModel.class);
        stockListViewModel.getStockListLiveData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.data != null && listResource.status == Status.SUCCESS) {
                showProgress(false);
                stocks.addAll(listResource.data);
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
        orderViewModel.getLevels().observe(getViewLifecycleOwner(), levels -> {
            this.levels = levels;
        });
        orderViewModel.getFutureLevels(student.getLevel()).observe(getViewLifecycleOwner(), level -> {
            binding.etFutureLevel.setText(level.getName());
            order.setOrderLevel(level);
            if (student.getProgram().getCourse() == Program.Course.AA) {
                if (order.getOrderLevel().getLevel() == 4) {
                    order.setCertificate(Certificate.CERT_GRADUATE);
                    binding.tvCertificate.setText("Graduate");
                } else if (order.getOrderLevel().getLevel() == 6) {
                    order.setCertificate(Certificate.CERT_MASTER);
                    binding.tvCertificate.setText("Master");
                } else {
                    order.setCertificate("N");
                    binding.llCertificates.setVisibility(View.GONE);
                }
            } else if (student.getProgram().getCourse() == Program.Course.MA) {
                if (order.getOrderLevel().getLevel() == 3) {
                    order.setCertificate(Certificate.CERT_GRADUATE);
                    binding.tvCertificate.setText("Graduate");
                } else if (order.getOrderLevel().getLevel() == 5) {
                    order.setCertificate(Certificate.CERT_MASTER);
                    binding.tvCertificate.setText("Master");
                } else {
                    order.setCertificate("N");
                    binding.llCertificates.setVisibility(View.GONE);
                }
            } else {
                order.setCertificate("N");
                binding.llCertificates.setVisibility(View.GONE);
            }
        });
        orderViewModel.getBooks(student).observe(getViewLifecycleOwner(), books -> {
            order.getBooks().addAll(books);
            for (String book : books) {
                AppCompatCheckBox cb = new AppCompatCheckBox(requireContext());
                cb.setText(book);
                cb.setChecked(true);
                cb.setClickable(false);
                binding.llBooks.addView(cb);
                binding.llBooks.setFocusable(false);
                binding.llBooks.setClickable(false);
                binding.llBooks.setFocusableInTouchMode(false);
            }
        });
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                showProgress(false);
                Snackbar.make(getView(), "Order completed!", BaseTransientBottomBar.LENGTH_SHORT).show();
                if (student.isPromotedAAtoMA()) {
                    student.setLevel(orderViewModel.getLevel(5));
                    student.setProgram(Program.getMA());
                } else {
                    student.setLevel(order.getOrderLevel());
                }
                student.setLastOrderedDate(order.getDate());
                studentListViewModel.updateStudent(student);
                student.setPromotedAAtoMA(false);
                UIUtils.API_IN_PROGRESS = false;
                //getActivity().onBackPressed();
            } else if (result.status == Status.LOADING) {
                showProgress(true);
                UIUtils.API_IN_PROGRESS = true;
            } else {
                retryCount++;
                if (retryCount >= 2) {
                    showProgress(false);
                    Snackbar.make(getView(), "Order failed!", BaseTransientBottomBar.LENGTH_LONG).show();
                    UIUtils.API_IN_PROGRESS = false;
                } else {
                    Snackbar.make(getView(), "Order failed!. Retrying..", BaseTransientBottomBar.LENGTH_LONG).show();
                    orderViewModel.order(order, student, stocks, currentUser);
                }
            }
        });
    }

    private void initViews() {
        binding.btnOrder.setOnClickListener(view -> {
            if (Order.isValid(order, student)) {
                UIUtils.hideKeyboardFrom(requireActivity());
                openPaymentActivityForResult();
            } else {
                UIUtils.hideKeyboardFrom(requireActivity());
                UIUtils.showSnack(requireActivity(), Order.error);
            }
        });
        binding.btnAddCart.setOnClickListener(view -> {
            if (Order.isValid(order, student)) {
                UIUtils.hideKeyboardFrom(requireActivity());
                CartOrder cartOrder = new CartOrder(order, student, stocks, currentUser, CartOrder.CartOrderType.ORDER);
                if (orderViewModel.addToCart(cartOrder)) {
                    binding.btnAddCart.setText(getString(R.string.remove_from_cart));
                    UIUtils.showToast(requireContext(), getString(R.string.items_added_cart));
                } else {
                    binding.btnAddCart.setText(getString(R.string.add_to_cart));
                    UIUtils.showToast(requireContext(), getString(R.string.items_removed_cart));
                }
            } else {
                UIUtils.hideKeyboardFrom(requireActivity());
                UIUtils.showSnack(requireActivity(), Order.error);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    order.setDate(UIUtils.getDate());
                    orderViewModel.order(order, student, stocks, currentUser);
                }
            });

    public void openPaymentActivityForResult() {
        if (UIUtils.IS_NO_PAYMENT) {
            order.setDate(UIUtils.getDate());
            orderViewModel.order(order, student, stocks, currentUser);
        } else {
            Intent intent = new Intent(requireContext(), PaymentActivity.class);
            intent.putExtra("amount", Order.getOrderValue(currentUser));
            someActivityResultLauncher.launch(intent);
        }

    }
}