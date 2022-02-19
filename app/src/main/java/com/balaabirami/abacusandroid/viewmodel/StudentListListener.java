package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public interface StudentListListener {
    void onStudentListLoaded(Student student);

    void onStudentListLoaded(List<Student> student);
}
