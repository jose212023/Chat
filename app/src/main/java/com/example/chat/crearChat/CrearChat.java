package com.example.chat.crearChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.chat.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CrearChat extends AppCompatActivity {

    private EditText buscar;
    private RecyclerView recyclerView;
    private List<CrearChatModel> crearChatList;
    private CrearChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buscar Usuarios");

        buscar = findViewById(R.id.txtBuscar);
        recyclerView = findViewById(R.id.recycler_viewUsuarios);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        crearChatList = new ArrayList<>();
        mAdapter = new CrearChatAdapter(crearChatList, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        listener();

        mAdapter.setOnItemClickListener(new CrearChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Obtén el item seleccionado
                CrearChatModel usuarioSeleccionado = crearChatList.get(position);

                // Realiza la acción deseada al seleccionar el item
                // Por ejemplo, puedes iniciar una nueva actividad pasando algún dato extra

                Intent intent = new Intent(CrearChat.this, Chat.class);
                intent.putExtra("nombreUsuario", usuarioSeleccionado.getNombre());
                intent.putExtra("idUsuario", usuarioSeleccionado.getIdUsuario());
                startActivity(intent);
            }
        });
    }

    public void listener() {
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Método antes de que se realicen cambios en el texto del EditText
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Método que se ejecuta mientras el texto del EditText cambia
                buscarUsuarios(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Método después de que el texto del EditText ha cambiado
            }
        });
    }

    public void buscarUsuarios(String letra) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        String idUsuarioActual = preferences.getString("IdUser", "");

        db.collection("Usuarios")
                .orderBy("Nombre")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            crearChatList.clear(); // Limpia la lista de usuarios

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtiene el id del usuario encontrado
                                String idUsuario = document.getString("Id Usuario");

                                // Verifica si el usuario encontrado no es el usuario actual
                                if (!idUsuario.equals(idUsuarioActual)) {
                                    // Obtiene el nombre de cada usuario encontrado
                                    String nombreUsuario = document.getString("Nombre");
                                    CrearChatModel usuario = new CrearChatModel(nombreUsuario, idUsuario);

                                    if (letra.isEmpty() || nombreUsuario.toLowerCase().contains(letra.toLowerCase())) {
                                        // Si no se ingresó ninguna letra o el nombre del usuario contiene la letra ingresada, agrega el usuario a la lista
                                        crearChatList.add(usuario);
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            // Ocurrió un error al buscar usuarios
                            Toast.makeText(CrearChat.this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}