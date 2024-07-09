package fr.actia.teledist.evol.views.usines;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.unboundid.ldap.matchingrules.IntegerMatchingRule;

import fr.actia.teledist.evol.models.ArtifactData;
import fr.actia.teledist.evol.models.GammeData;
import fr.actia.teledist.evol.models.UsineData;
import fr.actia.teledist.evol.tools.ArtifactoryClient;
import fr.actia.teledist.evol.tools.DatabaseTool;
import fr.actia.teledist.evol.treeviewer.CheckboxesCustom;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class UsinesPreviewView {

    private Map<Integer, List <Integer>> allGammeByUsine;
    private Map<Integer, UsineData> allUsinesDataMap;
    private Map<Integer, GammeData> allGammesDataMap;

    public SplitPane getView(Stage primaryStage) {
        

        // Left  gamme details
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        Label addGammeLabel = new Label("Ajouter une game à :");
        Label addUsinesLabel = new Label("Ajouter une usine:");
        Label modifUsinesLabel = new Label("Modifier une usine:");
        Label deleteUsinesLabel = new Label("Supprimer une usine:");
        leftVBox.getChildren().addAll(addGammeLabel, addUsinesLabel, modifUsinesLabel, deleteUsinesLabel);
        // Create the root item
        TreeItem<String> rootItem = new TreeItem<>("Usines");
        rootItem.setExpanded(true);

        // init datas
        getUsines();

        // Populate the TreeView with data
        for (Map.Entry<Integer, List<Integer>> entry : allGammeByUsine.entrySet()) {
            Integer idUsine = entry.getKey();
            List<Integer> listIdGamme = entry.getValue();

            TreeItem<String> usineItem = new TreeItem<String>();
            usineItem.setValue(allUsinesDataMap.get(idUsine).getNom() + " | " + allUsinesDataMap.get(idUsine).getPays());

            for (int i=0; i< listIdGamme.size(); i++) {
                if(allGammesDataMap.get(listIdGamme.get(i))!= null) {
                    TreeItem<String> gammeItem = new TreeItem<String>();
                    gammeItem.setValue(allGammesDataMap.get(listIdGamme.get(i)).getNom()+ " | " 
                        + allGammesDataMap.get(listIdGamme.get(i)).getVehicule() + " | " + 
                        allGammesDataMap.get(listIdGamme.get(i)).getNom());
                    usineItem.getChildren().add(gammeItem);
                }
            }
            rootItem.getChildren().add(usineItem);
        }

        // Create the TreeView
        TreeView<String> treeView = new TreeView<>(rootItem);

        // Create the layout and add the TreeView
        
        VBox rightVBox = new VBox(10);
        rightVBox.setPadding(new Insets(10));
        rightVBox.getChildren().addAll(treeView);
        
        // Main HBox to hold both VBoxes
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftVBox, rightVBox);
        splitPane.setDividerPositions(0.5f,0.4f);

        return splitPane;
    }


    public void getUsines() {

        String selectGammeQuery = "SELECT idgamme, idusine from joingammeusines";
        String selectAllUsinesQuery = "SELECT id, nom, pays from usines";
        
        DatabaseTool dbTool = new DatabaseTool();
        allGammeByUsine = new HashMap<>();
        allUsinesDataMap = new HashMap<>();
        allGammesDataMap = new HashMap<>();
        
        ResultSet res = dbTool.executeSelectQuery(selectGammeQuery);
        try {
            while (res.next()) {
                if (allGammeByUsine.get(res.getInt("idusine"))== null){
                    List <Integer> listidGamme= new ArrayList<Integer>();
                    listidGamme.add(res.getInt("idgamme"));
                    allGammeByUsine.put(res.getInt("idusine"), listidGamme);
                } else {
                    List <Integer> listidGamme= allGammeByUsine.get(res.getInt("idusine"));
                    listidGamme.add(res.getInt("idgamme"));
                    allGammeByUsine.put(res.getInt("idusine"), listidGamme);
                }
                //allGammeByUsine.put(res.getInt("idusine"), res.getInt("idgamme"));
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }

        res = dbTool.executeSelectQuery(selectAllUsinesQuery);
        try {
            while (res.next()) {
                allUsinesDataMap.put(res.getInt("id"), new UsineData(res.getInt("id"), res.getString("nom"), res.getString("pays")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertGammeQuery = "SELECT id, nom, vehicule, version, repository from gamme";
        // All gammes
        res = dbTool.executeSelectQuery(insertGammeQuery);
        try {
            while (res.next()) {
                GammeData gammeData = new GammeData(res.getInt("id"), res.getString("nom"),
                res.getString("vehicule"), res.getString("version"), res.getString("repository"));
                allGammesDataMap.put(res.getInt("id"), gammeData);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
   }
}
