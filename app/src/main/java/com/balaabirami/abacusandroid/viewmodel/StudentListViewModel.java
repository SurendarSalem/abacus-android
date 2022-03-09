package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.repository.FranchiseRepository;
import com.balaabirami.abacusandroid.repository.StudentsRepository;

import java.util.List;

public class StudentListViewModel extends AndroidViewModel implements StudentListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<Student>> result = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Student>>> studentsListData = new MutableLiveData<>();
    StudentsRepository studentsRepository;

    public StudentListViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STUDENTS_REFERENCE);
        studentsRepository = StudentsRepository.getInstance();
    }

    public void getAllStudents(User currentUser) {
        result.setValue(Resource.loading(null));
        firebaseHelper.getAllStudents(currentUser, this);
    }

    public void approveStudent(Student student) {
        result.setValue(Resource.loading(null));
        firebaseHelper.approveStudent(student, nothing -> {
            result.setValue(Resource.success(student));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    public void updateStudent(Student student) {
        firebaseHelper.approveStudent(student, nothing -> {
            studentsRepository.updateStudent(student);
            result.setValue(Resource.success(student));
        }, e -> {
            result.setValue(Resource.error(e.getMessage(), null));
        });
    }

    @Override
    public void onStudentListLoaded(Student student) {
        result.setValue(Resource.success(student));
    }

    @Override
    public void onStudentListLoaded(List<Student> student) {
        studentsRepository.setStudents(student);
        studentsListData.setValue(Resource.success(student));
    }

    public MutableLiveData<Resource<Student>> getResult() {
        return result;
    }

    public MutableLiveData<Resource<List<Student>>> getStudentsListData(User currentUser) {
        List<Student> students = StudentsRepository.getInstance().getStudents();
        if (students == null || students.isEmpty()) {
            getAllStudents(currentUser);
        } else {
            studentsListData.setValue(Resource.loading(null));
            studentsListData.setValue(Resource.success(students));
        }
        return studentsListData;
    }
}