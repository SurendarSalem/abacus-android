package com.balaabirami.abacusandroid.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.databinding.FragmentStudentListBinding;
import com.balaabirami.abacusandroid.model.Status;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.ui.activities.HomeActivity;
import com.balaabirami.abacusandroid.ui.adapter.StudentListAdapter;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.balaabirami.abacusandroid.viewmodel.StudentListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class StudentListFragment extends Fragment implements StudentListAdapter.StudentClickListener {

    public static StudentListFragment studentListFragment;
    FragmentStudentListBinding binding;
    List<Student> students = new ArrayList<>();
    StudentListViewModel studentListViewModel;
    private StudentListAdapter studentListAdapter;

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
        studentListAdapter = new StudentListAdapter(students, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_student_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        studentListViewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
        studentListViewModel.getAllStudents();
        studentListViewModel.getResult().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource.status == Status.SUCCESS) {
                showProgress(false);
                if (!students.contains(listResource.data)) {
                    students.add(listResource.data);
                    studentListAdapter.notifyItemInserted(students.size() - 1);
                }
            } else if (listResource.status == Status.LOADING) {
                showProgress(true);
            } else if (listResource.status == Status.ERROR) {
                showProgress(false);
                UIUtils.showToast(requireContext(), listResource.message);
            }
        });
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(studentListAdapter);
    }

    private void initViews() {
        binding.fabAdd.setOnClickListener(view -> {
            Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment_activity_home).navigate(R.id.enrollFragment, getArguments());
        });

    }

    @Override
    public void onStudentClicked(Student student) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("student", student);
        //Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment_activity_home).navigate(R.id.orderFragment, bundle);

        Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment_activity_home).navigate(R.id.studentDetailsFragment, bundle);
    }

    @Override
    public void onApproveClicked(Student student) {
        student.setApproved(true);
        studentListViewModel.approveStudent(student);
    }

    public void showProgress(boolean show) {
        ((HomeActivity) getActivity()).showProgress(show);
    }
}