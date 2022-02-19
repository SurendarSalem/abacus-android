package com.balaabirami.abacusandroid.ui.fragments;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balaabirami.abacusandroid.databinding.StudentItemBinding;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;


public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private final List<Student> students;
    StudentClickListener studentClickListener;

    public StudentListAdapter(List<Student> students, StudentClickListener studentClickListener) {
        this.students = students;
        this.studentClickListener = studentClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(StudentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.student = students.get(position);
        holder.tvName.setText(students.get(position).getName());
        holder.tvEmail.setText(students.get(position).getEmail());
        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentClickListener.onStudentClicked(students.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvEmail;
        public final AppCompatButton btnOrder;
        public Student student;

        public ViewHolder(StudentItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.itemNumber;
            tvEmail = binding.content;
            btnOrder = binding.btnOrder;
        }
    }

    interface StudentClickListener {
        void onStudentClicked(Student student);
    }
}