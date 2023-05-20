package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    EditText correo, contraseña;
    Button entrar, registrarse;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private boolean sesionIniciada = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("MiAplicacionPrefs", getApplicationContext().MODE_PRIVATE);
        sesionIniciada = prefs.getBoolean("sesionIniciada", false);
        if (sesionIniciada) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        correo = (EditText) findViewById(R.id.txtCorreo);
        contraseña = (EditText) findViewById(R.id.txtContraseña);
        entrar = (Button) findViewById(R.id.btnEntrar);
        registrarse = (Button) findViewById(R.id.btnRegistrarse);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        listener();
    }

    public void listener(){
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = correo.getText().toString().trim();
                String password = contraseña.getText().toString().trim();
                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(Login.this, "Ingrese un nombre, un correo y una contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    signInWithEmailAndPassword(email, password);
                }
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registrar.class);
                startActivity(intent);
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // El correo electrónico está verificado
                                // Realiza las acciones adicionales que desees después de la autenticación exitosa
                                verificarDatosUsuario(email, password);
                                // Aquí puedes redirigir al usuario al MainActivity o realizar otras acciones
                            } else {
                                // El correo electrónico no está verificado
                                mostrarDialogoCorreoNoVerificado();
                            }
                        } else {
                            // Error en la autenticación por correo electrónico y contraseña
                            Toast.makeText(Login.this, "Error al iniciar sesión. Verifique su correo y contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verificarDatosUsuario(String email, String password) {
        firestore.collection("Usuarios")
                .whereEqualTo("Correo", email)
                .whereEqualTo("Contraseña", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Los datos coinciden, el usuario existe en Firestore
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String userId = documentSnapshot.getString("Id Usuario");
                                String userName = documentSnapshot.getString("Nombre");

                                SharedPreferences prefs = getSharedPreferences("MiAplicacionPrefs", getApplicationContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("sesionIniciada", true);
                                editor.apply();

                                SharedPreferences preferences = getSharedPreferences("Datos_Usuario", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.putString("IdUser", userId);
                                editor2.putString("UserName", userName);
                                editor2.apply();


                                Toast.makeText(Login.this, "Datos de inicio de sesión válidos", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("IdUser", userId);
                                intent.putExtra("UserName", userName);
                                startActivity(intent);
                            } else {
                                // Los datos no coinciden, el usuario no existe en Firestore
                                Toast.makeText(Login.this, "Datos de inicio de sesión inválidos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error al consultar la colección "usuarios" en Firestore
                            Toast.makeText(Login.this, "Error al verificar los datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mostrarDialogoCorreoNoVerificado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correo no verificado");
        builder.setMessage("Su correo electrónico no ha sido verificado. ¿Desea enviar otro correo de verificación?")
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enviarCorreoVerificacion();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enviarCorreoVerificacion() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Se ha enviado un correo de verificación. Por favor, verifique su correo electrónico.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}