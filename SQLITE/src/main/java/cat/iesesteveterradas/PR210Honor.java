package cat.iesesteveterradas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class PR210Honor {

    // ejecuta -> mvn exec:java -Dexec.mainClass="cat.iesesteveterradas.PR210Honor"

    
    private static final String DB_FILE = Paths.get(System.getProperty("user.dir"), "data", "honorbbdd.db").toString();
    private final DataBase db;

    public PR210Honor() {
        db = new DataBase(DB_FILE);
        db.initializeDatabase();
    }

    public static void main(String[] args) {
        PR210Honor app = new PR210Honor();
        app.menuRun();
    }

    public void menuRun() {
        try (Scanner sc = new Scanner(System.in)) {
            boolean exit = false;
            while (!exit) {
                printMenu();
                System.out.print("Escoge una opcion: ");
                String line = sc.nextLine().trim();
                switch (line) {
                    case "1":
                        showTable(sc);
                        break;
                    case "2":
                        showPersonajesByFaccionn(sc);
                        break;
                    case "3":
                        showBestAtacantByFaccionn(sc);
                        break;
                    case "4":
                        showBestDefensorByFaccion(sc);
                        break;
                    case "5":
                        exit = true;
                        System.out.println("Byeee");
                        break;
                    default:
                        System.out.println("Opcion invalida");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Menú =====");
        System.out.println("1) Mostrar una tabla (Faccion / Personaje)");
        System.out.println("2) Mostrar Personajes por faccion");
        System.out.println("3) Mostrar el mejor atacante por faccion");
        System.out.println("4) Mostrar el mejor defensor por faccion");
        System.out.println("5) Salir");
    }

    private void showTable(Scanner sc) {
        System.out.print("Opciones tabla para ver [Faccion / Personaje]: ");
        String t = sc.nextLine().trim().toLowerCase();
        if (t.equals("Faccion") || t.equals("faccion")) {
            List<Faccion> facs = db.getAllFaccionns();
            System.out.println("\n===== Facciones =====");
            facs.forEach(System.out::println);
        } else if (t.equals("Personaje") || t.equals("Personajes")) {
            List<Personaje> ps = db.getAllPersonajes();
            System.out.println("\n===== Personajes =====");
            ps.forEach(System.out::println);
        } else {
            System.out.println("Taula desconeguda.");
        }
    }

    private int chooseFaccion(Scanner sc) {
        List<Faccion> facs = db.getAllFaccionns();
        System.out.println("\nFaccion por id:");
        facs.forEach(f -> System.out.println(f.getId() + ") " + f.getNom()));
        System.out.print("id = ");
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Id invalido.");
            return -1;
        }
    }

    private void showPersonajesByFaccionn(Scanner sc) {
        int id = chooseFaccion(sc);
        if (id < 0)
            return;

        List<Personaje> ps = db.getPersonajesByFaccion(id);
        String nomF = db.getAllFaccionns().stream()
                .filter(f -> f.getId() == id)
                .map(Faccion::getNom)
                .findFirst()
                .orElse("(desconegut)");

        System.out.println("\nPersonajes de la faccion: " + nomF);
        if (ps.isEmpty())
            System.out.println("(Personaje null)");
        else
            ps.forEach(System.out::println);
    }

    private void showBestAtacantByFaccionn(Scanner sc) {
        int id = chooseFaccion(sc);
        if (id < 0)
            return;

        Personaje p = db.getBestAtacantByFaccion(id);
        String nomF = db.getAllFaccionns().stream()
                .filter(f -> f.getId() == id)
                .map(Faccion::getNom)
                .findFirst()
                .orElse("(desconegut)");

        System.out.println("\nMejor atacante de la faccion: " + nomF);
        if (p == null)
            System.out.println("(Personaje null)");
        else
            System.out.println(p);
    }

    private void showBestDefensorByFaccion(Scanner sc) {
        int id = chooseFaccion(sc);
        if (id < 0)
            return;

        Personaje p = db.getBestDefensorByFaccion(id);
        String nomF = db.getAllFaccionns().stream()
                .filter(f -> f.getId() == id)
                .map(Faccion::getNom)
                .findFirst()
                .orElse("(desconegut)");

        System.out.println("\nMejor defensor de la faccion: " + nomF);
        if (p == null)
            System.out.println("(Personaje null)");
        else
            System.out.println(p);
    }

    public static Path obtenirPathFitxer() {
        return Paths.get(System.getProperty("user.dir"), "data", "bones_practiques_programacio.txt");
    }

    public static List<String> readFileContent(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }

}