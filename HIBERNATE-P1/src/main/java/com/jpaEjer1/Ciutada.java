package com.jpaEjer1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "CiutadaJPA")
@Table(name = "CIUTADA_JPA") 
public class Ciutada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciutadaId")
    private long ciutadaId;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "cognom")
    private String cognom;
    
    @Column(name = "edat")
    private int edat;

    public Ciutada() {}

    public Ciutada(String nom, String cognom, int edat) {
        this.nom = nom;
        this.cognom = cognom;
        this.edat = edat;
    }

    public long getCiutadaId() { 
        return ciutadaId; 
    }

    public void setCiutadaId(long ciutadaId) { 
        this.ciutadaId = ciutadaId; 
    }

    public String getNom() { 
        return nom; 
    }

    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getCognom() { 
        return cognom; 
    }

    public void setCognom(String cognom) { 
        this.cognom = cognom; 
    }

    public int getEdat() { 
        return edat; 
    }

    public void setEdat(int edat) { 
        this.edat = edat; 
    }

    @Override
    public String toString() {
        return "CiutadaJPA ID = " + ciutadaId + ", nom = " + nom + ", cognom = " + cognom + ", edat = " + edat ;
    }
}