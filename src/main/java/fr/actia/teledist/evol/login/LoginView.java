package fr.actia.teledist.evol.login;

import fr.actia.teledist.evol.views.gammes.CreateGammeView;
import fr.actia.teledist.evol.views.gammes.UpdateGammeView;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    public GridPane getView(BorderPane mainLayout) {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 0);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 0);

        Label pwLabel = new Label("Password:");
        grid.add(pwLabel, 0, 1);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 1);

        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 2);

        loginButton.setOnAction(event -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            //if (authenticate(username, password)) {
            //     showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
                CreateGammeView updateGammeView = new CreateGammeView();
                mainLayout.setCenter(updateGammeView.getView());
            //} else {
            //     showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials.");
                // On passe a la suite quand meme tnat que le lDAP n'est pas actif
            //     CreateGammeView updateGammeView = new CreateGammeView();
            //    mainLayout.setCenter(updateGammeView.getView());
            // }
        });
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private boolean authenticate(String username, String password) {
        return LDAPconnect.authenticate(username, password);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
