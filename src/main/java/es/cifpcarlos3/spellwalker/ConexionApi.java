package es.cifpcarlos3.spellwalker;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConexionApi {

    private static final String PIPELINE_URL = "https://spellwalker-joseju-dr.aws-eu-west-1.turso.io/v2/pipeline";

    public static String postToTurso(String jsonPayload) throws IOException {
        String token = "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NzAzMTQ5NDYsImlkIjoiMmM1MzMyNDctMjFhMy00MGVjLTk5ODAtNjMxMzgxNzg0ZTUzIiwicmlkIjoiZjNlMDNjNDktMDVjOC00OGQ3LWFkMTItZTEwNTc5MGI3NDlmIn0.TrrfxWUlEPfDlexKHTE_UfvUqrahiDwPMEyzcmsCMoFuYkOc1jUCnrSf5XNQku-K6kZKYcxTHXS23qHbvmR7Cw";
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException("TURSO_AUTH_TOKEN no está definido en las variables de entorno.");
        }
        token = token.trim();

        // Depuración: imprime longitud y primeros/últimos caracteres (sin mostrar el token completo)
        System.out.println("Token length: " + token.length());
        System.out.println("Token start: " + token.substring(0, Math.min(8, token.length())));
        System.out.println("Token end: " + token.substring(Math.max(0, token.length()-8)));

        byte[] body = jsonPayload.getBytes(StandardCharsets.UTF_8);

        URL url = new URL(PIPELINE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("POST");
            con.setConnectTimeout(15000);
            con.setReadTimeout(30000);
            con.setDoOutput(true);

            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(body.length));

            // Escribir body
            try (OutputStream os = con.getOutputStream()) {
                os.write(body);
                os.flush();
            }

            int code = con.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
            String response = readStream(is);
            System.out.println("HTTP code: " + code);
            return response;
        } finally {
            con.disconnect();
        }
    }

    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString().trim();
        }
    }

    // Utilidad opcional: comprobar formato JWT y expiración (solo para depuración local)
    public static boolean isJwtExpired(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return true;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            // Busca "exp": número (simple parse)
            int idx = payloadJson.indexOf("\"exp\"");
            if (idx == -1) return false;
            String after = payloadJson.substring(idx);
            String num = after.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
            long exp = Long.parseLong(num);
            long now = System.currentTimeMillis() / 1000L;
            return now >= exp;
        } catch (Exception e) {
            return true;
        }
    }

    // Ejemplo de uso
    public static void main(String[] args) {
        String payload = "{\"requests\":[{\"type\":\"execute\",\"stmt\":{\"sql\":\"SELECT 1\"}},{\"type\":\"close\"}]}";
        try {
            String token = "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NzAzMTQ5NDYsImlkIjoiMmM1MzMyNDctMjFhMy00MGVjLTk5ODAtNjMxMzgxNzg0ZTUzIiwicmlkIjoiZjNlMDNjNDktMDVjOC00OGQ3LWFkMTItZTEwNTc5MGI3NDlmIn0.TrrfxWUlEPfDlexKHTE_UfvUqrahiDwPMEyzcmsCMoFuYkOc1jUCnrSf5XNQku-K6kZKYcxTHXS23qHbvmR7Cw";
            if (token != null) System.out.println("JWT expired? " + isJwtExpired(token.trim()));
            String resp = postToTurso(payload);
            System.out.println("Respuesta: " + resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getPerfil(String token, URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
