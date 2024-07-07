package fr.actia.teledist.evol;

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
import fr.actia.teledist.evol.tools.DatabaseTool;
import fr.actia.teledist.evol.treeviewer.ArtifactItem;
import fr.actia.teledist.evol.treeviewer.CheckboxesCustom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UpdateGammeView {

    private ComboBox gammeComboBox;
    private Map<Integer, String> gammes;
    private Map<Integer, GammeData> AllGammes;
    CheckBoxTreeItem<ArtifactItem> currentItem = null;
    TreeView<ArtifactItem> treeView = new TreeView<>();
    TreeView<ArtifactItem> treeViewRepository = new TreeView<>();
    private List<CheckBoxTreeItem> checkBoxes = new ArrayList<>();
    private TextField versionLabel;
    private TextField vehicleLabel;

    @SuppressWarnings("unchecked")
    public VBox getView() {
        // init
        treeView = new TreeView<>();
        checkBoxes = new ArrayList<>();

        // Left VBox for gamme details
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        Label gammeLabel = new Label("Nom de gamme");
        gammeComboBox = new ComboBox();
        gammeComboBox.setOnAction(e -> {
            try {
                int igGammeFound=0;
                for (Entry<Integer, String> entry : gammes.entrySet()) {
                    if (entry.getValue().equals(gammeComboBox.getValue())) {
                        igGammeFound = entry.getKey();
                        break;
                    }
                }
                // update du 1er tree viewer
                getArtifactsByIdGamme(igGammeFound, gammes.get(igGammeFound));
                // update du 2eme tree viewer
                getArtifactsFromGitHub(AllGammes.get(igGammeFound).getUrl());
                // update labels
                vehicleLabel.setText(AllGammes.get(igGammeFound).getVehicule());
                versionLabel.setText(AllGammes.get(igGammeFound).getVersion());

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        Label versionLabelHead = new Label("Version");
        versionLabel = new TextField();
        Label vehicleLabelHead = new Label("Version");
        vehicleLabel = new TextField();

        Button updateButton = new Button("Update");

        leftVBox.getChildren().addAll(gammeLabel, gammeComboBox, versionLabelHead, versionLabel, vehicleLabelHead, vehicleLabel, updateButton);

        // Center VBox 
        VBox centerVBox = new VBox(20);
        centerVBox.setPadding(new Insets(10));
        VBox.setVgrow(treeView, Priority.ALWAYS);
        Button supprButton = new Button("Update artifacts de la gamme");
        centerVBox.getChildren().addAll(treeView, supprButton);

        // right
        VBox rightVBox = new VBox(20);
        rightVBox.setPadding(new Insets(10));
        VBox.setVgrow(treeViewRepository, Priority.ALWAYS);
        Button redoGammeButton = new Button("Refaire les artifacts de la gamme");
        rightVBox.getChildren().addAll(treeViewRepository,redoGammeButton);
        
        // Main HBox to hold both VBoxes
        HBox mainHBox = new HBox(20, leftVBox, centerVBox, rightVBox);
        mainHBox.setPadding(new Insets(10));

        // Wrap in a VBox to return as the main view
        VBox mainVBox = new VBox(mainHBox);

        // init gammes
        getAllGamme();
        handleArtifacts();
        
        return mainVBox;
    }

    private void handleArtifacts() {
        gammeComboBox.setItems((ObservableList) FXCollections.observableArrayList(gammes.values()));
    }

    public void getAllGamme() {
         String insertGammeQuery = "SELECT id, nom, vehicule, version, repository from gamme";
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
        System.err.println(selectArtifactsQuery);
        res = dbTool.executeSelectQuery(selectArtifactsQuery);
        List<ArtifactData> artifactDataList = new ArrayList<>();
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
                }
                newItem.selectedProperty().addListener((obs, wasChecked, isNowChecked) -> {
                    if (isNowChecked) {
                        checkBoxes.add(newItem);
                    } else {
                        checkBoxes.remove(newItem);
                    }
                });
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

    protected void getArtifactsFromGitHub(String currentRepoUrl) {
        
        String jsonResponse = null;
        // Get artifacts
        ArtifactoryClient artiClient = new ArtifactoryClient();
        try {
            jsonResponse = artiClient.getArtifacts(currentRepoUrl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Handle the response, assuming it's JSON
        if (jsonResponse != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            JsonArray artifactsArray = jsonObject.getAsJsonArray("tree");

            // Creation du treeviewer
            treeViewRepository.setRoot(buildTreeRepository(artifactsArray, currentRepoUrl));
            treeViewRepository.setCellFactory(CheckBoxTreeCell.<ArtifactItem>forTreeView());
        } else {
            System.err.println("Error fetching artifacts: jsonResponse is " + jsonResponse);
        }
    }

    private CheckBoxTreeItem<ArtifactItem> buildTreeRepository(JsonArray  nodes, String currentRepoUrl) {
        CheckBoxTreeItem<ArtifactItem> rootItem = new CheckBoxTreeItem(new ArtifactItem("Repository" + currentRepoUrl, "tree", "", ""));
        rootItem.setExpanded(true);
        Map<String, CheckBoxTreeItem<ArtifactItem>> pathMap = new HashMap<>();

        for (JsonElement nodeJS : nodes) {
            JsonObject node = nodeJS.getAsJsonObject();
            String path = node.get("path").getAsString();
            String type = node.get("type").getAsString();
            String url = node.get("url").getAsString();
            addPathToTree(rootItem, pathMap, path, type, url, false);
        }
        return rootItem;
    }

}
