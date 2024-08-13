package com.example.simulascore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private final List<UserNotification> notifications;

    public NotificationsAdapter(List<UserNotification> userNotifications) {
        this.notifications = userNotifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        UserNotification userNotification = notifications.get(position);
        holder.bind(userNotification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView contenidoTextView;
        private final TextView profesorTextView;
        private final TextView fechaHoraTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contenidoTextView = itemView.findViewById(R.id.textView_contenido);
            profesorTextView = itemView.findViewById(R.id.textView_profesor);
            fechaHoraTextView = itemView.findViewById(R.id.textView_fecha_hora);
        }

        public void bind(UserNotification userNotification) {
            contenidoTextView.setText(userNotification.getContenido());
            profesorTextView.setText(userNotification.getProfesor());
            fechaHoraTextView.setText(userNotification.getFechaHora());
        }
    }
}
