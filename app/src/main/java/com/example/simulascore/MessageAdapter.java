package com.example.simulascore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessagesA> messages;

    public MessageAdapter(List<MessagesA> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessagesA message = messages.get(position);

        if (message.isSent()) {
            holder.sentMessageLayout.setVisibility(View.VISIBLE);
            holder.receivedMessageLayout.setVisibility(View.GONE);
            holder.textViewSentMessage.setText(message.getMessageContent());
            holder.textViewSentDate.setText(message.getFechaHora()); // Mostrar fecha y hora para el mensaje enviado
        } else {
            holder.receivedMessageLayout.setVisibility(View.VISIBLE);
            holder.sentMessageLayout.setVisibility(View.GONE);
            holder.textViewReceivedMessage.setText(message.getMessageContent());
            holder.textViewReceivedDate.setText(message.getFechaHora()); // Mostrar fecha y hora para el mensaje recibido
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout receivedMessageLayout;
        public LinearLayout sentMessageLayout;
        public TextView textViewReceivedMessage;
        public TextView textViewSentMessage;
        public TextView textViewReceivedDate;
        public TextView textViewSentDate;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMessageLayout = itemView.findViewById(R.id.received_message_layout);
            sentMessageLayout = itemView.findViewById(R.id.sent_message_layout);
            textViewReceivedMessage = itemView.findViewById(R.id.textView_received_message);
            textViewSentMessage = itemView.findViewById(R.id.textView_sent_message);
            textViewReceivedDate = itemView.findViewById(R.id.textView_received_date);
            textViewSentDate = itemView.findViewById(R.id.textView_sent_date);
        }
    }
}
