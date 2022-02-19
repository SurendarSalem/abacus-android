package com.balaabirami.abacusandroid.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentLoginBinding;
import com.balaabirami.abacusandroid.databinding.FragmentLoginBindingImpl;
import com.balaabirami.abacusandroid.databinding.FragmentSignupBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.AuthenticationActivity;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.LoginViewModel;
import com.balaabirami.abacusandroid.viewmodel.SignupViewModel;

import java.util.Objects;

public class LoginFragment extends Fragment {


    FragmentLoginBinding binding;
    User user = new User();
    private LoginViewModel loginViewModel;

    public LoginFragment() {
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
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        initViews();
        observe();
    }

    private void observe() {
        loginViewModel.getResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Status.SUCCESS) {
                showProgress(false);
                NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
                Bundle bundle = new Bundle();
                String userId = PreferenceHelper.getInstance(requireContext()).getUserId();
                bundle.putString("userId", userId);
                //navController.navigate(R.id.studentListFragment, bundle);
                Intent intent = new Intent(requireContext(), HomeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else if (resource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showSnack(getActivity(), resource.message);
            } else if (resource.status == Status.LOADING) {
                showProgress(true);
            }
        });
    }

    private void initViews() {
        binding.tvRegister.setOnClickListener(view -> {
            Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.my_nav_host_fragment).navigate(R.id.signupFragment);
        });
        binding.btnLogin.setOnClickListener(view -> {
            if (User.isValid(user)) {
                UIUtils.hideKeyboardFrom(getActivity());
                loginViewModel.login(user);
            } else {
                UIUtils.hideKeyboardFrom(getActivity());
                UIUtils.showSnack(Objects.requireNonNull(getActivity()), User.error);
            }
        });
    }

    public void showProgress(boolean show) {
        ((AuthenticationActivity) getActivity()).showProgress(show);
    }
}
