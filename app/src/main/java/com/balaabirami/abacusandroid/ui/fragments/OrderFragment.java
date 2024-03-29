package com.balaabirami.abacusandroid.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import com.balaabirami.abacusandroid.firebase.AnalyticsConstants;
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
import com.balaabirami.abacusandroid.room.AbacusDatabase;
import com.balaabirami.abacusandroid.room.OrderDao;
import com.balaabirami.abacusandroid.room.OrderLog;
import com.balaabirami.abacusandroid.room.PendingOrder;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.activities.PaymentActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private List<PendingOrder> pendingOrders;
    OrderDao orderDao;

    public OrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = PreferenceHelper.getInstance(requireContext()).getCurrentUser();
        orderDao = Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(requireContext().getApplicationContext())).orderDao();
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
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback success"));
                }).start();
                showProgress(false, null);
                UIUtils.API_IN_PROGRESS = false;
                Snackbar.make(getView(), "Order completed!", BaseTransientBottomBar.LENGTH_SHORT).show();
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback success toast shown"));
                }).start();
                logSuccessEvent();
                getFragmentManager().popBackStack();
            } else if (result.status == Status.LOADING) {
                showProgress(true, result.message);
                UIUtils.API_IN_PROGRESS = true;
            } else {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback failed"));
                }).start();
                showProgress(false, null);
                Snackbar.make(getView(), "Order failed!", BaseTransientBottomBar.LENGTH_LONG).show();
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback failure toast shown"));
                }).start();
                logFailureEvent();
                UIUtils.API_IN_PROGRESS = false;
            }
        });
        orderViewModel.getPendingOrderResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback success"));
                }).start();
                showProgress(false, null);
                UIUtils.API_IN_PROGRESS = false;
                Snackbar.make(getView(), "Order completed!", BaseTransientBottomBar.LENGTH_SHORT).show();
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback success toast shown"));
                }).start();
                logSuccessEvent();
            } else if (result.status == Status.LOADING) {
                showProgress(true, result.message);
                UIUtils.API_IN_PROGRESS = true;
            } else {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback failed"));
                }).start();
                showProgress(false, null);
                Snackbar.make(getView(), "Order failed!", BaseTransientBottomBar.LENGTH_LONG).show();
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - Order API callback failure toast shown"));
                }).start();
                logFailureEvent();
                UIUtils.API_IN_PROGRESS = false;
                getFragmentManager().popBackStack();
            }
        });
        pendingOrders = orderViewModel.getPendingOrder(student.getStudentId());
        if (pendingOrders != null && pendingOrders.size() > 0) {
            binding.btnPendingOrder.setVisibility(View.VISIBLE);
            binding.btnOrder.setVisibility(View.GONE);
        } else {
            binding.btnPendingOrder.setVisibility(View.GONE);
            binding.btnOrder.setVisibility(View.VISIBLE);
        }
        binding.btnPendingOrder.setOnClickListener(view1 -> {
            orderViewModel.bulkOrder(pendingOrders, student, stocks, currentUser);
        });
    }

    private void initViews() {
        binding.btnOrder.setOnClickListener(view -> {
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - " + student.getName() + " order button clicked"));
            }).start();
            if (Order.isValid(order, student)) {
                UIUtils.hideKeyboardFrom(requireActivity());
                openPaymentActivityForResult();
            } else {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - invalid order"));
                }).start();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i,
                               long l) {

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

    public void showProgress(boolean show, String message) {
        ((HomeActivity) requireActivity()).showProgress(show, message);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                new Thread(() -> {
                    orderDao.insert(new OrderLog(order.getOrderId(), "Order - payment callback"));
                }).start();
                if (result != null) {
                    new Thread(() -> {
                        orderDao.insert(new OrderLog(order.getOrderId(), "Order - payment callback result not null"));
                    }).start();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        new Thread(() -> {
                            orderDao.insert(new OrderLog(order.getOrderId(), "Order - payment callback RESULT_OK"));
                        }).start();

                        if (order != null) {
                            new Thread(() -> {
                                orderDao.insert(new OrderLog(order.getOrderId(), "Order - order is not null"));
                            }).start();
                            new Thread(() -> {
                                orderDao.insert(new OrderLog(order.getOrderId(), "order setting date"));
                            }).start();
                            order.setDate(UIUtils.getDate());
                            new Thread(() -> {
                                orderDao.insert(new OrderLog(order.getOrderId(), "Order - order date set"));
                            }).start();
                            new Thread(() -> {
                                orderDao.insert(new OrderLog(order.getOrderId(), "Order - order API calling"));
                            }).start();
                            orderViewModel.order(order, student, stocks, currentUser);
                        } else {
                            new Thread(() -> {
                                orderDao.insert(new OrderLog(order.getOrderId(), "Order - order is NULL and popup shown"));
                            }).start();
                            UIUtils.showAlert(requireActivity(), "Order is null");
                        }
                    } else {
                        new Thread(() -> {
                            orderDao.insert(new OrderLog(order.getOrderId(), "Order - order API failure shown"));
                        }).start();
                        UIUtils.showAlert(requireActivity(), "Order failed " + result.getResultCode());
                    }
                } else {
                    new Thread(() -> {
                        orderDao.insert(new OrderLog(order.getOrderId(), "Order - order API failure shown and result is null"));
                    }).start();
                    UIUtils.showAlert(requireActivity(), "Order failed And result is NULL");
                }
            });

    public void openPaymentActivityForResult() {
        if (UIUtils.IS_NO_PAYMENT) {
            order.setDate(UIUtils.getDate());
            orderViewModel.order(order, student, stocks, currentUser);
        } else {
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            intent.putExtra("amount", Order.getOrderValue(currentUser));
            new Thread(() -> {
                orderDao.insert(new OrderLog(order.getOrderId(), "Order - opening payment activity"));
            }).start();
            intent.putExtra("orderId", order.getOrderId());
            intent.putExtra("order", order);
            intent.putExtra("student", (Parcelable) student);
            someActivityResultLauncher.launch(intent);
        }
    }

    @Override
    public void onStop() {
        if (((HomeActivity) requireActivity()).isProgressShown()) {
            logCancelEvent();
        }
        super.onStop();
    }

    private void logCancelEvent
            () {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", currentUser.getId());
        bundle.putString("amount", Order.getOrderValue(currentUser));

        FirebaseAnalytics.getInstance(requireContext())
                .logEvent(AnalyticsConstants.Companion.getEVENT_CLOSE_BEFORE_ORDER(), bundle);
    }

    private void logFailureEvent
            () {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", currentUser.getId());
        bundle.putString("amount", Order.getOrderValue(currentUser));

        FirebaseAnalytics.getInstance(requireContext())
                .logEvent(AnalyticsConstants.Companion.getEVENT_ORDER_FAILED(), bundle);

    }

    private void logSuccessEvent
            () {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", currentUser.getId());
        bundle.putString("amount", Order.getOrderValue(currentUser));

        FirebaseAnalytics.getInstance(requireContext())
                .logEvent(AnalyticsConstants.Companion.getEVENT_ORDER_SUCCESS(), bundle);
    }
}