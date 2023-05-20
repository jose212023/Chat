package com.example.chat.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.chat.Chat;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private List<MainModel> inicioModelList;
    private Activity activity;
    public MainAdapter(List<MainModel> inicioModelList, Activity activity){this.inicioModelList = inicioModelList;
        this.activity = activity;}
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_view, parent, false);
        return new MainAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        holder.nombre.setText(inicioModelList.get(position).getNombre());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el modelo correspondiente al elemento clicado
                MainModel clickedChat = inicioModelList.get(holder.getAdapterPosition());

                // Pasar los datos necesarios a la actividad de Chat
                Intent intent = new Intent(activity, Chat.class);
                intent.putExtra("nombreUsuario", clickedChat.getNombre());
                intent.putExtra("idUsuario", clickedChat.getIdUsuario());
                // Agregar más datos según sea necesario

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return inicioModelList.size();
    }
    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView nombre;
        public ViewHolder(View v){
            super(v);
            nombre = (TextView) v.findViewById(R.id.txtNombreUsuarioConversaciones);
        }
    }
}
