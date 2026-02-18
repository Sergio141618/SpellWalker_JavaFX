package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import es.cifpcarlos3.spellwalker.ConexionApi;
import java.net.URL;
import java.util.ResourceBundle;

public class CrearPersonajeController implements Initializable {

    @FXML
    private TextField campoUsuario;


    @FXML
    private PasswordField campoContrasena;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handlerCerrarAplicacion(ActionEvent actionEvent) {
        System.exit(0);
    }

}