package fr.actia.teledist.evol;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UpdateGammeView {

    public VBox getView() {
        // Left VBox for gamme details
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        Label gammeLabel = new Label("Nom de gamme");
        TextField gammeField = new TextField();
        gammeField.setPromptText("Entrez le nom de la gamme");

        Label versionLabel = new Label("Version");
        TextField versionField = new TextField();
        versionField.setPromptText("Entrez la version");

        Label vehicleLabel = new Label("Véhicule");
        TextField vehicleField = new TextField();
        vehicleField.setPromptText("Entrez le véhicule");

        Button updateButton = new Button("Update");
        // Add update logic here

        leftVBox.getChildren().addAll(gammeLabel, gammeField, versionLabel, versionField, vehicleLabel, vehicleField, updateButton);

        // Right VBox for additional options or information
        VBox rightVBox = new VBox(10);
        rightVBox.setPadding(new Insets(10));

        Label additionalOptionsLabel = new Label("Autres options");
        // Add additional fields or controls as needed
        TextField additionalField1 = new TextField();
        additionalField1.setPromptText("Option 1");

        TextField additionalField2 = new TextField();
        additionalField2.setPromptText("Option 2");

        rightVBox.getChildren().addAll(additionalOptionsLabel, additionalField1, additionalField2);

        // Main HBox to hold both VBoxes
        HBox mainHBox = new HBox(20, leftVBox, rightVBox);
        mainHBox.setPadding(new Insets(10));

        // Wrap in a VBox to return as the main view
        VBox mainVBox = new VBox(mainHBox);

        return mainVBox;
    }
}
