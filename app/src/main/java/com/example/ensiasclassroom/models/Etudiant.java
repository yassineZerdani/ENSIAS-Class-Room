package com.example.ensiasclassroom.models;

import java.io.Serializable;

public class Etudiant implements Serializable {

    public String nom;
    public String prenom;
    public String tel;
    public String photo;
    public String token;
    public String id;

    public Etudiant(String nom, String prenom, String tel){
        nom= new String(nom);
        prenom= new String(prenom);
        tel= new String(tel);
    }

    public Etudiant() {

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
