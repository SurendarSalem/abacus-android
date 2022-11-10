package com.balaabirami.abacusandroid.ui.fragments;

import android.content.Intent;
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
import com.balaabirami.abacusandroid.databinding.FragmentStockAdjustListBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockAdjustment;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.OrderListAdapter;
import com.balaabirami.abacusandroid.ui.adapter.StockAdjustListAdapter;
import com.balaabirami.abacusandroid.utils.FilterDialog;
import com.balaabirami.abacusandroid.utils.StockAdjustmentReportActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.OrderViewModel;
import com.balaabirami.abacusandroid.viewmodel.StockAdjustViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class StockAdjustListFragment extends Fragment implements FilterDialog.FilterListener {

    public static StockAdjustListFragment stockAdjustListFragment;
    FragmentStockAdjustListBinding binding;
    List<StockAdjustment> stockAdjustments = new ArrayList<>();
    StockAdjustViewModel stockAdjustViewModel;
    private StockAdjustListAdapter stockAdjustListAdapter;
    User currentUser;
    /*
    private FilterDialog filterDialog;
    private FranchiseListViewModel franchiseListViewModel;
    private StudentListViewModel studentListViewModel;
    private List<User> franchises;
    private List<Stock> items;
    private ArrayList<State> states;
    private List<Level> levels;
    private List<Student> students;
    private List<Book> books;*/

    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;

    private AlertDialog.Builder exportDialog;

    public StockAdjustListFragment() {
    }

    public static StockAdjustListFragment newInstance() {
        if (stockAdjustListFragment == null) {
            stockAdjustListFragment = new StockAdjustListFragment();
        }
        return stockAdjustListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockAdjustListAdapter = new StockAdjustListAdapter();
        setHasOptionsMenu(true);
        currentUser = PreferenceHelper.getInstance(getContext()).getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_adjust_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { menu.clear();
        inflater.inflate(R.menu.menu_stock_adjust_list, menu);
    }

   

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_report) {
            showPrintDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void printReport() {
        Intent intent = new Intent(requireActivity(), StockAdjustmentReportActivity.class);
        StockAdjustmentReportActivity.stockAdjustments = stockAdjustListAdapter.getStockAdjustments();
        requireActivity().startActivity(intent);
    }

    private void showFilterDialog() {
        /*if (filterDialog == null) {
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
                        filterDialog.setAdapters(states, franchises, null, null, levels, null, true);
                    });
                }
            });
        }
        filterDialog.show();*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        stockAdjustViewModel = new ViewModelProvider(this).get(StockAdjustViewModel.class);
       /* franchiseListViewModel = new ViewModelProvider(this).get(FranchiseListViewModel.class);
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);*/

        stockAdjustViewModel.getAllStockAdjustments();
        stockAdjustViewModel.getStockAdjustLisaData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.data != null && listResource.status == Status.SUCCESS) {
                showProgress(false);
                stockAdjustListAdapter.notifyList(listResource.data);
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
    }

    private void initViews() {
        binding.rvAdjustments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdjustments.setAdapter(stockAdjustListAdapter);
    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    @Override
    public void onStart() {
        super.onStart();
       /* if (filterDialog == null || !filterDialog.isShowing()) {
            UIUtils.changeOrientation(requireActivity(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (filterDialog != null) {
            filterDialog.clearAllFilter();
            filterDialog = null;
        }*/
    }


    @Override
    public void onFilterCleared() {
        //stockAdjustListAdapter.clearFilter();
        // filterDialog.hide();
    }

    @Override
    public void onFilterApplied(List<State> states, List<User> franchises, List<Stock> stocks, List<Student> students, List<Level> levels, List<Book> books, String[] dates) {
    }

    @Override
    public void onFilterSelected(List<String> states, List<String> franchises, List<String> stocks, List<String> students, List<String> levels, List<String> books, String[] dates) {
       /* List<OrderList> filteredOrders = new ArrayList<>();

        if (dates != null && dates.length == 2) {
            List<OrderList> dateOrders = new ArrayList<>();
            for (OrderList orderList : orders) {
                if (orderList.getOrder() != null) {
                    if (dates[0].equalsIgnoreCase(dates[1])) {
                        Log.d("Surendar", orderList.toString());
                        if (!dateOrders.contains(orderList) && orderList.getOrder().getDate().equalsIgnoreCase(dates[0])) {
                            if ((states.isEmpty() || states.contains(orderList.getOrder().getState())) &&
                                    (franchises.isEmpty() || franchises.contains(orderList.getOrder().getFranchiseName())) &&
                                    (levels.isEmpty() || levels.contains(orderList.getOrder().getOrderLevel().getName()))) {
                                dateOrders.add(orderList);
                            }
                        }
                    } else {
                        Date startDate = UIUtils.convertStringToDate(dates[0]);
                        Date endDate = UIUtils.convertStringToDate(dates[1]);
                        Date orderDate = UIUtils.convertStringToDate(orderList.getOrder().getDate());
                        if (orderDate.compareTo(startDate) == 0 || // Order Date same as Filter Start Date
                                orderDate.compareTo(endDate) == 0 || // Order Date same as Filter End Date
                                (orderDate.after(startDate) && orderDate.before(endDate)) // Order Date between Filter start date and End date
                                        && !dateOrders.contains(orderList)) {
                            dateOrders.add(orderList);
                        }
                    }
                }
            }
            for (OrderList orderList : dateOrders) {
                if (orderList.getOrder() != null) {
                    if ((states.isEmpty() || states.contains(orderList.getOrder().getState())) &&
                            (franchises.isEmpty() || franchises.contains(orderList.getOrder().getFranchiseName())) &&
                            (levels.isEmpty() || levels.contains(orderList.getOrder().getOrderLevel().getName()))) {
                        filteredOrders.add(orderList);
                    }
                }
            }
        } else {
            for (OrderList orderList : orders) {
                if (orderList.getOrder() != null) {
                    if ((states.isEmpty() || states.contains(orderList.getOrder().getState())) &&
                            (franchises.isEmpty() || franchises.contains(orderList.getOrder().getFranchiseName())) &&
                            (levels.isEmpty() || levels.contains(orderList.getOrder().getOrderLevel().getName()))) {
                        filteredOrders.add(orderList);
                    }
                }
            }

        }

        if (states.isEmpty() && franchises.isEmpty() && students.isEmpty() && levels.isEmpty() && dates == null) {
            filteredOrders.clear();
            filteredOrders.addAll(orders);
        }
        stockAdjustListAdapter.updateList(createOrderListWithHeaders(filteredOrders));
        if (filterDialog != null) {
            filterDialog.hide();
        }*/
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
                if (stockAdjustListAdapter != null && stockAdjustListAdapter.getStockAdjustments() != null && !stockAdjustListAdapter.getStockAdjustments().isEmpty()) {
                    Intent intent = new Intent(requireActivity(), StockAdjustmentReportActivity.class);
                    StockAdjustmentReportActivity.stockAdjustments = stockAdjustListAdapter.getStockAdjustments();
                    requireActivity().startActivity(intent);
                } else {
                    UIUtils.showSnack(requireActivity(), "No Orders to export");
                }
            }
        }
    }

}