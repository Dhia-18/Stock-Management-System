package main.Controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Models.CustomerModel;

public class AddCustomerController {
    private CustomersMenuController parentController;

    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField nameInput;
    @FXML
    private ChoiceBox<String> serviceInput;

    public void setParentController(CustomersMenuController controller){
        this.parentController = controller;
    }

    public void initialize(){
        serviceInput.getItems().addAll("Bibliothèque", "Informatique", "Enseignement", "Administration", "Médicale", "Nettoyage", "Autres");
    }

    @FXML
    private void handleAddCustomer(){
        // Inputs
        String name = nameInput.getText().trim();
        String service = serviceInput.getValue();
        String email = emailInput.getText().trim();

        List<String> validationErrors = validateInputs(name, service, email);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        CustomerModel model = new CustomerModel();

        try{
            model.saveCustomer(name, service, email);
            showSuccess("Customer added successfully!");
        } catch (SQLException e){
            e.printStackTrace();
            showError("Failed to save Customer");
        }
    }

    private List<String> validateInputs(String name, String service, String email) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is required.");
        }
        if (service == null || service.trim().isEmpty()) {
            errors.add("Service is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required.");
        }

        if (email != null && !email.trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.matches(emailRegex)) {
                errors.add("Please enter a valid email address.");
            }
        }

        return errors;
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
            parentController.loadAllCustomers();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        nameInput.clear();
        emailInput.clear();
        serviceInput.setValue(null);
    }
}
