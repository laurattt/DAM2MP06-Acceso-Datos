package com.project;

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class PR113sobreescriu {

    public static void main(String[] args) {
        // Definir la ruta del fichero dentro del directorio "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/frasesMatrix.txt";

        // Llamada al método que escribe las frases sobrescribiendo el fichero
        escriureFrases(camiFitxer);
    }

    // Método que escribe las frases sobrescribiendo el fichero con UTF-8 y línea en blanco final
    public static void escriureFrases(String camiFitxer) {
    
         // Frases Matrix 
        String frase1 = "I can only show you the door";
        String frase2 = "You're the one that has to walk through it";
        String lineaVacia = ""; // Línea en blanco
    
        // Crear un array con las frases y la línea en blanco
        String[] frases = {frase1, frase2, lineaVacia};
        Path path = Paths.get(camiFitxer); // Ruta del fichero
    
        try {
            // Escribe las frases en el fichero, creándolo si no existe y sobrescribiendo el contenido anterior
            Files.write(path, Arrays.asList(frases), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Eliminar la última línea en blanco
            if (Files.exists(path)) {
                java.util.List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
                if (!lineas.isEmpty() && lineas.get(lineas.size() - 1).trim().isEmpty()) {
                    lineas.remove(lineas.size() - 1);
                    Files.write(path, lineas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                }
            }

            System.out.println("Frases escrites correctament a l'arxiu.");
        } catch (IOException e) {
            System.out.println("Error en escriure al fitxer: " + e.getMessage());
        }
    }
}
