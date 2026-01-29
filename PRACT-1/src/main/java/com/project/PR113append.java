package com.project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class PR113append {

    // Método que añade frases al fichero en UTF-8 con línea en blanco al final
    public static void afegirFrases(String camiFixer) {
        // Frases a añadir
        String frase1 = "I can only show you the door";
        String frase2 = "You're the one that has to walk through it";
        String lineaBlanca = ""; // Línea en blanco

        // Creamos un array con las frases y la línea en blanco
        String[] frases = {frase1, frase2, lineaBlanca};

        // Ruta del fichero
        Path path = Paths.get(camiFixer);

        try {
            // Escribimos las frases en el fichero, creándolo si no existe
            Files.write(path, Arrays.asList(frases), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Leemos todas las líneas del fichero
            if (Files.exists(path)) {
                List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

                // Si la última línea está vacía, la quitamos (para no tener línea en blanco al final)
                if (!lineas.isEmpty() && lineas.get(lineas.size() - 1).trim().isEmpty()) {
                    lineas.remove(lineas.size() - 1);
                    // Sobrescribimos el fichero sin la última línea en blanco
                    Files.write(path, lineas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                }
            }

            System.out.println("Frases afegides correctament a l'arxiu.");
        } catch (IOException e) {
            System.out.println("Error en escriure al fitxer: " + e.getMessage());
        }
    }
}


// recuerda: este ejercicio estaba conectado con "pr113sobreescriu", por eso el testing te salía mal, OJO --> tener en cuenta los nombres de los test para ver si existe alguna relación >:(