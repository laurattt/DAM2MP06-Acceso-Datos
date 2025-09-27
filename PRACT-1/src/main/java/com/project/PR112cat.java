package com.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PR112cat {

    public static void main(String[] args) {
    // Comprobar que se ha proporcionado una ruta como parámetro        
    if (args.length == 0) {
            System.out.println("No s'ha proporcionat cap ruta d'arxiu.");
            return;
        }

        // Obtener la ruta del archivo desde los parámetros
        String rutaArxiu = args[0];
        mostrarContingutArxiu(rutaArxiu);
    }

        // Función para mostrar el contenido del archivo o el mensaje de error correspondiente
        public static void mostrarContingutArxiu(String rutaArxiu) {
        File fitxer = new File(rutaArxiu);
        if (!fitxer.exists() || !fitxer.isFile()) {
            if (fitxer.isDirectory()) {
                System.out.println("El path no correspon a un arxiu, sinó a una carpeta.");
            } else {
                System.out.println("El fitxer no existeix o no és accessible.");
            }
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fitxer), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (java.io.IOException e) {
            System.out.println("El fitxer no existeix o no és accessible."); 
        }
    }
}


// recuerda que en los print debe de ser el mismo mensaje que sale en el Test, sino puede saltar error >:(

