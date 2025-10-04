package com.project;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class PR124main {

    // tamaño de cada campo del registro
    private static final int ID_SIZE = 4; // num registro --> 4 bytes
    private static final int NAME_MAX_BYTES = 40; // max 20 caracts (40 bytes en UTF-8)
    private static final int GRADE_SIZE = 4; //4 bytes y float

    // Posicions dels camps dins el registre
    private static final int NAME_POS = ID_SIZE; // nombre despues del num-registro
    private static final int GRADE_POS = NAME_POS + NAME_MAX_BYTES; // nota despues del nombre

    // Atribut per al path del fitxer
    private String filePath;

    private Scanner scanner = new Scanner(System.in);

    // Constructor per inicialitzar el path del fitxer
    public PR124main() {
        this.filePath = System.getProperty("user.dir") + "/data/PR124estudiants.dat"; 
    }

    // Getter 
    public String getFilePath() {
        return filePath;
    }

    // Setter 
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        PR124main gestor = new PR124main();
        boolean sortir = false;

        while (!sortir) {
            try {
                gestor.mostrarMenu();
                int opcio = gestor.getOpcioMenu();

                switch (opcio) {
                    case 1 -> gestor.llistarEstudiants();
                    case 2 -> gestor.afegirEstudiant();
                    case 3 -> gestor.consultarNota();
                    case 4 -> gestor.actualitzarNota();
                    case 5 -> sortir = true;
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IOException e) {
                System.out.println("Error en la manipulació del fitxer: " + e.getMessage());
            }
        }
    }

 
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió d'Estudiants");
        System.out.println("1. Llistar estudiants");
        System.out.println("2. Afegir nou estudiant");
        System.out.println("3. Consultar nota d'un estudiant");
        System.out.println("4. Actualitzar nota d'un estudiant");
        System.out.println("5. Sortir");
        System.out.print("Selecciona una opció: ");
    }


    private int getOpcioMenu() {
        return Integer.parseInt(scanner.nextLine());
    }

    public void llistarEstudiants() throws IOException {
        llistarEstudiantsFitxer();
    }

    public void afegirEstudiant() throws IOException {
        int registre = demanarRegistre();
        String nom = demanarNom();
        float nota = demanarNota();
        afegirEstudiantFitxer(registre, nom, nota);
    }

    public void consultarNota() throws IOException {
        int registre = demanarRegistre();
        consultarNotaFitxer(registre);
    }

    public void actualitzarNota() throws IOException {
        int registre = demanarRegistre();
        float novaNota = demanarNota();
        actualitzarNotaFitxer(registre, novaNota);
    }

     // respuestas usuario
     private int demanarRegistre() {
        System.out.print("Introdueix el número de registre (enter positiu): ");
        int registre = Integer.parseInt(scanner.nextLine());
        if (registre < 0) {
            throw new IllegalArgumentException("El número de registre ha de ser positiu.");
        }
        return registre;
    }

    private String demanarNom() {
        System.out.print("Introdueix el nom (màxim 20 caràcters, depenent dels bytes UTF-8): ");
        return scanner.nextLine();
    }

    private float demanarNota() {
        System.out.print("Introdueix la nota (valor entre 0 i 10): ");
        float nota = Float.parseFloat(scanner.nextLine());
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("La nota ha de ser un valor entre 0 i 10.");
        }
        return nota;
    }

    // busca la posición del registro que tenga ese número
    private long trobarPosicioRegistre(RandomAccessFile raf, int registreBuscat) throws IOException {
        long numRegistres = raf.length() / (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE);
        for (int i = 0; i < numRegistres; i++) {
            raf.seek(i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE));
            int id = raf.readInt();
            if (id == registreBuscat) {
                return i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE);
            }
        }
        return -1;
    }

    // muestra todos los registros guardados en el archivo
    public void llistarEstudiantsFitxer() throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No hi ha estudiants registrats.");
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long numRegistres = raf.length() / (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE);
            boolean algun = false;
            for (int i = 0; i < numRegistres; i++) {
                raf.seek(i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE));
                int id = raf.readInt();
                String nom = llegirNom(raf);
                float nota = raf.readFloat();
                if (id > 0) {
                    System.out.println("Registre: " + id + ", Nom: " + nom + ", Nota: " + nota);
                    algun = true;
                }
            }
            if (!algun) {
                System.out.println("No hi ha estudiants registrats.");
            }
        }
    }

    // guarda un nuevo estudiante en el archivo
    public void afegirEstudiantFitxer(int registre, String nom, float nota) throws IOException {
        File file = new File(filePath);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long pos = trobarPosicioRegistre(raf, registre);
            if (pos != -1) {
                raf.seek(pos);
                int id = raf.readInt();
                if (id == registre) {
                    raf.seek(pos + ID_SIZE);
                    escriureNom(raf, nom);
                    raf.writeFloat(nota);
                    System.out.println("Estudiant afegit correctament.");
                    return;
                }
            }
            raf.seek(raf.length());
            raf.writeInt(registre);
            escriureNom(raf, nom);
            raf.writeFloat(nota);
            System.out.println("Estudiant afegit correctament.");
        }
    }

    // muestra la info de un estudiante concreto             
    public void consultarNotaFitxer(int registre) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long pos = trobarPosicioRegistre(raf, registre);
            if (pos == -1) {
                System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
                return;
            }
            raf.seek(pos);
            int id = raf.readInt();
            String nom = llegirNom(raf);
            float nota = raf.readFloat();
            System.out.println("Registre: " + id + ", Nom: " + nom + ", Nota: " + nota);
        }
    }

    // cambia la nota de un estudiante ya existente
    public void actualitzarNotaFitxer(int registre, float novaNota) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long pos = trobarPosicioRegistre(raf, registre);
            if (pos == -1) {
                System.out.println("No s'ha trobat l'estudiant amb registre: " + registre);
                return;
            }
            raf.seek(pos + ID_SIZE + NAME_MAX_BYTES); // posicion de la nota
            raf.writeFloat(novaNota);
            System.out.println("Nota actualitzada correctament.");
        }
    }

    // lee el nombre desde el archivo sin pasarse del tamaño
    private String llegirNom(RandomAccessFile raf) throws IOException {
    byte[] bytes = new byte[NAME_MAX_BYTES];
    raf.readFully(bytes);
    String nom = new String(bytes, StandardCharsets.UTF_8).trim();
    int idx = nom.indexOf(0);
    if (idx != -1) nom = nom.substring(0, idx);
    return nom.replaceAll("\\u0000+$", "");
    }

    // escribe el nombre rellenando con ceros si hace falta
    private void escriureNom(RandomAccessFile raf, String nom) throws IOException {
        byte[] nomBytes = nom.getBytes(StandardCharsets.UTF_8);
        if (nomBytes.length > NAME_MAX_BYTES) {
            byte[] truncat = new byte[NAME_MAX_BYTES];
            System.arraycopy(nomBytes, 0, truncat, 0, NAME_MAX_BYTES);
            raf.write(truncat);
        } else {
            raf.write(nomBytes);
            for (int i = nomBytes.length; i < NAME_MAX_BYTES; i++) {
                raf.writeByte(0);
            }
        }
    }
}
