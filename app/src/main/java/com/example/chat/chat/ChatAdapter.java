package com.example.chat.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private List<ChatModel> chatModelList;
    private Context context;
    private String id;

    public ChatAdapter(List<ChatModel> chatModelList, Context context, String id){
        this.chatModelList = chatModelList;
        this.context = context;
        this.id = id;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble, parent, false);
        return new ChatAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatModel chat = chatModelList.get(position);

        Log.d("Ids", chat.getId() + id);

        if (chat.getId().equals(id)) {
            // El mensaje fue enviado por el usuario actual
            holder.msgContainerSent.setVisibility(View.VISIBLE);
            holder.msgContainerReceived.setVisibility(View.GONE);

            holder.txtMessageSent.setText(chat.getMensaje());

        } else {
            // El mensaje fue recibido del otro usuario
            holder.msgContainerSent.setVisibility(View.GONE);
            holder.msgContainerReceived.setVisibility(View.VISIBLE);

            holder.txtMessageReceived.setText(chat.getMensaje());

        }
    }
    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView txtMessageSent, txtMessageReceived;
        private LinearLayout msgContainerSent, msgContainerReceived;
        public ViewHolder(View v){
            super(v);
            txtMessageSent = (TextView) v.findViewById(R.id.txtMessageSent);
            txtMessageReceived = (TextView) v.findViewById(R.id.txtMessageReceived);
            msgContainerSent = (LinearLayout) v.findViewById(R.id.msgContainerSent);
            msgContainerReceived = (LinearLayout) v.findViewById(R.id.msgContainerReceived);
        }
    }
}
