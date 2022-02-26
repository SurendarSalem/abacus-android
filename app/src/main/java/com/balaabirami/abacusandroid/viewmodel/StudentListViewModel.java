package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class StudentListViewModel extends AndroidViewModel implements StudentListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<Student>> result = new MutableLiveData<>();


    public StudentListViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.STUDENTS_REFERENCE);
    }

    public void getAllStudents() {
        result.setValue(Resource.loading(null));
        firebaseHelper.getAllStudents(this);
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
        //result.setValue(Resource.success(student));
    }

    public MutableLiveData<Resource<Student>> getResult() {
        return result;
    }
}