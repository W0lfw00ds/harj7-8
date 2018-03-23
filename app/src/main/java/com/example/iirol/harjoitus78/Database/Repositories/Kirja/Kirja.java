package com.example.iirol.harjoitus78.Database.Repositories.Kirja;

public class Kirja {

    private Integer id;
    private int numero;
    private String nimi;
    private int painos;
    private String hankintapvm;

    public Kirja(Integer id, int numero, String nimi, int painos, String hankintapvm) {
        this.id = id;
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

    public Integer getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
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

    // @Object
    @Override public String toString() {
        return this.numero + ". " + this.nimi;
    }
}
