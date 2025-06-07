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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Entities.Customer;
import main.Models.CustomerModel;

public class CustomersMenuController {
    @FXML
    private TableColumn<Customer, Void> actionCol;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> emailCol;
    @FXML
    private TableColumn<Customer, Integer> idCol;
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> serviceCol;
    @FXML
    private HBox addButton;

    // Filter components
    @FXML
    private VBox filterBox;
    @FXML
    private HBox filterBtn;
    @FXML
    private TextField keywordInput;
    @FXML
    private TextField serviceInput;
    @FXML
    private Button resetAllButton;
    @FXML
    private Button searchResetBtn;
    @FXML
    private Button serviceResetBtn;
    @FXML
    private Button applyFilterBtn;

    public void initialize(){
        setCellValueFactory();
        loadAllCustomers();
    }

    private  void setCellValueFactory(){
        class ButtonCell extends TableCell<Customer, Void>{
            private HBox buttons = new HBox();
            private Button modifyBtn = new Button("Modify");
            private Button deleteBtn = new Button("Delete");
            private CustomerModel model = new CustomerModel();

            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(modifyBtn, deleteBtn);

                /* Styling */
                buttons.setStyle("-fx-alignment: center");
                modifyBtn.getStyleClass().add("modifyBtn");
                deleteBtn.getStyleClass().add("deleteBtn");

                modifyBtn.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());

                    modifyCustomer(customer);
                });
                deleteBtn.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());

                    showDeleteConfirmation(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: buttons);
            }

            private void showDeleteConfirmation(Customer customer){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Deleting customer: "+ customer.getName());
                alert.setContentText("Are your sure you want to delete this customer?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.deleteCustomer(customer.getId());
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                    getTableView().getItems().remove(customer);
                }
            }
        }
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("service"));
        actionCol.setCellFactory(param -> new ButtonCell());
    }

    public void loadAllCustomers(){
        CustomerModel model = new CustomerModel();

        try{
            ObservableList<Customer> customersList = FXCollections.observableArrayList(model.getAllCustomer());
            
            customersTable.setItems(customersList);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addCustomer(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/AddCustomer.fxml"));
            Parent root = loader.load();
            AddCustomerController addCustomerController = loader.getController();
            addCustomerController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void modifyCustomer(Customer customer){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/ModifyCustomer.fxml"));
            Parent root = loader.load();
            ModifyCustomerController modifyCustomerController = loader.getController();
            modifyCustomerController.setParentController(this);
            modifyCustomerController.setCustomer(customer);

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
        String keyword = keywordInput.getText().toLowerCase().trim();
        String service = serviceInput.getText().toLowerCase().trim();

        loadAllCustomers();
        ObservableList<Customer> filtered = customersTable.getItems().filtered(customer -> {
            if(!keyword.isEmpty() && !customer.getName().toLowerCase().contains(keyword)){
                return(false);
            }

            if(!service.isEmpty() && !customer.getService().toLowerCase().contains(service)){
                return(false);
            }

            return(true);
        });

        filterBox.setVisible(false);
        customersTable.setItems(filtered);
    }

    @FXML
    private void resetKeyword(){
        keywordInput.clear();
    }

    @FXML
    private void resetService(){
        serviceInput.clear();
    }

    @FXML
    private void resetAll(){
        resetKeyword();
        resetService();
    }
}
