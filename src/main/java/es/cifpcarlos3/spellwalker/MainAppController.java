package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {

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



