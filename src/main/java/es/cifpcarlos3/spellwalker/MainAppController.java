package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    public void handlerAcceder(ActionEvent event) {
        String login = campoUsuario.getText().trim();
        String password = campoContrasena.getText().trim();

        if (login.equals("admin") && password.equals("1234")) {
            crearPersonaje();
        } else {
            campoUsuario.setStyle("-fx-border-color: red;");
            campoContrasena.setStyle("-fx-border-color: red;");
        }
    }
    private void crearPersonaje() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("crear_personaje-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
            stage1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlerCrearUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("registro-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Stage stage1 = (Stage) campoUsuario.getScene().getWindow();
            stage1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



