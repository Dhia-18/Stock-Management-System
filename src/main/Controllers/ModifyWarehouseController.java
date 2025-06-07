package main.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Entities.Warehouse;
import main.Models.WarehouseModel;

public class ModifyWarehouseController {
    @FXML
    private Button clearButton;
    @FXML
    private Button modifyButton;
    @FXML
    private TextField nameInput;
    @FXML
    private ChoiceBox<String> serviceInput;

    private WarehousesMenuController parentController; // Later, it will help me to refresh the warehouses table after modifying a warehouse.
    private Warehouse warehouse;

    @FXML
    private void initialize() throws IOException{
        // Set service choiceBox
        serviceInput.getItems().addAll("Bibliothèque", "Magasin", "Informatique", "Enseignement", "Administration", "Médicale", "Nettoyage", "Autres");
    }

    public void setParentController(WarehousesMenuController controller){
        this.parentController = controller;
    }

    public void setWarehouse(Warehouse warehouse){
        this.warehouse = warehouse;

        if(warehouse == null) return;
        nameInput.setText(warehouse.getName());
        serviceInput.setValue(warehouse.getService());
    }

    @FXML
    private void handleModifyWarehouse(){
        String warehouseName = nameInput.getText().trim();
        String service = serviceInput.getValue();

        if(warehouseName.isEmpty() || service==null){
            showError("Please fill in all the required fields!");
            return;
        }

        WarehouseModel model = new WarehouseModel();
        try{
            model.updateWarehouse(warehouse.getId(), warehouseName, service);
            showSuccess("Warehouse updated Successfully!");
        } catch (SQLException e){
            e.printStackTrace();
            showError("Failed to update warehouse!");
        }
    }

    private void showError(String msg){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        
        alert.show();
    }

    private void showSuccess(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            Stage stage = (Stage) nameInput.getScene().getWindow();
            parentController.loadAllWarehouses();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        nameInput.clear();
        serviceInput.setValue("");
    }
}
