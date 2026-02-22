package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CrearPersonajeController implements Initializable {

    @FXML
    private TextField campoNombre;

    @FXML
    private ComboBox<String> comboCampana;

    @FXML
    private ComboBox<String> comboEscuela;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<String> escuelas = ConexionApi.obtenerTodasLasEscuelas();
            comboEscuela.getItems().addAll(escuelas);

            List<String> campanas = ConexionApi.obtenerTodasLasCampanas();
            comboCampana.getItems().addAll(campanas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerCrear(ActionEvent event) {
        String nombrePersonaje = campoNombre.getText();
        String nombreCampana = comboCampana.getValue();
        String nombreEscuela = comboEscuela.getValue();

        if (nombrePersonaje == null || nombrePersonaje.isBlank() || nombreCampana == null || nombreEscuela == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Por favor, rellena todos los campos.");
            alert.showAndWait();
            return;
        }

        String perfilUsuario = "Admin1234";

        boolean creado = ConexionApi.crearPersonajeConNombreYCampana(nombrePersonaje, nombreCampana, perfilUsuario);

        if (creado) {
            try {
                int idPersonaje = ConexionApi.obtenerIdPersonajePorNombre(nombrePersonaje);
                int idEscuela = ConexionApi.obtenerIdEscuelaPorNombre(nombreEscuela);

                if (idPersonaje != -1 && idEscuela != -1) {
                    boolean vinculado = ConexionApi.vincularPersonajeAEscuela(idPersonaje, idEscuela);
                    if (vinculado) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setContentText("Personaje creado y vinculado a la escuela correctamente.");
                        alert.showAndWait();

                        campoNombre.clear();
                        comboCampana.getSelectionModel().clearSelection();
                        comboEscuela.getSelectionModel().clearSelection();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo crear el personaje.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handlerCerrarAplicacion(ActionEvent actionEvent) {
        System.exit(0);
    }

}