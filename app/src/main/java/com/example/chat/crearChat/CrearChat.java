package com.example.chat.crearChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.R;
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

        buscar = findViewById(R.id.txtBuscar);
        recyclerView = findViewById(R.id.recycler_viewUsuarios);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        crearChatList = new ArrayList<>();
        mAdapter = new CrearChatAdapter(crearChatList, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        listener();
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

        if (letra.isEmpty()) {
            // Si la cadena de búsqueda está vacía, limpiar la lista y notificar al adaptador
            crearChatList.clear();
            mAdapter.notifyDataSetChanged();
        } else {
            db.collection("Usuarios")
                    .orderBy("Nombre")
                    .startAt(letra)
                    .endAt(letra + "\uf8ff")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                crearChatList.clear(); // Limpia la lista de usuarios

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Obtiene el nombre de cada usuario encontrado
                                    String nombreUsuario = document.getString("Nombre");
                                    CrearChatModel usuario = new CrearChatModel(nombreUsuario);
                                    crearChatList.add(usuario); // Agrega el nombre a la lista
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                // Ocurrió un error al buscar usuarios
                                Toast.makeText(CrearChat.this, "Error al buscar usuarios", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}