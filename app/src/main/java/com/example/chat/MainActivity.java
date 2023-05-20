package com.example.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.chat.crearChat.CrearChat;
import com.example.chat.main.MainAdapter;
import com.example.chat.main.MainModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<MainModel> mainList;

    public RecyclerView.Adapter mAdapter;

    private RecyclerView recyclerView;
    private FloatingActionButton agregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        agregar = (FloatingActionButton) findViewById(R.id.btnagregar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_viewChats);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        mainList = new ArrayList<>();
        mAdapter = new MainAdapter(mainList, MainActivity.this);
        recyclerView.setAdapter(mAdapter);

        obtenerChats();

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
        SharedPreferences preferences = getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String idUsuarioActual = preferences.getString("IdUser", "valor_por_defecto");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference().child("Conversaciones");

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    String[] ids = chatId.split("_");
                    String idUsuario1 = ids[0];
                    String idUsuario2 = ids[1];
                    String idOtroUsuario;

                    // Verificar si el ID actual coincide con uno de los ID de la conversación
                    if (idUsuarioActual.equals(idUsuario1)) {
                        idOtroUsuario = idUsuario2;
                    } else if (idUsuarioActual.equals(idUsuario2)) {
                        idOtroUsuario = idUsuario1;
                    } else {
                        continue; // El ID actual no está involucrado en la conversación, continuar con la siguiente
                    }

                    db.collection("Usuarios").document(idOtroUsuario)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String nombre = documentSnapshot.getString("Nombre");
                                    String id = documentSnapshot.getString("Id Usuario");
                                    MainModel chat = new MainModel(nombre, id);
                                    mainList.add(chat);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error al obtener los nombres de los chats", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error en caso de que la lectura de la base de datos falle
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Realizar el cierre de sesión y regresar a la actividad de inicio de sesión (login)
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Eliminar los datos de usuario de las preferencias compartidas
        SharedPreferences preferences = getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences prefs = getSharedPreferences("MiAplicacionPrefs", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.clear();
        editor2.apply();

        // Abrir la actividad de inicio de sesión (login)
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish(); // Finalizar la actividad actual para que no se pueda volver atrás
    }

}