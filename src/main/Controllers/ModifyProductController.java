package main.Controllers;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Entities.Product;
import main.Models.InventoryModel;
import main.Models.ProductModel;

public class ModifyProductController {
    @FXML
    private ChoiceBox<String> categoryInput;
    @FXML
    private Button clearBtn;
    @FXML
    private CheckBox criticismInput;
    @FXML
    private DatePicker expirationInput;
    @FXML
    private Button modifyBtn;
    @FXML
    private TextField nameInput;

    private ProductsMenuController parentController; // Later, it will help me to refresh the products table after modifying a product.
    private Product product;

    @FXML
    private void initialize() throws IOException{
        categoryInput.getItems().addAll("Consumable","Non-Consumable");
        categoryInput.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            handleChoiceSelection(newVal);
        });
    }

    private void handleChoiceSelection(String selected){
        if(selected.equals("Non-Consumable")){
            expirationInput.setValue(null);
            expirationInput.setDisable(true);
        } else{
            expirationInput.setDisable(false);
        }
    }

    public void setParentController(ProductsMenuController controller){
        this.parentController = controller;
    }

    public void setProduct(Product product){
        this.product = product;
        setInputs();
    }

    private void setInputs(){
        InventoryModel model = new InventoryModel();

        if(product == null) return;
        nameInput.setText(product.getName());
        categoryInput.setValue(product.getCategory());
        criticismInput.setSelected(product.getCriticism());
        if(product.getCategory().equals("Consumable")){
            try{
                Date expiration_date = model.getExpirationDate(product.getRef());
                
                LocalDate localDate = expiration_date.toLocalDate();
                expirationInput.setValue(localDate);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleModifyProduct(){
        String productName = nameInput.getText().trim();
        String category = categoryInput.getValue();
        LocalDate expirationDate = expirationInput.getValue();
        boolean criticism = criticismInput.isSelected();

        List<String> validationErrors = validateInputs(productName, category, expirationDate);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        ProductModel productModel = new ProductModel();
        try{
            productModel.updateProduct(product.getRef(), productName, category, expirationDate, criticism);
            showSuccess("Product updated Successfully!");
        } catch(SQLException e){
            e.printStackTrace();
            showError("Failed to update product!");
        }
    }

    private List<String> validateInputs(String productName, String category, LocalDate expirationDate){
        List<String> errors = new ArrayList<>();

        if(productName.isEmpty()){
            errors.add("Product name is required.");
        }
        if(category.equals("Consumable") && expirationDate == null){
            errors.add("Expiration date must be provided for consumable products.");
        }

        return(errors);
    }
    
    private void showError(String msg){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("ops...");
        alert.setContentText(msg);
        
        alert.show();
    }

    private void showSuccess(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(msg);
        
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            Stage stage = (Stage) nameInput.getScene().getWindow();
            parentController.loadAllProducts();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        nameInput.clear();
        categoryInput.setValue("Consumable");
        expirationInput.setValue(null);
        criticismInput.setSelected(false);
    }
}