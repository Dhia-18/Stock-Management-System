package main.Controllers;

import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Entities.Warehouse;
import main.Models.WarehouseModel;

public class WarehousesMenuController{
    @FXML
    private TableView<Warehouse> warehousesTable;
    @FXML
    private TableColumn<Warehouse, Void> actionCol;
    @FXML
    private HBox addButton;
    @FXML
    private TableColumn<Warehouse, Integer> idCol;
    @FXML
    private TableColumn<Warehouse, String> nameCol;
    @FXML
    private TableColumn<Warehouse, String> serviceCol;

    // Filter components
    @FXML
    private VBox filterBox;
    @FXML
    private HBox filterBtn;
    @FXML
    private TextField keywordInput;
    @FXML
    private ChoiceBox<String> serviceInput;
    @FXML
    private Button resetAllBtn;
    @FXML
    private Button searchResetBtn;
    @FXML
    private Button serviceResetBtn;


    public void initialize(){
        setCellValueFactory();
        loadAllWarehouses();

        serviceInput.getItems().addAll("Bibliothèque", "Magasin", "Informatique", "Enseignement", "Administration", "Médicale", "Nettoyage", "Autres");
        serviceInput.setValue("All Services");
    }

    private void setCellValueFactory(){
        class ButtonCell extends TableCell<Warehouse, Void>{
            private HBox buttons = new HBox();
            private Button modifyBtn = new Button("Modify");
            private Button deleteBtn = new Button("Delete");
            private WarehouseModel model = new WarehouseModel();

            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(modifyBtn, deleteBtn);

                /* Styling */
                buttons.setStyle("-fx-alignment:center");
                modifyBtn.getStyleClass().add("modifyBtn");
                deleteBtn.getStyleClass().add("deleteBtn");

                modifyBtn.setOnAction(event -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());

                    modifyWarehouse(warehouse);
                });
                deleteBtn.setOnAction(event -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());

                    showDeleteConfirmation(warehouse);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: buttons);
            }

            private void showDeleteConfirmation(Warehouse warehouse){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle(("Delete Confirmation"));
                alert.setHeaderText("Deleting Warehouse: "+ warehouse.getName());
                alert.setContentText("Are you sure you want to delete this warehouse?");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.deleteWarehouse(warehouse.getId());
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                    getTableView().getItems().remove(warehouse);
                }
            }
        }
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("service"));
        actionCol.setCellFactory(param -> new ButtonCell());
    }

    public void loadAllWarehouses(){
        WarehouseModel model = new WarehouseModel();

        try{
            ObservableList<Warehouse> warehousesList = FXCollections.observableArrayList(model.getAllWarehouse());

            warehousesTable.setItems(warehousesList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addWarehouse(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/AddWarehouse.fxml"));
            Parent root = loader.load();
            AddWarehouseController addProductController = loader.getController();
            addProductController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void modifyWarehouse(Warehouse warehouse){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/ModifyWarehouse.fxml"));
            Parent root = loader.load();
            ModifyWarehouseController modifyWarehouseController = loader.getController();
            modifyWarehouseController.setParentController(this);
            modifyWarehouseController.setWarehouse(warehouse);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Developing the filter's logic
    @FXML
    private void addFilter(){
        if(filterBox.isVisible()){
            filterBox.setVisible(false);
        } else{
            filterBox.setVisible(true);
        }
    }

    @FXML
    private void applyFilter(){
        String selectedService = serviceInput.getValue();
        String keyword = keywordInput.getText().toLowerCase().trim();

        loadAllWarehouses();
        ObservableList<Warehouse> filtered = warehousesTable.getItems().filtered(warehouse -> {
            if(!selectedService.equals("All Services") && !warehouse.getService().equals(selectedService)){
                return(false);
            }
            if(!keyword.isEmpty() && !warehouse.getName().toLowerCase().contains(keyword)){
                return(false);
            }
            return(true);
        });
        filterBox.setVisible(false);
        warehousesTable.setItems(filtered);
    }

    @FXML
    private void resetService(){
        serviceInput.setValue("All Services");
    }

    @FXML
    private void resetKeyword(){
        keywordInput.clear();
    }

    @FXML
    private void resetAll(){
        resetService();
        resetKeyword();
    }
}