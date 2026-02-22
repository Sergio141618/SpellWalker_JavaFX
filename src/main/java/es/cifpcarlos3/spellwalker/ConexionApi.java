package es.cifpcarlos3.spellwalker;

import es.cifpcarlos3.spellwalker.RegistroController;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConexionApi {

    private static final String PIPELINE_URL = "https://spellwalker-joseju-dr.aws-eu-west-1.turso.io/v2/pipeline";

    public static String postToTurso(String jsonPayload) throws IOException {
        String token = "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NzAzMTQ5NDYsImlkIjoiMmM1MzMyNDctMjFhMy00MGVjLTk5ODAtNjMxMzgxNzg0ZTUzIiwicmlkIjoiZjNlMDNjNDktMDVjOC00OGQ3LWFkMTItZTEwNTc5MGI3NDlmIn0.TrrfxWUlEPfDlexKHTE_UfvUqrahiDwPMEyzcmsCMoFuYkOc1jUCnrSf5XNQku-K6kZKYcxTHXS23qHbvmR7Cw";
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException("TURSO_AUTH_TOKEN no está definido en las variables de entorno.");
        }
        token = token.trim();

        System.out.println("Token length: " + token.length());
        System.out.println("Token start: " + token.substring(0, Math.min(8, token.length())));
        System.out.println("Token end: " + token.substring(Math.max(0, token.length() - 8)));

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
        if (is == null)
            return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append('\n');
            return sb.toString().trim();
        }
    }


    public static boolean isJwtExpired(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2)
                return true;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int idx = payloadJson.indexOf("\"exp\"");
            if (idx == -1)
                return false;
            String after = payloadJson.substring(idx);
            String num = after.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
            long exp = Long.parseLong(num);
            long now = System.currentTimeMillis() / 1000L;
            return now >= exp;
        } catch (Exception e) {
            return true;
        }
    }

    public static void main(String[] args) {
        String payload = "{\"requests\":[{\"type\":\"execute\",\"stmt\":{\"sql\":\"SELECT 1\"}},{\"type\":\"close\"}]}";
        try {
            String token = "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NzAzMTQ5NDYsImlkIjoiMmM1MzMyNDctMjFhMy00MGVjLTk5ODAtNjMxMzgxNzg0ZTUzIiwicmlkIjoiZjNlMDNjNDktMDVjOC00OGQ3LWFkMTItZTEwNTc5MGI3NDlmIn0.TrrfxWUlEPfDlexKHTE_UfvUqrahiDwPMEyzcmsCMoFuYkOc1jUCnrSf5XNQku-K6kZKYcxTHXS23qHbvmR7Cw";
            if (token != null)
                System.out.println("JWT expired? " + isJwtExpired(token.trim()));
            String resp = postToTurso(payload);
            System.out.println("Respuesta: " + resp);

            System.out.println("=== PRUEBA DE REGISTRO ===");
            String usurname = "Prueba23";
            String password = "Tata";

            boolean resultado = registerPerfil(
                    usurname,
                    password,
                    "prueba23Tata@gmail.com");

            System.out.println("Resultado del registro: " + resultado);

            boolean ok = login(usurname, password);

            if (ok) {
                System.out.println("Login correcto");
            } else {
                System.out.println("Usuario o contraseña incorrectos");
            }


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

    public static String generarHash(String usuario, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");

            StringBuilder sb = new StringBuilder();
            sb.append(usuario);
            sb.append(password);

            md.update(sb.toString().getBytes());
            byte[] pass = md.digest();

            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(pass);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean usuarioExiste(String username) {
        try {
            String payload = """
                    {
                      "requests": [
                        {
                          "type": "execute",
                          "stmt": {
                            "sql": "SELECT NOMBRE_USUARIO FROM PERFIL WHERE NOMBRE_USUARIO = ?",
                            "args": ["%s"]
                          }
                        },
                        { "type": "close" }
                      ]
                    }
                    """.formatted(username);

            String resp = postToTurso(payload);

            return resp.contains("\"rows\":[[");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean contrasenaExiste(String password) {
        try {
            String payload = """
                    {
                      "requests": [
                        {
                          "type": "execute",
                          "stmt": {
                            "sql": "SELECT CONTRASENYA FROM PERFIL WHERE CONTRASENYA = ?",
                            "args": ["%s"]
                          }
                        },
                        { "type": "close" }
                      ]
                    }
                    """.formatted(password);

            String resp = postToTurso(payload);

            return resp.contains("\"rows\":[[");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean mailExiste(String mail) {
        try {
            String payload = """
                    {
                      "requests": [
                        {
                          "type": "execute",
                          "stmt": {
                            "sql": "SELECT MAIL FROM PERFIL WHERE MAIL = ?",
                            "args": ["%s"]
                          }
                        },
                        { "type": "close" }
                      ]
                    }
                    """.formatted(mail);

            String resp = postToTurso(payload);

            return resp.contains("\"rows\":[[");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerPerfil(String username, String password, String mail) {
        try {
            if (usuarioExiste(username)) {
                System.out.println("El usuario ya existe");
                return false;
            }

            if (mailExiste(mail)) {
                System.out.println("El correo ya está registrado");
                return false;
            }

            String hash = generarHash(username, password);

            String payload = """
                    {
                      "requests": [
                        {
                          "type": "execute",
                          "stmt": {
                            "sql": "INSERT INTO PERFIL (NOMBRE_USUARIO, CONTRASENYA, MAIL, NOTIFICACIONES) VALUES (?, ?, ?, 1)",
                            "args": [
                              { "type": "text", "value": "%s" },
                              { "type": "text", "value": "%s" },
                              { "type": "text", "value": "%s" }
                            ]
                          }
                        },
                        { "type": "close" }
                      ]
                    }
                    """.formatted(username, hash, mail);



            System.out.println("Payload enviado: " + payload);
            String resp = postToTurso(payload);
            System.out.println("Respuesta del servidor: " + resp);

            if (resp.contains("error")) {
                System.out.println("Error al registrar usuario: " + resp);
                return false;
            }

            System.out.println("Usuario registrado correctamente");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int obtenersiguienteIdCampana() throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT MAX(ID_CAMPAÑA) + 1 FROM CAMPAÑA"
              }
            },
            { "type": "close" }
          ]
        }
        """;

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }

    public static int extraerNumero(String json) {
        try {
            int start = json.indexOf("[[") + 2;
            int end = json.indexOf("]]");

            return Integer.parseInt(json.substring(start, end));

        } catch (Exception e) {
            return 1;
        }
    }

    public static int obtenersiguienteIdPersonaje() throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT MAX(PERSONAJE_ID) + 1 FROM PERSONAJE"
              }
            },
            { "type": "close" }
          ]
        }
        """;

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }
    public static int obtenerIdSpellPorNombre(String nombreSpell) throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_SPELL FROM SPELLS WHERE NOMBRE_SPELL = ?",
                "args": ["%s"]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreSpell);

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }

    public static int obtenerIdPersonajePorNombre(String nombrePersonaje) throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT PERSONAJE_ID FROM PERSONAJE WHERE NOMBRE_PERSONAJE = ?",
                "args": ["%s"]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombrePersonaje);

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }

    public static boolean insertarSpellAPersonaje(String nombrePersonaje, String nombreSpell) throws IOException {

        int idPersonaje = obtenerIdPersonajePorNombre(nombrePersonaje);
        if (idPersonaje == -1) {
            System.out.println("No existe el personaje: " + nombrePersonaje);
            return false;
        }

        int idSpell = obtenerIdSpellPorNombre(nombreSpell);
        if (idSpell == -1) {
            System.out.println("No existe el spell: " + nombreSpell);
            return false;
        }

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE_SPELLS (ID_SPELL, ID_PERS) VALUES (?, ?)",
                "args": [%d, %d]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idSpell, idPersonaje);

        postToTurso(payload);

        return true;
    }
    public static int obtenerIdCampanaPorNombre(String nombreCampana) throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_CAMPAÑA FROM CAMPAÑA WHERE NOMBRE = ?",
                "args": ["%s"]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreCampana);

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }


    public static boolean crearPersonajeConNombreYCampana(String nombrePersonaje, String nombreCampana, String perfil) {

        try {
            int idCampana = obtenerIdCampanaPorNombre(nombreCampana);

            if (idCampana == -1) {
                System.out.println("La campaña '" + nombreCampana + "' no existe. Debe crearse antes.");
                return false;
            }

            int nuevoIdPersonaje = obtenersiguienteIdPersonaje();

            String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE (PERSONAJE_ID, NOMBRE_PERSONAJE, ID_CAMPAÑA, PERSONAJE_PERFIL) VALUES (?, ?, ?, ?)",
                "args": [%d, "%s", %d, "%s"]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nuevoIdPersonaje, nombrePersonaje, idCampana, perfil);

            postToTurso(payload);

            System.out.println("Personaje creado correctamente con ID: " + nuevoIdPersonaje);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int crearCampana(String nombreCampana) throws IOException {

        int nuevoId = obtenersiguienteIdCampana();

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO CAMPAÑA (ID_CAMPAÑA, NOMBRE) VALUES (?, ?)",
                "args": [%d, "%s"]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nuevoId, nombreCampana);

        postToTurso(payload);

        return nuevoId;
    }

    public static boolean login(String username, String password) {
        try {
            String hash = generarHash(username, password);

            String payload = """
            {
              "requests": [
                {
                  "type": "execute",
                  "stmt": {
                    "sql": "SELECT NOMBRE_USUARIO FROM PERFIL WHERE NOMBRE_USUARIO = ? AND CONTRASENYA = ?",
                    "args": [
                      { "type": "text", "value": "%s" },
                      { "type": "text", "value": "%s" }
                    ]
                  }
                },
                { "type": "close" }
              ]
            }
            """.formatted(username, hash);


            String resp = postToTurso(payload);

            return resp.contains("\"rows\":[[");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int obtenerIdEscuelaPorNombre(String nombreEscuela) throws IOException {

        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_ESCUELA FROM ESCUELA WHERE NOMBRE_ESCUELA = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreEscuela);

        String resp = postToTurso(payload);

        return extraerNumero(resp);
    }

    public static List<String> obtenerTodasLasCampanas() throws IOException {
        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT NOMBRE FROM CAMPAÑA"
              }
            },
            { "type": "close" }
          ]
        }
        """;

        String resp = postToTurso(payload);
        return extraerNombresDeJson(resp);
    }

    private static List<String> extraerNombresDeJson(String json) {
        List<String> nombres = new ArrayList<>();
        try {
            int rowsIndex = json.indexOf("\"rows\":[");
            if (rowsIndex == -1)
                return nombres;

            String rowsContent = json.substring(rowsIndex + 8);

            String[] parts = rowsContent.split("\\],\\[");
            for (String part : parts) {
                int valueIndex = part.indexOf("\"value\":\"");
                if (valueIndex != -1) {
                    int startQuote = valueIndex + 9;
                    int nextQuote = part.indexOf("\"", startQuote);
                    if (nextQuote != -1) {
                        nombres.add(part.substring(startQuote, nextQuote));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombres;
    }

    public static boolean vincularPersonajeAEscuela(int idPersonaje, int idEscuela) throws IOException {
        String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE_ESCUELAS (ID_PERSONAJE, ID_ESCUELA) VALUES (?, ?)",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idEscuela);

        String resp = postToTurso(payload);
        return !resp.contains("error");
    }

}
