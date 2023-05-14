package com.example.chat.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private List<MainModel> inicioModelList;
    private Context context;
    public MainAdapter(List<MainModel> inicioModelList, Context context){this.inicioModelList = inicioModelList;
        this.context = context;}
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_view, parent, false);
        return new MainAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        holder.nombre.setText(inicioModelList.get(position).getNombre());
    }
    @Override
    public int getItemCount() {
        return inicioModelList.size();
    }
    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView nombre;
        public ViewHolder(View v){
            super(v);
            nombre = (TextView) v.findViewById(R.id.txtNombreOtroUsuarioChat);
        }
    }
}
