package com.balaabirami.abacusandroid.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentStudentListBinding;
import com.balaabirami.abacusandroid.local.preferences.PreferenceHelper;
import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.LevelRepository;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.StudentListAdapter;
import com.balaabirami.abacusandroid.utils.FilterDialog;
import com.balaabirami.abacusandroid.utils.StudentsReportActivity;
import com.balaabirami.abacusandroid.utils.StateHelper;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.FranchiseListViewModel;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class StudentListFragment extends Fragment implements StudentListAdapter.StudentClickListener, FilterDialog.FilterListener {

    public static StudentListFragment studentListFragment;
    FragmentStudentListBinding binding;
    List<Student> allStudents = new ArrayList<>();
    StudentListViewModel studentListViewModel;
    private StudentListAdapter studentListAdapter;
    User currentUser;
    private FranchiseListViewModel franchiseListViewModel;
    private List<User> franchises;
    private FilterDialog filterDialog;
    private View view;

    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;
    private AlertDialog.Builder exportDialog;

    public StudentListFragment() {
    }

    public static StudentListFragment newInstance() {
        if (studentListFragment == null) {
            studentListFragment = new StudentListFragment();
        }
        return studentListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = PreferenceHelper.getInstance(requireContext()).getCurrentUser();
        //if (currentUser != null && currentUser.isIsAdmin()) {
        setHasOptionsMenu(true);
        //}
        studentListAdapter = new StudentListAdapter(this);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_logout);
        item.setVisible(false);
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
                List<State> states = StateHelper.getInstance().getStates(requireContext());
                if (currentUser.isIsAdmin()) {
                    franchiseListViewModel.getFranchiseListData().observe(getViewLifecycleOwner(), listResource -> {
                        if (listResource.data != null && listResource.status == Status.SUCCESS) {
                            franchises = listResource.data;
                            filterDialog.setAdapters(states, franchises, null, allStudents, LevelRepository.newInstance().getLevels(), null, false);
                        } else {
                        }
                    });
                } else {
                    filterDialog.setAdapters(states, null, null, allStudents, LevelRepository.newInstance().getLevels(), null, false);
                }

            }
            filterDialog.show();
            return true;
        } else if (item.getItemId() == R.id.menu_export) {
            showPrintDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_order) {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.ordersFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_student_list, container, false);
            view = binding.getRoot();
            initViews();
            observerData();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  initViews();
        observerData();*/
    }

    private void observerData() {
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
        franchiseListViewModel = new ViewModelProvider(this).get(FranchiseListViewModel.class);
        studentListViewModel.getStudentsListData(currentUser).observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.data != null && listResource.status == Status.SUCCESS) {
                if (listResource.data.isEmpty()) {
                    showProgress(false);
                    UIUtils.showToast(requireContext(), "No Students found!");
                } else {
                    allStudents.clear();
                    allStudents.addAll(listResource.data);
                    studentListAdapter.notifyList(allStudents);
                    showProgress(false);
                }
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
    }

    private void initViews() {
        binding.fabAdd.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("userId", PreferenceHelper.getInstance(requireContext()).getCurrentUser().getId());
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.enrollFragment, bundle);
        });
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(studentListAdapter);
        binding.svFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                studentListAdapter.filterSearch(text);
            }
        });

    }

    @Override
    public void onStudentClicked(Student student) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("student", student);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.studentDetailsFragment, bundle);
    }

    @Override
    public void onError(String error) {
        filterDialog.hide();
    }

    @Override
    public void onApproveClicked(Student student) {
        student.setApproved(true);
        studentListViewModel.approveStudent(student);
    }

    public void showProgress(boolean show) {
        ((HomeActivity) requireActivity()).showProgress(show);
    }

    @Override
    public void onFilterCleared() {
        filterDialog.clearAllFilter();
        filterDialog.hide();
        filterDialog = null;
        studentListAdapter.clearFilter(false);
    }

    @Override
    public void onFilterApplied(List<State> states, List<User> franchises, List<Stock> stocks, List<Student> students, List<Level> levels, List<Book> books, String[] dates) {
        if (filterDialog != null) {
            filterDialog.hide();
        }
        List<Student> filteredStudents = new ArrayList<>();
        if (states != null && allStudents != null) {
            for (Student student : allStudents) {
                for (State state : states) {
                    if (!filteredStudents.contains(student) && (student.getState() != null && student.getState().equalsIgnoreCase(state.getName()))) {
                        filteredStudents.add(student);
                    }
                }
            }
        }

        if (franchises != null && allStudents != null) {
            for (Student student : allStudents) {
                for (User franchise : franchises) {
                    if (!filteredStudents.contains(student) && (student.getFranchise() != null && student.getFranchise().equalsIgnoreCase(franchise.getId()))) {
                        filteredStudents.add(student);
                    }
                }
            }
        }

        if (students != null && allStudents != null) {
            for (Student student1 : allStudents) {
                for (Student student : students) {
                    if (!filteredStudents.contains(student) && (student1.getStudentId() != null && student1.getStudentId().equalsIgnoreCase(student.getStudentId()))) {
                        filteredStudents.add(student1);
                    }
                }
            }
        }

        if (franchises != null && allStudents != null) {
            for (Student student : allStudents) {
                for (Level level : levels) {
                    if (!filteredStudents.contains(student)
                            && (student.getLevel() != null && student.getLevel().getLevel() == level.getLevel())) {
                        filteredStudents.add(student);
                    }
                }
            }
        }

        if ((states == null || states.isEmpty()) && (franchises == null || franchises.isEmpty()) && (levels == null || levels.isEmpty()) && (students == null || students.isEmpty())) {
            filteredStudents.clear();
            filteredStudents.addAll(allStudents);
        }

        studentListAdapter.updateList(filteredStudents, true);
    }

    @Override
    public void onFilterSelected(List<String> states, List<String> franchises, List<String> stocks, List<String> students, List<String> levels, List<String> books, String[] dates) {

    }

    @Override
    public void onStop() {
        super.onStop();
        if (filterDialog != null) {
            filterDialog.clearAllFilter();
            filterDialog = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        if (permsRequestCode == 200) {
            boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (writeAccepted && readAccepted) {
                if (studentListAdapter != null && studentListAdapter.getStudents() != null && !studentListAdapter.getStudents().isEmpty()) {
                    Intent intent = new Intent(requireActivity(), StudentsReportActivity.class);
                    StudentsReportActivity.students = studentListAdapter.getStudents();
                    requireActivity().startActivity(intent);
                } else {
                    UIUtils.showSnack(getActivity(), "No Students to export");
                }
            }
        }
    }
}