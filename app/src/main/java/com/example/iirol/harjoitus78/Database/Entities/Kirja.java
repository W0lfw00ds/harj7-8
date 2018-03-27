package com.example.iirol.harjoitus78.Database.Entities;

import android.os.Parcel;

import com.example.iirol.harjoitus78.Database.Entity;

public class Kirja extends Entity {

    // CONSTANTS
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_NIMI = "nimi";
    public static final String COLUMN_PAINOS = "painos";
    public static final String COLUMN_HANKINTAPVM = "hankintapvm";

    // FIELDS
    private int numero;
    private String nimi;
    private int painos;
    private String hankintapvm;

    // METHODS
    public int getNumero() {
        return this.numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public String getNimi() {
        return this.nimi;
    }
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    public int getPainos() {
        return this.painos;
    }
    public void setPainos(int painos) {
        this.painos = painos;
    }
    public String getHankintapvm() {
        return this.hankintapvm;
    }
    public void setHankintapvm(String hankintapvm) {
        this.hankintapvm = hankintapvm;
    }

    // CONSTRUCTORS
    public Kirja(String key, int numero, String nimi, int painos, String hankintapvm) {
        this.setKey(key);
        this.numero = numero;
        this.nimi = nimi;
        this.painos = painos;
        this.hankintapvm = hankintapvm;
    }
    public Kirja(int numero, String nimi, int painos, String hankintapvm) {
        this(null, numero, nimi, painos, hankintapvm);
    }
    public Kirja() {

    }

    // @Object
    @Override public String toString() {

        return this.numero + ". " + this.nimi;
    }

    // @Parceable
    @Override public int describeContents() {
        return 0;
    }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.numero);
        dest.writeString(this.nimi);
        dest.writeInt(this.painos);
        dest.writeString(this.hankintapvm);
        dest.writeString(this.getKey());
    }
    protected Kirja(Parcel in) {
        this.numero = in.readInt();
        this.nimi = in.readString();
        this.painos = in.readInt();
        this.hankintapvm = in.readString();
        this.setKey(in.readString());
    }
    public static final Creator<Kirja> CREATOR = new Creator<Kirja>() {
        @Override
        public Kirja createFromParcel(Parcel source) {
            return new Kirja(source);
        }
        @Override
        public Kirja[] newArray(int size) {
            return new Kirja[size];
        }
    };

}
