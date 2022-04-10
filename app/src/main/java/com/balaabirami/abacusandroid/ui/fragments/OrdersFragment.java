package com.balaabirami.abacusandroid.ui.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentOrdersBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.BooksRepository;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.OrdersAdapter;
import com.balaabirami.abacusandroid.utils.FilterDialog;
import com.balaabirami.abacusandroid.utils.OrdersReportActivity;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListViewModel;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class OrdersFragment extends Fragment implements FilterDialog.FilterListener {

    public static OrdersFragment ordersFragment;
    FragmentOrdersBinding binding;
    List<Order> orders = new ArrayList<>();
    OrderViewModel orderViewModel;
    private OrdersAdapter ordersAdapter;
    private FilterDialog filterDialog;
    private FranchiseListViewModel franchiseListViewModel;
    private StudentListViewModel studentListViewModel;
    private List<User> franchises;
    private List<Stock> items;
    private ArrayList<State> states;
    private List<Level> levels;
    private List<Student> students;
    private List<Book> books;
    User currentUser;
    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;
    private AlertDialog.Builder exportDialog;

    public OrdersFragment() {
    }

    public static OrdersFragment newInstance() {
        if (ordersFragment == null) {
            ordersFragment = new OrdersFragment();
        }
        return ordersFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordersAdapter = new OrdersAdapter(orders);
        setHasOptionsMenu(true);
        currentUser = PreferenceHelper.getInstance(getContext()).getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
        return binding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_filter) {
            if (filterDialog == null) {
                filterDialog = new FilterDialog(requireContext());
                filterDialog.setFilterListener(this);
                franchiseListViewModel.getFranchiseListData().observe(getViewLifecycleOwner(), listResource -> {
                    if (listResource.status == Status.SUCCESS && listResource.data != null) {
                        franchises = listResource.data;
                        states = StateHelper.getInstance().getStates(requireContext());
                        levels = LevelRepository.newInstance().getLevels();
                        books = BooksRepository.newInstance().getBooks();
                        studentListViewModel.getStudentsListData(currentUser).observe(getViewLifecycleOwner(), studentsData -> {
                            if (studentsData.status == Status.SUCCESS && studentsData.data != null) {
                                students = studentsData.data;
                            }
                            filterDialog.setAdapters(states, franchises, null, students, levels, null);
                        });
                    }
                });
            }
            filterDialog.show();
            return true;
        } else if (item.getItemId() == R.id.menu_export) {
            showPrintDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        franchiseListViewModel = new ViewModelProvider(this).get(FranchiseListViewModel.class);
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);

        orderViewModel.getOrderListData(currentUser).observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                ordersAdapter.notifyList(listResource.data);
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
        binding.rvTransactions.setAdapter(ordersAdapter);
    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (filterDialog != null) {
            filterDialog.clearAllFilter();
            filterDialog = null;
        }
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onFilterCleared() {
        filterDialog.hide();
        ordersAdapter.clearFilter();
    }

    @Override
    public void onFilterApplied(List<State> states, List<User> franchises, List<Stock> stocks, List<Student> students, List<Level> levels, List<Book> books) {
        if (filterDialog != null)
            filterDialog.hide();
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            for (State state : states) {
                if (!filteredOrders.contains(order) && order.getState().equalsIgnoreCase(state.getName())) {
                    filteredOrders.add(order);
                }
            }
        }
        for (Order order : orders) {
            for (User franchise : franchises) {
                if (!filteredOrders.contains(order) && order.getFranchiseName().equalsIgnoreCase(franchise.getName())) {
                    filteredOrders.add(order);
                }
            }
        }

        /*for (Book book : books) {
            for (Order order : orders) {
                if (!filteredOrders.contains(order) && (order.getBooks() != null && order.getBooks().contains(book.getName()))) {
                    filteredOrders.add(order);
                }
            }
        }*/

        for (Student student : students) {
            for (Order order : orders) {
                if (!filteredOrders.contains(order) && order.getStudentName().equalsIgnoreCase(student.getName())) {
                    filteredOrders.add(order);
                }
            }
        }

        for (Level level : levels) {
            for (Order order : orders) {
                if (!filteredOrders.contains(order) && order.getOrderLevel().equals(level)) {
                    filteredOrders.add(order);
                }
            }
        }
        if (states.isEmpty() && franchises.isEmpty() && students.isEmpty() && levels.isEmpty()) {
            filteredOrders.clear();
            filteredOrders.addAll(orders);
        }
        ordersAdapter.updateList(filteredOrders);
    }

    private void showPrintDialog() {
        if (exportDialog == null) {
            exportDialog = new AlertDialog.Builder(requireContext());
            exportDialog.setMessage("Are you sure want to export this as PDF?");
            exportDialog.setPositiveButton("Export", (dialogInterface, i) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(perms, permsRequestCode);
                }
            });
            exportDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
        }
        exportDialog.show();
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);

        if (permsRequestCode == 200) {

            boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (writeAccepted && readAccepted) {
                if (ordersAdapter != null && orders != null && !orders.isEmpty()) {
                    Intent intent = new Intent(requireActivity(), OrdersReportActivity.class);
                    OrdersReportActivity.orders = ordersAdapter.getOrders();
                    requireActivity().startActivity(intent);
                } else {
                    UIUtils.showSnack(getActivity(), "No Orders to export");
                }
            }
        }
    }

}