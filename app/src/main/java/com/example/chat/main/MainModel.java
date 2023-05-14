package com.example.chat.main;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class MainModel implements Parcelable{
    private String nombreContacto;

    public MainModel(String nombreContacto){
        this.nombreContacto = nombreContacto;
    }

    protected MainModel(Parcel in) {
        nombreContacto = in.readString();
    }

    public static final Parcelable.Creator<MainModel> CREATOR = new Parcelable.Creator<MainModel>() {
        @Override
        public MainModel createFromParcel(Parcel in) {
            return new MainModel(in);
        }

        @Override
        public MainModel[] newArray(int size) {
            return new MainModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombreContacto);
    }

    public String getNombre() {
        return nombreContacto;
    }
}
