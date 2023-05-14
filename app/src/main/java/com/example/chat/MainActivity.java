package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chat.crearChat.CrearChat;
import com.example.chat.main.MainAdapter;
import com.example.chat.main.MainModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<MainModel> mainList = new ArrayList<>();

    public RecyclerView.Adapter mAdapter;

    private RecyclerView recyclerView;
    private FloatingActionButton agregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        agregar = (FloatingActionButton) findViewById(R.id.btnagregar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_viewChats);

        /*recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        mAdapter = new MainAdapter(mainList, getApplicationContext());
        recyclerView.setAdapter(mAdapter);*/
        listener();
    }

    public void listener(){
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CrearChat.class);
                startActivity(intent);
            }
        });
    }

    public void obtenerChats(){

    }

}