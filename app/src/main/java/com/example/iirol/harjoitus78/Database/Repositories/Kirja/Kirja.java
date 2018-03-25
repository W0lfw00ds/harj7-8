package com.example.iirol.harjoitus78.Database.Repositories.Kirja;

import com.example.iirol.harjoitus78.Database.Repositories.Entity;

public class Kirja extends Entity {

    // VARIABLES
    private String key;
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
        this.key = key;
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

    // @Entity
    @Override public String getKey() {
        return this.key;
    }
    @Override public void setKey(String key) {
        this.key = key;
    }

    // @Object
    @Override public String toString() {
        return this.numero + ". " + this.nimi;
    }

}
