package com.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PR115cp {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: Has d'indicar dues rutes d'arxiu.");
            System.out.println("Ús: PR115cp <origen> <destinació>");
            return;
        }

        // Ruta del archivo origen
        String rutaOrigen = args[0];
        // Ruta del archivo destino
        String rutaDestino = args[1];

        // Llamada al método para copiar el archivo
        copiarArxiu(rutaOrigen, rutaDestino);
    }

    // Método para copiar un archivo de texto del origen al destino
    public static void copiarArxiu(String rutaOrigen, String rutaDestino) {
        Path origen = Paths.get(rutaOrigen);
        Path destino = Paths.get(rutaDestino);
        try {
            if (!Files.exists(origen) || !Files.isRegularFile(origen)) {
                System.out.println("Error: L'arxiu origen no existeix o no és un fitxer de text.");
                return;
            }
            if (Files.exists(destino)) {
                System.out.println("Advertència: L'arxiu de destinació ja existeix i serà sobreescrit.");
            } else {
                Files.createDirectories(destino.getParent());
            }
            // Leer todo el contenido como bytes para mantener saltos de línea finales
            byte[] contenido = Files.readAllBytes(origen);
            Files.write(destino, contenido);
            System.out.println("Còpia realitzada correctament.");
        } catch (Exception e) {
            System.out.println("Error en copiar l'arxiu: " + e.getMessage());
        }
    }
}

// RECUERDA: asi como saltn errores con prints diferentes, tambien pasa con los métodos zzz