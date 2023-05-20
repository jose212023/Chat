package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Registrar extends AppCompatActivity {

    EditText nombre, correo, contraseña;
    Button registrarse;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        nombre = (EditText) findViewById(R.id.txtNombreR);
        correo = (EditText) findViewById(R.id.txtCorreoR);
        contraseña = (EditText) findViewById(R.id.txtContraseñaR);
        registrarse = (Button) findViewById(R.id.btnRegistrarseR);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        listener();
    }

    public void listener(){
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = correo.getText().toString().trim();
                String password = contraseña.getText().toString().trim();
                String username = nombre.getText().toString().trim();
                if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
                    Toast.makeText(Registrar.this, "Ingrese un nombre, un correo y una contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    verificarCorreoFirebaseAuth(email, password, username);
                }
            }
        });
    }

    private void verificarCorreoFirebaseAuth(String correo, String password, String username) {
        firebaseAuth.fetchSignInMethodsForEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods == null || signInMethods.isEmpty()) {
                                // El correo electrónico no está registrado en Firebase Authentication
                                registrarUsuarioFirebaseAuth(correo, password, username);
                            } else {
                                Toast.makeText(Registrar.this, "Correo ya registrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error al verificar el correo electrónico
                        }
                    }
                });
    }

    private void registrarUsuarioFirebaseAuth(String correo, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> emailTask) {
                                        if (emailTask.isSuccessful()) {
                                            // Correo de verificación enviado exitosamente
                                            guardarDatosFirestore(username, correo, password);
                                        } else {
                                            // Error al enviar el correo de verificación
                                            Toast.makeText(Registrar.this, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // Error al registrar el usuario en Firebase Authentication
                            Toast.makeText(Registrar.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosFirestore(String username, String correo, String password) {
        String id = UUID.randomUUID().toString();
        // Crea un objeto con los datos a guardar en Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("Nombre", username);
        userData.put("Correo", correo);
        userData.put("Contraseña", password);
        userData.put("Id Usuario", id);

        // Guarda los datos en Firestore
        firestore.collection("Usuarios").document(id)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Datos guardados exitosamente en Firestore
                        Toast.makeText(Registrar.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // Realiza las acciones adicionales que desees después de un registro exitoso
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar los datos en Firestore
                        Toast.makeText(Registrar.this, "Error al guardar los datos en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}