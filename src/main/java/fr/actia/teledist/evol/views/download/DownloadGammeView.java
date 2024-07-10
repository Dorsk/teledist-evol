package fr.actia.teledist.evol.views.download;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.actia.teledist.evol.models.ArtifactData;
import fr.actia.teledist.evol.models.GammeData;
import fr.actia.teledist.evol.models.UsineData;
import fr.actia.teledist.evol.tools.ArtifactoryClient;
import fr.actia.teledist.evol.tools.DatabaseTool;
import fr.actia.teledist.evol.treeviewer.ArtifactItem;
import fr.actia.teledist.evol.treeviewer.CheckboxesCustom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DownloadGammeView {

    private ComboBox gammeComboBox;
    private Map<Integer, String> gammes;
    private Map<Integer, GammeData> AllGammes;
    private Map<Integer, UsineData> UsinesPerGammme;
    private CheckBoxTreeItem<ArtifactItem> currentItem = null;
    private TreeView<ArtifactItem> treeView = new TreeView<>();
    private List<CheckBoxTreeItem> checkBoxesSavedArtifacts = new ArrayList<>();
    private TextField versionLabel;
    private TextField vehicleLabel;
    private Integer igGammeFound;
    private List<CheckboxesCustom> checkBoxesUsines;
    private VBox usinesBox;
    private List<ArtifactData> artifactDataList = new ArrayList<>();
    private File selectedFolder;

    @SuppressWarnings("unchecked")
    public SplitPane getView(Stage primaryStage) {
        // init
        treeView = new TreeView<>();
        checkBoxesSavedArtifacts = new ArrayList<>();
        checkBoxesUsines = new ArrayList<>();

        // Left  gamme details
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        Label labelFolder = new Label("Pas de dossier sélectionné");
        Button buttonFolder = new Button("Choisir un dossier d'export");
        buttonFolder.setOnAction(e -> {
            DirectoryChooser fileChooser = new DirectoryChooser();
            selectedFolder = fileChooser.showDialog(primaryStage);

            if (selectedFolder != null) {
                labelFolder.setText("Export : " + selectedFolder.getAbsolutePath());
            } else {
                labelFolder.setText("No file selected");
            }
        });

        Label gammeLabel = new Label("Nom de gamme");
        gammeComboBox = new ComboBox();
        gammeComboBox.setOnAction(e -> {
            try {
                
                for (Entry<Integer, String> entry : gammes.entrySet()) {
                    if (entry.getValue().equals(gammeComboBox.getValue())) {
                        igGammeFound = entry.getKey();
                        break;
                    }
                }
                // update du 1er tree viewer
                getArtifactsByIdGamme(igGammeFound, gammes.get(igGammeFound));
                // update labels
                vehicleLabel.setText(AllGammes.get(igGammeFound).getVehicule());
                versionLabel.setText(AllGammes.get(igGammeFound).getVersion());
                //update Usines
                getUsinesByIdGamme(igGammeFound, gammes.get(igGammeFound));
                // Non editable
                vehicleLabel.setEditable(false);
                versionLabel.setEditable(false);
                treeView.setEditable(false);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        Label versionLabelHead = new Label("Version");
        versionLabel = new TextField();
        Label vehicleLabelHead = new Label("Véhicule");
        vehicleLabel = new TextField();

         // "Usines" Section
        Label usinesLabel = new Label("Usines");
        usinesBox = new VBox(10);

        Button updateButton = new Button("Download Gamme");
        updateButton.setOnAction(e -> {
            ArtifactoryClient client = new ArtifactoryClient();
            boolean hasError = false;

            if(selectedFolder == null || selectedFolder.getAbsolutePath() == null || selectedFolder.getAbsolutePath().isEmpty())
            {
                hasError=true;
                showErrorPopup();
            }
            if(this.artifactDataList == null || this.artifactDataList.isEmpty())
            {
                hasError=true;
                showErrorPopup();
            }
            if(!hasError){
                for (ArtifactData artifact : this.artifactDataList) {
                    
                    try {
                        client.downloadArtifact(artifact.getUrl(), selectedFolder.getAbsolutePath()+ File.separator + artifact.getNom());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                showSuccessPopup(artifactDataList.size()); // IHM : export fini
            }
        });
        

        leftVBox.getChildren().addAll( labelFolder, buttonFolder, gammeLabel, gammeComboBox, versionLabelHead, versionLabel,
            vehicleLabelHead, vehicleLabel, usinesLabel, usinesBox, updateButton);

        // Center VBox 
        VBox centerVBox = new VBox(20);
        centerVBox.setPadding(new Insets(10));
        VBox.setVgrow(treeView, Priority.ALWAYS);
        centerVBox.getChildren().addAll(treeView);
        centerVBox.setAlignment(Pos.CENTER);

        // init gammes
        getAllGamme();
        handleArtifacts();
        
        // Main HBox to hold both VBoxes
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftVBox, centerVBox);
        splitPane.setDividerPositions(0.2f,0.4f,0.3f);

        return splitPane;
    }

    private void handleArtifacts() {
        gammeComboBox.setItems((ObservableList) FXCollections.observableArrayList(gammes.values()));
    }

    public void getAllGamme() {
        String insertGammeQuery = "SELECT id, nom, vehicule, version, repository from gamme ORDER BY nom ASC";
        DatabaseTool dbTool = new DatabaseTool();
        
        ResultSet res = dbTool.executeSelectQuery(insertGammeQuery);
        gammes = new HashMap<>();
        AllGammes = new HashMap<>();
        try {
            while (res.next()) {
                String urlRepo = res.getString("repository");
                GammeData gammeData = new GammeData(res.getInt("id"), res.getString("nom").toString(),
                res.getString("vehicule").toString(), res.getString("version").toString(), urlRepo);
                AllGammes.put(res.getInt("id"), gammeData);
                gammes.put(res.getInt("id"), res.getString("nom"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getUsinesByIdGamme(int idgamme, String nomGamme) {
        
       
        DatabaseTool dbTool = new DatabaseTool();
        Map<Integer, UsineData> allUsinesDataMap = new HashMap<>();

        allUsinesDataMap = dbTool.getAllUsines();
        Map<Integer, UsineData> selectedUsinesDataMap = dbTool.getAllusinesByidGamme(idgamme);

        // Creation du treeviewer
        usinesBox.getChildren().clear();
        for (int i = 1; i <= 10; i++) {
            CheckboxesCustom usineCheckBox = new CheckboxesCustom(allUsinesDataMap.get(i).getNom(), allUsinesDataMap.get(i).getId());
            usinesBox.getChildren().add(usineCheckBox);
            checkBoxesUsines.add(usineCheckBox);
            if(selectedUsinesDataMap.containsKey(i)){
                usineCheckBox.setSelected(true);
            }
       }
       
   }

    public void getArtifactsByIdGamme(int idgamme, String nomGamme) {
        String insertGammeQuery = "SELECT idartifact from joinGammeArtifacts where idgamme = " + idgamme;
        String selectArtifactsQuery = "SELECT id, nom, url, version, type, path from artifacts where id IN (";
        DatabaseTool dbTool = new DatabaseTool();
       
        ResultSet res = dbTool.executeSelectQuery(insertGammeQuery);
        String idArtefactsList="";
        int countSeparator = 0;
        try {
            while (res.next()) {
                if (countSeparator==0){
                    idArtefactsList = idArtefactsList + Integer.toString(res.getInt("idartifact"));
                    countSeparator=1;
                } else {
                    idArtefactsList = idArtefactsList + "," + Integer.toString(res.getInt("idartifact"));
                }
            }
        } catch (SQLException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }

        selectArtifactsQuery = selectArtifactsQuery + idArtefactsList + ")";
        res = dbTool.executeSelectQuery(selectArtifactsQuery);
        artifactDataList = new ArrayList<>();
        try {
            while (res.next()) {
                artifactDataList.add(new ArtifactData(res.getInt("id"), res.getString("nom"), res.getString("url"),
                    res.getString("version"), res.getString("type"), res.getString("path")));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Creation du treeviewer
        treeView.setRoot(buildTree(artifactDataList, nomGamme));
        treeView.setCellFactory(CheckBoxTreeCell.<ArtifactItem>forTreeView());
   }

   private CheckBoxTreeItem<ArtifactItem> buildTree(List<ArtifactData>  artifactDataList, String nomGamme) {
        CheckBoxTreeItem<ArtifactItem> rootItem = new CheckBoxTreeItem(new ArtifactItem("Repository " + nomGamme, "tree", "", ""));
       
        Map<String, CheckBoxTreeItem<ArtifactItem>> pathMap = new HashMap<>();

        for (ArtifactData artiItem : artifactDataList) {
            String path = artiItem.getPath();
            String type = artiItem.getType();
            String url = artiItem.getUrl();
            addPathToTree(rootItem, pathMap, path, type, url, true);
        }
        // init root item 
        rootItem.setExpanded(true);
        rootItem.setSelected(true);

        return rootItem;
    }

    private void addPathToTree(CheckBoxTreeItem<ArtifactItem> rootItem, Map<String, CheckBoxTreeItem<ArtifactItem>> pathMap, String path, String type, String url, boolean isArtifactFromDB) {
        String[] parts = path.split("/");
        currentItem = rootItem;

        for (String part : parts) {
            String fullPath = getFullPath(currentItem, part);
            currentItem = pathMap.computeIfAbsent(fullPath, k -> {
                CheckBoxTreeItem<ArtifactItem> newItem = createTreeItem(part, type, url, path);
                // Toujours expand les artifacts déja selectionné dans la database
                if (isArtifactFromDB){
                    newItem.setExpanded(true);
                    newItem.selectedProperty().addListener((obs, wasChecked, isNowChecked) -> {
                        if (isNowChecked) {
                            checkBoxesSavedArtifacts.add(newItem);
                        } else {
                            checkBoxesSavedArtifacts.remove(newItem);
                        }
                    });
                }
                currentItem.getChildren().add(newItem);
                return newItem;
            });
        }
    }

    private CheckBoxTreeItem<ArtifactItem> createTreeItem(String part, String type, String url, String path) {
        return new CheckBoxTreeItem(new ArtifactItem(part, type, url, path));
    }

    private String getFullPath(CheckBoxTreeItem<ArtifactItem> item, String part) {
        StringBuilder fullPath = new StringBuilder(part);
        TreeItem<ArtifactItem> current = item;

        while (current != null && current.getParent() != null) {
            fullPath.insert(0, current.getValue() + "/");
            current = current.getParent();
        }

        return fullPath.toString();
    }

    
    private void showSuccessPopup(int artifactsSize) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Confirmation");

        Label successLabel = new Label("Export de la gamme réussi ! \n"
        + "\n Gamme    : " + AllGammes.get(igGammeFound).getNom()
        + "\n Version    : " + AllGammes.get(igGammeFound).getVersion()
        + "\n Véhicule   : " + AllGammes.get(igGammeFound).getVehicule()
        + "\n Artifacts   : " + artifactsSize);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10, successLabel, okButton);
        popupLayout.setPadding(new Insets(10));
        popupLayout.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupLayout, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private void showErrorPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Confirmation");

        Label successLabel = new Label("Erreur d'export !");
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10, successLabel, okButton);
        popupLayout.setPadding(new Insets(10));
        popupLayout.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupLayout, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

}
