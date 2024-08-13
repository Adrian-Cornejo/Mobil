package com.example.simulascore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {

    private List<Student> studentList;
    private List<Student> studentListFull; // Lista completa para el filtrado
    private OnItemClickListener listener;

    // Interfaz para manejar los clics en los elementos
    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    // Constructor del adaptador
    public StudentAdapter(List<Student> studentList, OnItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
        this.studentListFull = new ArrayList<>(studentList); // Copia completa de la lista
    }

    public void updateStudents(List<Student> students) {
        this.studentList.clear();
        this.studentList.addAll(students);
        this.studentListFull.clear();
        this.studentListFull.addAll(students); // Actualiza la lista completa también
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada elemento del RecyclerView
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        // Obtener el alumno de la lista y establecer los datos en las vistas
        Student student = studentList.get(position);
        holder.tvNombreAlumno.setText(student.getName() + " " + student.getSurname());
        holder.tvCodigoAlumno.setText(student.getCode());
        holder.tvEmailAlumno.setText(student.getEmail());

        // Manejar el clic en el botón de enviar mensaje
        holder.btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    @Override
    public Filter getFilter() {
        return studentFilter;
    }

    private Filter studentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Student> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(studentListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Student item : studentListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getSurname().toLowerCase().contains(filterPattern) ||
                            item.getEmail().toLowerCase().contains(filterPattern) ||
                            item.getCode().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            studentList.clear();
            studentList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // ViewHolder interno que describe los elementos del RecyclerView
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombreAlumno;
        public TextView tvCodigoAlumno;
        public TextView tvEmailAlumno;
        public ImageButton btnEnviarMensaje;

        public StudentViewHolder(View view) {
            super(view);
            tvNombreAlumno = view.findViewById(R.id.tv_nombre_alumno);
            tvCodigoAlumno = view.findViewById(R.id.tv_codigo_alumno);
            tvEmailAlumno = view.findViewById(R.id.tv_email_alumno);
            btnEnviarMensaje = view.findViewById(R.id.btn_enviar_mensaje);
        }
    }
}
