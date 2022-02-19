package com.balaabirami.abacusandroid.firebase;

import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;
import com.balaabirami.abacusandroid.viewmodel.StudentListListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    DatabaseReference databaseReference;
    public static String USER_REFERENCE = "users";
    public static String STUDENTS_REFERENCE = "students";
    public static String ORDER_REFERENCE = "orders";
    FirebaseAuth mAuth;
    private StudentListListener studentListListener;

    public void init(String path) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference(path);
        mAuth = FirebaseAuth.getInstance();
    }

    public void addUser(User user, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        databaseReference.child(user.createID()).setValue(user).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void enrollStudent(Student student, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        databaseReference.child(student.createID()).setValue(student).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void order(Order order, OnSuccessListener<Void> successListener, OnFailureListener onFailureListener) {
        databaseReference.child(order.getOrderId()).setValue(order).addOnSuccessListener(successListener).addOnFailureListener(onFailureListener);
    }

    public void createUser(User user, OnCompleteListener onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(onCompleteListener);
    }

    public void login(User user, OnCompleteListener onCompleteListener) {
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(onCompleteListener);
    }

    public void getAllStudents(StudentListListener studentListListener) {
        this.studentListListener = studentListListener;
        databaseReference.addChildEventListener(childEventListener);
        //this.studentListListener.onStudentListLoaded(Student.dummy());
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Student student = dataSnapshot.getValue(Student.class);
            studentListListener.onStudentListLoaded(student);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Toast.makeText(mContext, "Failed to load data.", Toast.LENGTH_SHORT).show();
        }
    };


}
