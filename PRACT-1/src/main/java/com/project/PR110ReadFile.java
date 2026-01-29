package com.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PR110ReadFile {

    public static void main(String[] args) {
        String camiFitxer = System.getProperty("user.dir") + "/data/GestioTasques.java";
        llegirIMostrarFitxer(camiFitxer);  // Sólo llamamos a la función con la ruta del archivo
    }

// Función que lee el archivo y muestra las líneas con numeración   
    public static void llegirIMostrarFitxer(String camiFitxer) {
        try (BufferedReader br = new BufferedReader(new FileReader(camiFitxer))) {
            String linea;
            int numLinea = 1;

            while ((linea = br.readLine()) != null) {
                System.out.println(numLinea + ": " + linea);
                numLinea++;
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}

// asi se ejecuta --> mvn test "-Dtest=com.project.PR110ReadFileTest"
