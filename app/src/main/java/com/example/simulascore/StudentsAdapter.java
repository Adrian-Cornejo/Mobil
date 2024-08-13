package com.example.simulascore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {

    private List<Student> students;

    public StudentsAdapter(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void updateStudents(List<Student> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }

    public List<Student> getSelectedStudents() {
        List<Student> selectedStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.isSelected()) {
                selectedStudents.add(student);
            }
        }
        return selectedStudents;
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCode, textViewName, textViewEmail;
        private CheckBox checkBoxSelect;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCode = itemView.findViewById(R.id.textViewCode);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            checkBoxSelect = itemView.findViewById(R.id.checkBoxSelect);

            checkBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Student student = students.get(getAdapterPosition());
                student.setSelected(isChecked);
            });
        }

        public void bind(Student student) {
            textViewCode.setText(student.getCode());
            textViewName.setText(student.getName() + " " + student.getSurname());
            textViewEmail.setText(student.getEmail());
            checkBoxSelect.setChecked(student.isSelected());
        }
    }
}
