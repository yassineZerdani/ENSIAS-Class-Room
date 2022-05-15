package com.example.ensiasclassroom.models;

import java.util.LinkedList;

public class Group {
    public String titre, id;
    public String description;
    LinkedList<Etudiant> liste_etudiants;

    public Group(String titre, String description, LinkedList<Etudiant> liste_etudiants ){
        this.titre= new String(titre);
        this.description= new String(description);
        this.liste_etudiants=liste_etudiants;
    }

    public Group() {

    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LinkedList<Etudiant> getListe_etudiants() {
        return liste_etudiants;
    }

    public void setListe_etudiants(LinkedList<Etudiant> liste_etudiants) {
        this.liste_etudiants = liste_etudiants;
    }
}
