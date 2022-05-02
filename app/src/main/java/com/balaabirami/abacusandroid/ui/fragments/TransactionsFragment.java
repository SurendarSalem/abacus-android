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
import com.balaabirami.abacusandroid.databinding.FragmentTransactionsBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.TransactionsAdapter;
import com.balaabirami.abacusandroid.utils.FilterDialog;
import com.balaabirami.abacusandroid.utils.PdfHelper;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.balaabirami.abacusandroid.utils.TransactionReportActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListViewModel;
import com.balaabirami.abacusandroid.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class TransactionsFragment extends Fragment implements FilterDialog.FilterListener {

    public static TransactionsFragment transactionsFragment;
    FragmentTransactionsBinding binding;
    List<StockTransaction> stockTransactions = new ArrayList<>();
    TransactionViewModel transactionViewModel;
    private TransactionsAdapter transactionsAdapter;
    private FilterDialog filterDialog;
    private FranchiseListViewModel franchiseListViewModel;
    private StockListViewModel stockListViewModel;
    private List<User> franchises;
    private List<Stock> items;
    private ArrayList<State> states;
    User currentUser;
    Stock stock;


    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;
    private PdfHelper pdfHelper;
    private AlertDialog.Builder exportDialog;

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
        setHasOptionsMenu(true);
        currentUser = PreferenceHelper.getInstance(getContext()).getCurrentUser();
        if (getArguments() != null) {
            stock = getArguments().getParcelable("stock");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transactions, container, false);
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
                        filterDialog.setAdapters(states, franchises, null, null, null, null);
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
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        franchiseListViewModel = new ViewModelProvider(this).get(FranchiseListViewModel.class);
        stockListViewModel = new ViewModelProvider(this).get(StockListViewModel.class);
        transactionViewModel.getAllTransactions(stock, currentUser);
        transactionViewModel.getResult().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                transactionsAdapter.notifyList(listResource.data);
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
        transactionsAdapter.clearFilter();
    }

    @Override
    public void onFilterApplied(List<State> states, List<User> franchises, List<Stock> stocks, List<Student> students, List<Level> levels, List<Book> books, String[] dates) {
        filterDialog.hide();
        List<StockTransaction> filteredStockTransactions = new ArrayList<>();
        for (StockTransaction stockTransaction : stockTransactions) {
            for (State state : states) {
                if (!filteredStockTransactions.contains(stockTransaction) && stockTransaction.getStudentState().equalsIgnoreCase(state.getName())) {
                    filteredStockTransactions.add(stockTransaction);
                }
            }
        }
        for (StockTransaction stockTransaction : stockTransactions) {
            for (User franchise : franchises) {
                if (!filteredStockTransactions.contains(stockTransaction) && stockTransaction.getFranchiseID().equalsIgnoreCase(franchise.getId())) {
                    filteredStockTransactions.add(stockTransaction);
                }
            }
        }

        if (states.isEmpty() && franchises.isEmpty()) {
            filteredStockTransactions.clear();
            filteredStockTransactions.addAll(stockTransactions);
        }
        transactionsAdapter.updateList(filteredStockTransactions);
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);

        if (permsRequestCode == 200) {

            boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (writeAccepted && readAccepted) {
                if (transactionsAdapter != null && stockTransactions != null && !stockTransactions.isEmpty()) {
                    Intent intent = new Intent(requireActivity(), TransactionReportActivity.class);
                    TransactionReportActivity.stockTransactions = transactionsAdapter.getTransactions();
                    requireActivity().startActivity(intent);
                } else {
                    UIUtils.showSnack(getActivity(), "No Students to export");
                }
            }

        }


    }

}