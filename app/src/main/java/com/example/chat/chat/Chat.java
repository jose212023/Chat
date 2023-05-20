package com.example.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.crearChat.CrearChatAdapter;
import com.example.chat.crearChat.CrearChatModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private EditText mensaje;
    private RecyclerView recyclerView;
    private List<ChatModel> chatList;
    private ChatAdapter mAdapter;
    private ImageButton enviar;
    private String idOtherUser, userName, id;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("nombreUsuario")) {
            String nombreUsuario = intent.getStringExtra("nombreUsuario");
            idOtherUser = intent.getStringExtra("idUsuario");
            getSupportActionBar().setTitle(nombreUsuario);
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
        userName = sharedPreferences.getString("UserName", "valor_por_defecto");
        id = sharedPreferences.getString("IdUser", "valor_por_defecto");

        mensaje = (EditText) findViewById(R.id.txtMensaje);
        enviar = (ImageButton) findViewById(R.id.btnEnviar);
        recyclerView = findViewById(R.id.recycler_viewChat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList, getApplicationContext(), id);
        recyclerView.setAdapter(mAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Conversaciones");

        listener();

    }

    public void listener(){
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });
    }

    public void enviarMensaje(){

        String messageText = mensaje.getText().toString().trim();
        if (!messageText.isEmpty()) {

            String conversationId;

            // Comparar los IDs en orden lexicográfico
            int comparisonResult = id.compareTo(idOtherUser);

            // Formar el conversationId en función del resultado de la comparación
            if (comparisonResult < 0) {
                // El id es menor que idOtherUser, mantener el mismo orden
                conversationId = id + "_" + idOtherUser;
            } else if (comparisonResult > 0) {
                // El id es mayor que idOtherUser, invertir el orden
                conversationId = idOtherUser + "_" + id;
            } else {
                // Los IDs son iguales, manejar este caso según tus necesidades
                // Por ejemplo, puedes generar un mensaje de error o elegir una estrategia específica
                conversationId = ""; // Asigna un valor adecuado para este caso
            }

            // Verificar si la conversación ya existe
            DatabaseReference conversationReference = databaseReference.child(conversationId);
            conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        // La conversación no existe, crearla y agregar el primer mensaje
                        ChatModel message = new ChatModel(userName, messageText, id);
                        DatabaseReference newConversationReference = databaseReference.child(conversationId);
                        newConversationReference.push().setValue(message);
                    } else {
                        // La conversación ya existe, simplemente agregar el nuevo mensaje
                        ChatModel message = new ChatModel(userName, messageText, id);
                        conversationReference.push().setValue(message);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar el error en caso de que la lectura de la base de datos falle
                }
            });

            mensaje.setText("");

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = this.getSharedPreferences("ConversationId", MODE_PRIVATE);
        String conversationIdOriginal = sharedPreferences.getString("ConversationId", "valor_por_defecto");

        String conversationId;

        int comparisonResult = id.compareTo(idOtherUser);

        // Formar el conversationId en función del resultado de la comparación
        if (comparisonResult < 0) {
            // El id es menor que idOtherUser, mantener el mismo orden
            conversationId = id + "_" + idOtherUser;
        } else if (comparisonResult > 0) {
            // El id es mayor que idOtherUser, invertir el orden
            conversationId = idOtherUser + "_" + id;
        } else {
            // Los IDs son iguales, manejar este caso según tus necesidades
            // Por ejemplo, puedes generar un mensaje de error o elegir una estrategia específica
            conversationId = ""; // Asigna un valor adecuado para este caso
        }

        DatabaseReference conversationReference = databaseReference.child(conversationId);

        conversationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    ChatModel message = messageSnapshot.getValue(ChatModel.class);
                    chatList.add(message);
                }
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1); // Desplazar automáticamente hacia el último mensaje agregado
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error en caso de que la lectura de la base de datos falle
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