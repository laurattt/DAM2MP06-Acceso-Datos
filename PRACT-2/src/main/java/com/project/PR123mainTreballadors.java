package com.project;

import com.project.excepcions.IOFitxerExcepcio;
import com.project.utilitats.UtilsCSV;

import java.util.List;
import java.util.Scanner;

public class PR123mainTreballadors {
    private String filePath = System.getProperty("user.dir") + "/data/PR123treballadors.csv";
    private Scanner scanner = new Scanner(System.in);

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void iniciar() {
        boolean sortir = false;

        while (!sortir) {
            try {
                mostrarMenu();
                int opcio = Integer.parseInt(scanner.nextLine());

                switch (opcio) {
                    case 1 -> mostrarTreballadors();
                    case 2 -> modificarTreballadorInteractiu();
                    case 3 -> {
                        System.out.println("Sortint...");
                        sortir = true;
                    }
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IOFitxerExcepcio e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // menu
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió de Treballadors");
        System.out.println("1. Mostra tots els treballadors");
        System.out.println("2. Modificar dades d'un treballador");
        System.out.println("3. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    // Mètode per mostrar els treballadors llegint el fitxer CSV
    public void mostrarTreballadors() throws IOFitxerExcepcio {
        List<String> treballadorsCSV = llegirFitxerCSV();
        for (String linia : treballadorsCSV) {
            System.out.println(linia);
        }
    }

    // Mètode per modificar un treballador (interactiu)
    public void modificarTreballadorInteractiu() throws IOFitxerExcepcio {
        System.out.print("\nIntrodueix l'ID del treballador que vols modificar: ");
        String id = scanner.nextLine();
        System.out.print("Quina dada vols modificar (Nom, Cognom, Departament, Salari)? ");
        String columna = scanner.nextLine();
        System.out.print("Introdueix el nou valor per a " + columna + ": ");
        String nouValor = scanner.nextLine();
        modificarTreballador(id, columna, nouValor);
    }

    public void modificarTreballador(String id, String columna, String nouValor) throws IOFitxerExcepcio {
        List<String> treballadorsCSV = llegirFitxerCSV();
        int numLinia = UtilsCSV.obtenirNumLinia(treballadorsCSV, "Id", id);
        if (numLinia == -1) {
            throw new IOFitxerExcepcio("No s'ha trobat el treballador amb Id: " + id);
        }
        String[] capcalera = UtilsCSV.obtenirArrayLinia(treballadorsCSV.get(0));
        int idxCol = -1;
        for (int i = 0; i < capcalera.length; i++) {
            if (capcalera[i].equalsIgnoreCase(columna)) {
                idxCol = i;
                break;
            }
        }
        if (idxCol == -1) {
            throw new IOFitxerExcepcio("Columna no vàlida: " + columna);
        }
        // Modificar el valor
        String[] dades = UtilsCSV.obtenirArrayLinia(treballadorsCSV.get(numLinia));
        dades[idxCol] = nouValor;
        StringBuilder novaLinia = new StringBuilder();
        for (int i = 0; i < dades.length; i++) {
            novaLinia.append(dades[i]);
            if (i < dades.length - 1) novaLinia.append(",");
        }
        treballadorsCSV.set(numLinia, novaLinia.toString());
        escriureFitxerCSV(treballadorsCSV);
    }

    private List<String> llegirFitxerCSV() throws IOFitxerExcepcio {
        List<String> treballadorsCSV = UtilsCSV.llegir(filePath);
        if (treballadorsCSV == null) {
            throw new IOFitxerExcepcio("Error en llegir el fitxer.");
        }
        return treballadorsCSV;
    }

    private void escriureFitxerCSV(List<String> treballadorsCSV) throws IOFitxerExcepcio {
        try {
            UtilsCSV.escriure(filePath, treballadorsCSV);
        } catch (Exception e) {
            throw new IOFitxerExcepcio("Error en escriure el fitxer.", e);
        }
    }

    public static void main(String[] args) {
        PR123mainTreballadors programa = new PR123mainTreballadors();
        programa.iniciar();
    }    
}
