package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentSignupBinding;
import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.AuthenticationActivity;
import com.balaabirami.abacusandroid.utils.DateTextWatchListener;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.SignupViewModel;
import com.google.firebase.FirebaseApp;

import java.util.Calendar;
import java.util.Objects;

public class SignupFragment extends Fragment {


    FragmentSignupBinding binding;
    User franchise = new User();
    SignupViewModel signupViewModel;

    public SignupFragment() {
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        franchise.setId(User.createFranchiseID());
        franchise.setRegisterDate(UIUtils.getDate());
        franchise.setAccountType(User.TYPE_FRANCHISE);
        binding.setFranchise(franchise);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        initViews();
        binding.btnRegister.setOnClickListener(btn -> {
            if (User.isValidForSignup(franchise)) {
                UIUtils.hideKeyboardFrom(getActivity());
                signupUser();
            } else {
                UIUtils.hideKeyboardFrom(getActivity());
                UIUtils.showSnack(Objects.requireNonNull(getActivity()), User.error);
            }
        });
        observe();
    }

    private void observe() {
        signupViewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                showProgress(false);
                UIUtils.showToast(requireContext(), "Signup successful!");
                NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
                navController.popBackStack();
            } else if (result.status == Status.LOADING) {
                showProgress(true);
            } else if (result.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), result.message);
            }
        });
    }

    private void signupUser() {
        Log.d("Suren", franchise.toString());
        signupViewModel.signup(franchise);
    }

    private void initViews() {
        binding.etDate.addTextChangedListener(new DateTextWatchListener(binding.etDate));
    }

    public void showProgress(boolean show) {
        ((AuthenticationActivity) getActivity()).showProgress(show);
    }
}