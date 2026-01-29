package com.jpaEjer1;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "CiutatJPA")
@Table(name = "CIUTAT_JPA") 
public class Ciutat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciutatId")
    private long ciutatId;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "pais")
    private String pais;
    
    @Column(name = "poblacio")
    private int poblacio;
    
    // una ciudad tiene muchos ciudadanos / one to many
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ciutat_id_logica") 
    private Set<Ciutada> ciutadans = new HashSet<>();

    public Ciutat() {}

    public Ciutat(String nom, String pais, int poblacio) {
        this.nom = nom;
        this.pais = pais;
        this.poblacio = poblacio;
    }

    public long getCiutatId() { 
        return ciutatId; 
    }

    public void setCiutatId(long ciutatId) { 
        this.ciutatId = ciutatId; 
    }

    public String getNom() { 
        return nom; 
    }

    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getPais() { 
        return pais;   
    }
     
    public void setPais(String pais) { 
        this.pais = pais; 

    }

    public int getPoblacio() { 
        return poblacio; 
    }

    public void setPoblacio(int poblacio) { 
        this.poblacio = poblacio; 
    }

    public Set<Ciutada> getCiutadans() { 
        return ciutadans; 
    }

    public void setCiutadans(Set<Ciutada> ciutadans) { 
        this.ciutadans = ciutadans; 
    }

    @Override
    public String toString() {
        return "CiutatJPA ID=" + ciutatId + ", nom = " + nom + ", pais = " + pais + ", CP = " + poblacio;
    }
}