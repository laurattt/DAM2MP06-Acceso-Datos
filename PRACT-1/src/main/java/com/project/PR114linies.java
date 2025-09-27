package com.project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class PR114linies {

    public static void main(String[] args) {
        // Definir la ruta del fichero dentro del directorio "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/numeros.txt";

        // Llamada al método que genera y escribe los números aleatorios
        generarNumerosAleatoris(camiFitxer);
    }

    // Método para generar 10 números aleatorios y escribirlos en el fichero
    public static void generarNumerosAleatoris(String camiFitxer) {

        //Array con los 10 numeros
        Random generadorRandom = new Random();
        ArrayList<String> lineas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lineas.add(String.valueOf(generadorRandom.nextInt(100)));
        }
        try {
            // Escribir todas las líneas menos la última con salto de línea y la última sin salto
            Path path = Paths.get(camiFitxer);
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (int i = 0; i < lineas.size(); i++) {
                    writer.write(lineas.get(i));
                    if (i < lineas.size() - 1) {
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
