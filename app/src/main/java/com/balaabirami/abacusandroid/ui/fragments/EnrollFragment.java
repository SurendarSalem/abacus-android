package com.balaabirami.abacusandroid.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.ui.activities.AuthenticationActivity;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.activities.PaymentActivity;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.EnrollViewModel;
import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentEnrollBinding;
import com.balaabirami.abacusandroid.model.Item;
import com.balaabirami.abacusandroid.model.Program;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.ui.adapter.LevelAdapter;
import com.balaabirami.abacusandroid.utils.DateTextWatchListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnrollFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {


    FragmentEnrollBinding binding;
    List<String> states = new ArrayList<>();
    Student student = new Student();
    private EnrollViewModel enrollViewModel;
    private LevelAdapter levelAdapter;
    private ArrayAdapter<String> cityAdapter;
    List<String> items = new ArrayList<>();
    private String userId;
    private StockListViewModel stockListViewModel;
    List<Stock> stocks = new ArrayList<>();
    private User currentUser;

    public EnrollFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (!TextUtils.isEmpty(getArguments().getString("userId"))) {
                userId = getArguments().getString("userId");
                student.setFranchise(userId);
            }
        }
        currentUser = PreferenceHelper.getInstance(getContext()).getCurrentUser();
        student.setStudentId(User.createStudentID());
        student.setEnrollDate(UIUtils.getDate());
        student.setApproveDate(UIUtils.getDate());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enroll, container, false);
        binding.setUser(student);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        enrollViewModel = new ViewModelProvider(this).get(EnrollViewModel.class);
        enrollViewModel.getStates().observe(getViewLifecycleOwner(), states -> {
            if (states != null && !states.isEmpty()) {
                this.states = states;
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), R.layout.user_type_item, states);
                stateAdapter.setDropDownViewResource(R.layout.user_type_item);
                binding.spState.setAdapter(stateAdapter);
            }
        });
        enrollViewModel.getLevels().observe(getViewLifecycleOwner(), levels -> {
            if (levels != null && !levels.isEmpty()) {
                levelAdapter = new LevelAdapter(requireContext(), R.layout.user_type_custom_item, R.id.title, levels);
                //levelAdapter.setDropDownViewResource(R.layout.user_type_custom_item);
                binding.spLevels.setAdapter(levelAdapter);
            }
        });
        enrollViewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Status.SUCCESS) {
                showProgress(false);
                Snackbar.make(getView(), "Student Enrolled", BaseTransientBottomBar.LENGTH_SHORT).show();
                updateStock();
            } else if (result.status == Status.LOADING) {
                showProgress(true);
            } else {
                showProgress(false);
                Snackbar.make(getView(), result.message, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
        observeStockItems();
    }

    private void updateStock() {
        if (stocks != null && !stocks.isEmpty()) {
            enrollViewModel.updateStock(student, stocks, currentUser);
        }
    }

    private void observeStockItems() {
        stockListViewModel = new ViewModelProvider(this).get(StockListViewModel.class);
        stockListViewModel.getAllStocks();
        stockListViewModel.getStockListLiveData().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                stocks.addAll(listResource.data);
            }
        });
    }

    private void initViews() {
        binding.spState.setOnItemSelectedListener(this);
        binding.spCity.setOnItemSelectedListener(this);
        binding.etEnrollDate.addTextChangedListener(new DateTextWatchListener(binding.etEnrollDate));
        binding.etApprovedDate.addTextChangedListener(new DateTextWatchListener(binding.etApprovedDate));
        binding.spLevels.setOnItemSelectedListener(this);
        binding.rgProgram.setOnCheckedChangeListener(this);
        binding.rgCost.setOnCheckedChangeListener(this);
        binding.cbAbacus.setOnCheckedChangeListener(itemSelectListener);
        binding.cbAbility.setOnCheckedChangeListener(itemSelectListener);
        binding.cbBag.setOnCheckedChangeListener(itemSelectListener);
        binding.cbPencil.setOnCheckedChangeListener(itemSelectListener);
        binding.cbProgressCard.setOnCheckedChangeListener(itemSelectListener);
        binding.rbTshirt.setOnCheckedChangeListener(itemSelectListener);
        binding.rbTshirtSize8.setOnCheckedChangeListener(itemSelectListener);
        binding.rbTshirtSize12.setOnCheckedChangeListener(itemSelectListener);
        binding.rbTshirtSize16.setOnCheckedChangeListener(itemSelectListener);
        binding.btnRegister.setOnClickListener(view -> {
            if (Student.isValidForEnroll(student)) {
                UIUtils.hideKeyboardFrom(requireActivity());
                openPaymentActivityForResult();
            } else {
                UIUtils.hideKeyboardFrom(requireActivity());
                UIUtils.showSnack(requireActivity(), User.error);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == binding.spState.getId()) {
            if (i > 0) {
                student.setState(states.get(i));
                enrollViewModel.getCities(i - 1).observe(getViewLifecycleOwner(), s -> {
                    if (s != null && !s.isEmpty()) {
                        cityAdapter = new ArrayAdapter<>(requireContext(), R.layout.user_type_item, s);
                        cityAdapter.setDropDownViewResource(R.layout.user_type_item);
                        binding.spCity.setVisibility(View.VISIBLE);
                        binding.spCity.setAdapter(cityAdapter);
                    }
                });
            } else {
                student.setState(null);
                binding.spCity.setVisibility(View.GONE);
            }
        } else if (adapterView.getId() == binding.spCity.getId()) {
            if (i > 0) {
                student.setCity(cityAdapter.getItem(i));
            } else {
                student.setCity(null);
            }
        } else if (adapterView.getId() == binding.spLevels.getId()) {
            if (i == 0) {
                student.setLevel(null);
            } else if (i > 0 && levelAdapter != null) {
                student.setLevel(levelAdapter.getItem(i));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    CompoundButton.OnCheckedChangeListener itemSelectListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (checked) {
                if (compoundButton.getId() == R.id.cb_pencil) {
                    items.add(Item.Pencil.getName());
                } else if (compoundButton.getId() == R.id.cb_bag) {
                    items.add(Item.Bag.getName());
                } else if (compoundButton.getId() == R.id.cb_abacus) {
                    items.add(Item.Abacus.getName());
                } else if (compoundButton.getId() == R.id.cb_ability) {
                    items.add(Item.ListeningAbility.getName());
                } else if (compoundButton.getId() == R.id.cb_progress_card) {
                    items.add(Item.ProgressCard.getName());
                } else if (compoundButton.getId() == R.id.rb_tshirt) {
                    binding.rbTshirtSize8.setVisibility(View.VISIBLE);
                    binding.rbTshirtSize12.setVisibility(View.VISIBLE);
                    binding.rbTshirtSize16.setVisibility(View.VISIBLE);
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_8) {
                    items.remove(Item.SIZE.SIZE8.getSize());
                    items.add(Item.SIZE.SIZE8.getSize());
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_12) {
                    items.remove(Item.SIZE.SIZE12.getSize());
                    items.add(Item.SIZE.SIZE12.getSize());
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_16) {
                    items.remove(Item.SIZE.SIZE16.getSize());
                    items.add(Item.SIZE.SIZE16.getSize());
                }
            } else {
                if (compoundButton.getId() == R.id.cb_pencil) {
                    items.remove(Item.Pencil.getName());
                } else if (compoundButton.getId() == R.id.cb_bag) {
                    items.remove(Item.Bag.getName());
                } else if (compoundButton.getId() == R.id.cb_abacus) {
                    items.remove(Item.Abacus.getName());
                } else if (compoundButton.getId() == R.id.cb_ability) {
                    items.remove(Item.ListeningAbility.getName());
                } else if (compoundButton.getId() == R.id.cb_progress_card) {
                    items.remove(Item.ProgressCard.getName());
                } else if (compoundButton.getId() == R.id.rb_tshirt) {
                    binding.rbTshirtSize8.setOnCheckedChangeListener(null);
                    binding.rbTshirtSize8.setVisibility(View.GONE);
                    binding.rbTshirtSize8.setChecked(false);
                    binding.rbTshirtSize12.setOnCheckedChangeListener(null);
                    binding.rbTshirtSize12.setVisibility(View.GONE);
                    binding.rbTshirtSize12.setChecked(false);
                    binding.rbTshirtSize16.setOnCheckedChangeListener(null);
                    binding.rbTshirtSize16.setVisibility(View.GONE);
                    binding.rbTshirtSize16.setChecked(false);

                    binding.rbTshirtSize8.setOnCheckedChangeListener(itemSelectListener);
                    binding.rbTshirtSize12.setOnCheckedChangeListener(itemSelectListener);
                    binding.rbTshirtSize16.setOnCheckedChangeListener(itemSelectListener);

                    items.remove(Item.SIZE.SIZE8.getSize());
                    items.remove(Item.SIZE.SIZE12.getSize());
                    items.remove(Item.SIZE.SIZE16.getSize());
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_8) {
                    items.remove(Item.SIZE.SIZE8.getSize());
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_12) {
                    items.remove(Item.SIZE.SIZE12.getSize());
                } else if (compoundButton.getId() == R.id.rb_tshirt_size_16) {
                    items.remove(Item.SIZE.SIZE16.getSize());
                }
            }
            student.setItems(items);
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getId() == R.id.rg_program) {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_ma) {
                Program ma = new Program();
                ma.setCourse(Program.Course.MA);
                student.setProgram(ma);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_aa) {
                Program aa = new Program();
                aa.setCourse(Program.Course.AA);
                student.setProgram(aa);
            }
        } else if (radioGroup.getId() == R.id.rg_cost) {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_admission) {
                student.setCost("Admission");
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_level) {
                student.setCost("Level");
            }
        }
    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Intent data = result.getData();
                    enrollViewModel.enroll(student);
                }
            });

    public void openPaymentActivityForResult() {
        Intent intent = new Intent(requireContext(), PaymentActivity.class);
        intent.putExtra("order", student);
        someActivityResultLauncher.launch(intent);
    }
}