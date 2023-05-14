package com.example.chat.crearChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;

import java.util.List;

public class CrearChatAdapter extends RecyclerView.Adapter<CrearChatAdapter.ViewHolder>{
    private List<CrearChatModel> crearChatModelList;
    private Context context;

    public CrearChatAdapter(List<CrearChatModel> crearChatModelList, Context context){
        this.crearChatModelList = crearChatModelList;
        this.context = context;
    }

    @Override
    public CrearChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_view, parent, false);
        return new CrearChatAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CrearChatAdapter.ViewHolder holder, int position) {
        holder.nombre.setText(crearChatModelList.get(position).getNombre());
    }
    @Override
    public int getItemCount() {
        return crearChatModelList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView nombre;
        public ViewHolder(View v){
            super(v);
            nombre = (TextView) v.findViewById(R.id.txtUsuarios);
        }
    }
}
