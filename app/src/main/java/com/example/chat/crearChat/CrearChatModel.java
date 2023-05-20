package com.example.chat.crearChat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.chat.main.MainModel;

public class CrearChatModel implements Parcelable {
    private String nombreContacto;
    private String idUsuario;

    public CrearChatModel(String nombreContacto, String idUsuario){
        this.nombreContacto = nombreContacto;
        this.idUsuario = idUsuario;
    }

    protected CrearChatModel(Parcel in) {
        nombreContacto = in.readString();
        idUsuario = in.readString();
    }

    public static final Parcelable.Creator<CrearChatModel> CREATOR = new Parcelable.Creator<CrearChatModel>() {
        @Override
        public CrearChatModel createFromParcel(Parcel in) {
            return new CrearChatModel(in);
        }

        @Override
        public CrearChatModel[] newArray(int size) {
            return new CrearChatModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombreContacto);
        parcel.writeString(idUsuario);
    }

    public String getNombre() {
        return nombreContacto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }
}
