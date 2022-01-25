package com.udemy.ubercl.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.udemy.ubercl.config.ConfiguracaoFirebase;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String tipo;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(getIdUsuario());

        usuarios.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
