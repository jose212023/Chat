package com.example.chat.chat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ChatModel implements Parcelable{
    private String nombre;
    private String mensaje;
    private String id;

    public ChatModel() {
    }

    public ChatModel(String nombre, String mensaje, String id){
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.id = id;
    }

    protected ChatModel(Parcel in) {
        nombre = in.readString();
        mensaje = in.readString();
        id = in.readString();
    }

    public static final Parcelable.Creator<ChatModel> CREATOR = new Parcelable.Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(mensaje);
        parcel.writeString(id);
    }

    public String getNombre() {
        return nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getId() {
        return id;
    }
}
