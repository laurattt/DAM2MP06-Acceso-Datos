package cat.iesesteveterradas;

public class Faccion {
    private int id;
    private String nom;
    private String resum;

    // cons
    public Faccion(int id, String nom, String resum) {
        this.id = id;
        this.nom = nom;
        this.resum = resum;
    }

    //getters 
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getResum() {
        return resum;
    }

    @Override
    public String toString() {
        return String.format("%d - %s : %s", id, nom, resum);
    }
}