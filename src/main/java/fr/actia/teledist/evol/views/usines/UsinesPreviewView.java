package fr.actia.teledist.evol.views.usines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.actia.teledist.evol.models.GammeData;
import fr.actia.teledist.evol.models.UsineData;
import fr.actia.teledist.evol.tools.DatabaseTool;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UsinesPreviewView {

    private Map<Integer, List <Integer>> allGammeByUsine;
    private Map<Integer, UsineData> allUsinesDataMap;
    private Map<Integer, GammeData> allGammesDataMap;

    public SplitPane getView(Stage primaryStage) {
        

        // Left  gamme details
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        Label addGammeLabel = new Label("Ajouter une gamme à une usine :");
        Label deleteGammeLabel = new Label("Supprimer une gamme à une usine :");
        Label addUsinesLabel = new Label("Ajouter une usine:");
        Label modifUsinesLabel = new Label("Modifier une usine:");
        Label deleteUsinesLabel = new Label("Supprimer une usine:");
        leftVBox.getChildren().addAll(addGammeLabel, deleteGammeLabel, addUsinesLabel, modifUsinesLabel, deleteUsinesLabel);
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
                    gammeItem.setValue("Nom : " + allGammesDataMap.get(listIdGamme.get(i)).getNom()+ " | " +
                    "Vehicle : " + allGammesDataMap.get(listIdGamme.get(i)).getVehicule() + " | " + 
                    "Version : " + allGammesDataMap.get(listIdGamme.get(i)).getVersion());
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
        VBox.setVgrow(treeView, Priority.ALWAYS);
        // Main HBox to hold both VBoxes
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftVBox, rightVBox);
        splitPane.setDividerPositions(0.5f,0.4f);

        return splitPane;
    }


    public void getUsines() {

        DatabaseTool dbTool = new DatabaseTool();
        allGammeByUsine = new HashMap<>();
        allUsinesDataMap = new HashMap<>();
        allGammesDataMap = new HashMap<>();
       
        allGammeByUsine = dbTool.getAllJoinGammeUsines();
        allUsinesDataMap = dbTool.getAllUsines();
        allGammesDataMap = dbTool.getAllGammes();

   }
}
