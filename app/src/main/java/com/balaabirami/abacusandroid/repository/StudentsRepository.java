package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public class StudentsRepository {

    static StudentsRepository repository;
    private List<Student> students;

    public static StudentsRepository getInstance() {
        if (repository == null) {
            repository = new StudentsRepository();
        }
        return repository;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void updateStudent(Student student) {
        int index = students.indexOf(student);
        students.set(index, student);
    }

    public void clear() {
        setStudents(null);
        repository = null;
    }
}
