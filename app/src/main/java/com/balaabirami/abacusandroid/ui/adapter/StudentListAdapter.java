package com.balaabirami.abacusandroid.ui.adapter;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balaabirami.abacusandroid.databinding.StudentItemBinding;
import com.balaabirami.abacusandroid.model.Student;

import java.util.ArrayList;
import java.util.List;


public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private List<Student> students = new ArrayList<>(), filteredStudents = new ArrayList<>(), filteredStudentsCopy = new ArrayList<>(),
            searchedStudents = new ArrayList<>();
    StudentClickListener studentClickListener;

    public StudentListAdapter(StudentClickListener studentClickListener) {
        this.studentClickListener = studentClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(StudentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.student = filteredStudents.get(position);
        holder.tvName.setText(filteredStudents.get(position).getName());
        holder.tvEmail.setText(filteredStudents.get(position).getEmail());
        holder.btnOrder.setOnClickListener(view -> studentClickListener.onStudentClicked(filteredStudents.get(position)));
        holder.btnApprove.setOnClickListener(view -> studentClickListener.onApproveClicked(filteredStudents.get(position)));
        holder.itemView.setOnClickListener(view -> studentClickListener.onStudentClicked(filteredStudents.get(position)));
        holder.tvStudentId.setText("ID - " + holder.student.getStudentId());
    }

    @Override
    public int getItemCount() {
        return filteredStudents.size();
    }

    public void updateList(List<Student> list, boolean createDuplicateCopy) {
        filteredStudents.clear();
        filteredStudents.addAll(list);
        if (createDuplicateCopy) {
            filteredStudentsCopy.clear();
            filteredStudentsCopy.addAll(filteredStudents);
        }
        notifyDataSetChanged();
    }

    public void notifyList(List<Student> students) {
        this.filteredStudents.clear();
        this.students.clear();
        this.students.addAll(students);
        filteredStudents.addAll(students);
        filteredStudentsCopy.clear();
        filteredStudentsCopy.addAll(students);
        notifyDataSetChanged();
    }

    public void filterSearch(String text) {

        if (text == null || text.isEmpty()) {
            searchedStudents.clear();
            clearFilter(true);
        } else {
            searchedStudents.clear();
            if (filteredStudentsCopy.isEmpty()) {
                filteredStudentsCopy.addAll(filteredStudents);
            }
            for (Student student : filteredStudentsCopy) {
                if (student.getName() == null) {
                    Log.d("Suren", student.getStudentId());
                }
                if (student.getName().toLowerCase().contains(text.toLowerCase())
                        || student.getStudentId().toLowerCase().contains(text.toLowerCase())
                        || (student.getContactNo() != null && student.getContactNo().contains(text))) {
                    searchedStudents.add(student);
                }
            }
            updateList(searchedStudents, false);
        }
    }

    public void clearFilter(boolean bySearch) {
        if (bySearch) {
            updateList(filteredStudentsCopy, true);
        } else {
            filteredStudents.clear();
            filteredStudents.addAll(students);
            filteredStudentsCopy.clear();
            filteredStudentsCopy.addAll(students);
            searchedStudents.clear();
            updateList(students, true);
        }
    }

    public List<Student> getStudents() {
        return filteredStudents;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvStudentId;
        public final TextView tvEmail;
        public final AppCompatButton btnOrder, btnApprove;
        public Student student;

        public ViewHolder(StudentItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.itemNumber;
            tvEmail = binding.content;
            btnOrder = binding.btnOrder;
            btnApprove = binding.btnApprove;
            tvStudentId = binding.tvStudentid;
        }
    }

    public interface StudentClickListener {
        void onStudentClicked(Student student);

        void onError(String error);

        void onApproveClicked(Student student);
    }
}