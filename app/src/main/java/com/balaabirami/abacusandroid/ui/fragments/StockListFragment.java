package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentStockListBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.FranchiseRepository;
import com.balaabirami.abacusandroid.repository.StudentsRepository;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.StockListAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StockListFragment extends Fragment implements StockListAdapter.StockUpdateDialogListener {

    private static StockListFragment stockListFragment;
    FragmentStockListBinding binding;
    List<Stock> stocks = new ArrayList<>();
    StockListViewModel stockListViewModel;
    private StockListAdapter stockListAdapter;
    private StockUpdateFragment stockUpdateFragment;
    private StockTransaction stockTransaction;
    User currentUser;

    public StockListFragment() {
    }

    public static StockListFragment newInstance() {
        if (stockListFragment == null) {
            stockListFragment = new StockListFragment();
        }
        return stockListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = PreferenceHelper.getInstance(requireContext()).getCurrentUser();
        setHasOptionsMenu(currentUser.isIsAdmin());
        stockListAdapter = new StockListAdapter(stocks, this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_stock, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_stock_adjust) {
            if (FranchiseRepository.getInstance().getFranchises() == null || FranchiseRepository.getInstance().getFranchises().isEmpty() ||
                    StudentsRepository.getInstance().getStudents() == null || StudentsRepository.getInstance().getStudents().isEmpty()) {
                UIUtils.showSnack(requireActivity(), "Please open students and franchise atleast once to get those data");
            } else {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.stockAdjustFragment, null);
            }
        } else if (item.getItemId() == R.id.menu_stock_adjust_report) {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.stockAdjustListFragment, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        stockListViewModel = new ViewModelProvider(this).get(StockListViewModel.class);
        //stockListViewModel.getAllStocks();
        stockListViewModel.getStockListLiveData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                stocks.addAll(listResource.data);
                stockListAdapter.notifyItemInserted(stocks.size() - 1);

            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
        stockListViewModel.getStockUpdateLiveData().observe(getViewLifecycleOwner(), listResource -> {
            //if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
            if (listResource.status == Status.SUCCESS) {
                if (stockUpdateFragment != null) {
                    stockUpdateFragment.dismiss();
                }
                showProgress(false);
                if (stocks.contains(listResource.data)) {
                    stocks.set(stocks.indexOf(listResource.data), listResource.data);
                    stockListAdapter.notifyItemChanged(stocks.indexOf(listResource.data));
                    UIUtils.showSnack(requireActivity(), listResource.message);
                    stockListViewModel.addTransaction(stockTransaction);
                }
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                if (stockUpdateFragment != null) {
                    stockUpdateFragment.dismiss();
                }
                showProgress(false);
                UIUtils.showToast(requireActivity(), listResource.message);
            }

            //StockRepository.getInstance().updateStock(listResource.data);
        });
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(stockListAdapter);
    }

    private void initViews() {

    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    @Override
    public void onStockClicked(Stock stock) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("stock", stock);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.transactionsFragment, bundle);
    }

    @Override
    public void onUpdateClicked(Stock stock) {
        stockUpdateFragment = StockUpdateFragment.newInstance();
        stockUpdateFragment.setStockClickListener(this);
        Bundle bundle = new Bundle();
        bundle.putParcelable("stock", stock);
        stockUpdateFragment.setArguments(bundle);
        stockUpdateFragment.show(getParentFragmentManager(), "");
    }

    @Override
    public void onStockAdded(Stock stock, int qtyInput, String vendor) {
        stockTransaction = new StockTransaction(stock.getName(), StockTransaction.TYPE.ADD.ordinal(), stock.getQuantity(), 0, qtyInput, UIUtils.getDate(), currentUser.getId(), vendor, currentUser.getState(), StockTransaction.OWNER_TYPE_VENDOR);
        stockListViewModel.updateStock(stock);
    }

    @Override
    public void onStockRemoved(Stock stock, int qtyInput, String vendor) {
        stockTransaction = new StockTransaction(stock.getName(), StockTransaction.TYPE.REMOVE.ordinal(), stock.getQuantity(), 0, qtyInput, UIUtils.getDate(), currentUser.getId(), vendor, currentUser.getState(), StockTransaction.OWNER_TYPE_VENDOR);
        stockListViewModel.updateStock(stock);
    }

    @Override
    public void onError(String s) {
        UIUtils.showSnack(requireActivity(), s);
    }

    @Override
    public void onStop() {
        super.onStop();
        stockListViewModel.getStockListLiveData().removeObservers(getViewLifecycleOwner());
    }
}