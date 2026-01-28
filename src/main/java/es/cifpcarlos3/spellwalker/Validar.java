package es.cifpcarlos3.spellwalker;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Validar {

    static String usuariosUrl = "http://localhost:8080/api/usuarios";

    public static boolean comprobacion(String nombre, String contrasena) throws Exception {

        URL url = new URL(usuariosUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String linea;
        boolean encontrado = false;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split("},");

            int numero = 0;
            while (numero < partes.length) {

                if (partes[numero].contains(nombre ) && partes[numero].contains(contrasena)) {

                    encontrado = true;
                    break;
                }

                numero++;
            }
        }

        br.close();
        con.disconnect();

        return encontrado;
    }
}