package es.cifpcarlos3.spellwalker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistroController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private PasswordField campoConfirmar;

    @FXML
    private TextField campoEmail;

    @FXML
    private javafx.scene.control.CheckBox checkNotificaciones;

    @FXML
    public void handlerCrearCuenta(ActionEvent event) {
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();
        String confirmar = campoConfirmar.getText();
        String email = campoEmail.getText();
        boolean recibeNotificaciones = checkNotificaciones.isSelected();
        int boolNotificaciones;
        try {
            if (usuario.isBlank()||contrasena.isBlank()||confirmar.isBlank()||email.isBlank()) {
                Alert alertVacio = new Alert(Alert.AlertType.ERROR);
                alertVacio.setTitle("Error");
                alertVacio.setContentText("Uno o más campos están vacíos.");
                alertVacio.showAndWait();
            } else if (!confirmar.equals(contrasena)) {
                Alert alertContrasena = new Alert(Alert.AlertType.ERROR);
                alertContrasena.setTitle("Error");
                alertContrasena.setContentText("Las contraseñas no coinciden.");
                alertContrasena.showAndWait();
            } else if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                Alert alertEmail = new Alert(Alert.AlertType.ERROR);
                alertEmail.setTitle("Error");
                alertEmail.setContentText("El email no es válido.");
                alertEmail.showAndWait();
            }
            else {

                if (recibeNotificaciones) {
                    boolNotificaciones = 1;
                } else {
                    boolNotificaciones = 0;
                }

                System.out.println("Usuario: " + usuario);
                System.out.println("Contraseña: " + contrasena);
                System.out.println("Confirmar: " + confirmar);
                System.out.println("Email: " + email);
                System.out.println("Recibe notificaciones: " + boolNotificaciones);

                Alert alertSucc = new javafx.scene.control.Alert(
                        Alert.AlertType.INFORMATION);
                alertSucc.setTitle("Registro Completado");
                alertSucc.setHeaderText(null);
                alertSucc.setContentText("Usuario registrado correctamente");
                alertSucc.showAndWait();
                handlerLogin(event);
            }

        } catch (Exception e) {
            Alert alert = new javafx.scene.control.Alert(
                    Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ha ocurrido un error, código: " + e.getMessage());
            alert.showAndWait();
        }


    }

    @FXML
    public void handlerLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
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
