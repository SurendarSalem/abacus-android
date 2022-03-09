package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentFranchiseListBinding;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.FranchiseListAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class FranchiseListFragment extends Fragment implements FranchiseListAdapter.FranchiseClickListener {

    public static FranchiseListFragment franchiseListFragment;
    FragmentFranchiseListBinding binding;
    List<User> franchises = new ArrayList<>();
    FranchiseListViewModel franchiseListViewModel;
    private FranchiseListAdapter franchiseListAdapter;

    public FranchiseListFragment() {
    }

    public static FranchiseListFragment newInstance() {
        if (franchiseListFragment == null) {
            franchiseListFragment = new FranchiseListFragment();
        }
        return franchiseListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        franchiseListAdapter = new FranchiseListAdapter(franchises, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_franchise_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        franchiseListViewModel = new ViewModelProvider(this).get(FranchiseListViewModel.class);
        franchiseListViewModel.getFranchiseListData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                if (listResource.data != null && !listResource.data.isEmpty()) {
                    franchises.clear();
                    franchises.addAll(listResource.data);
                    franchises.remove(0);
                    franchiseListAdapter.notifyItemRangeInserted(0, franchises.size());
                }
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
        franchiseListViewModel.getFranchiseUpdateData().observe(getViewLifecycleOwner(), listResource -> {
            if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                if (listResource.status == Status.SUCCESS) {
                    showProgress(false);
                    if (franchises.contains(listResource.data)) {
                        int index = franchises.indexOf(listResource.data);
                        franchises.set(index, listResource.data);
                        franchiseListAdapter.notifyDataSetChanged();
                    }
                } else if (listResource.status == Status.LOADING) {
                    showProgress(true);
                } else if (listResource.status == Status.ERROR) {
                    showProgress(false);
                    UIUtils.showToast(requireContext(), listResource.message);
                }
            }
        });
        binding.rvFranchises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFranchises.setAdapter(franchiseListAdapter);
    }


    public void showProgress(boolean show) {
        ((HomeActivity) getActivity()).showProgress(show);
    }

    @Override
    public void onFranchiseClicked(User franchise) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("franchise", franchise);
        Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment_activity_home).navigate(R.id.franchiseDetailsFragment, bundle);
    }

    @Override
    public void onApproveClicked(User franchise) {
        franchiseListViewModel.approveFranchise(franchise);
    }

    @Override
    public void onStop() {
        super.onStop();
        franchiseListViewModel.getFranchiseListData().removeObservers(getViewLifecycleOwner());
    }
}