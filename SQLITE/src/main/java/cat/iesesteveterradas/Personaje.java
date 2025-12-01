package cat.iesesteveterradas;

public class Personaje{
    private int id;
    private String nombre;
    private double ataque;
    private double defensa;
    private int idFaccion;

    // cons
    public Personaje(int id, String nombre, double ataque, double defensa, int idFaccion) {
        this.id = id;
        this.nombre = nombre;
        this.ataque = ataque;
        this.defensa = defensa;
        this.idFaccion = idFaccion;
    }


    //getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getAtaque() {
        return ataque;
    }

    public double getDefensa() {
        return defensa;
    }

    public int getIdFaccion() {
        return idFaccion;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (Ataque: %.1f, Defensa: %.1f) [FACCIÓN=%d]", id, nombre, ataque, defensa, idFaccion);
    }
}