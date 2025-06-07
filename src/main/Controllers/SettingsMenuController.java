package main.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Entities.Admin;
import main.Models.AdminModel;

public class SettingsMenuController {
    @FXML
    private ImageView avatar;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Button saveBtn;
    @FXML
    private Button uploadBtn;
    @FXML
    private TextField usernameInput;

    private MainController parentController; // Later, it will help me to refresh the main menu
    private File selectedAvatarFile;
    private Admin currentUser;

    @FXML
    public void initialize(){
        loadUser();
    }

    public void setParentController(MainController controller){
        this.parentController = controller;
    }

    private void loadUser(){
        AdminModel model = new AdminModel();

        try{
            currentUser = model.loadAdmin();

            usernameInput.setText(currentUser.getUsername());
            nameInput.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            emailInput.setText(currentUser.getEmail());
            passwordInput.setText(currentUser.getPassword());
            String path = currentUser.getAvatarPath();
            Image image = new Image(new File(path).toURI().toString());
            avatar.setImage(image);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpload(){
        FileChooser chooser =new FileChooser();
        chooser.setTitle("Select Avatar Image");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images (*.jpg, *.png)", "*.jpg", "*.png")
        );
        File file = chooser.showOpenDialog(uploadBtn.getScene().getWindow());
        if(file==null){
            return;
        }
        if(file.length() > 1_048_576){
            showError("File Too Large", "Please choose an image under 1MB.");
            return;
        }

        try(FileInputStream fis = new FileInputStream(file)){
            avatar.setImage(new Image(fis));
            selectedAvatarFile = file;
        } catch(IOException ex){
            ex.printStackTrace();
            showError("Upload Error", "Could not load the selected image.");
        }
    }

    @FXML
    private void handleSave(){
        List<String> errors = validateInputs();
        if(!errors.isEmpty()){
            showError("Validation Error", String.join("\n", errors));
            return;
        }

        String fullName = nameInput.getText().trim();
        String firstName , lastName;
        if(fullName.contains("")){
            String[] parts = fullName.split("\\s+",2);
            firstName = parts[0];
            lastName = parts[1];
        } else{
            firstName = fullName;
            lastName = "";
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(emailInput.getText().trim());
        currentUser.setPassword(passwordInput.getText());
        if(selectedAvatarFile != null){
            currentUser.setAvatarPath(selectedAvatarFile.getAbsolutePath());
        }

        try{
            new AdminModel().updateAdmin(currentUser);
            showSuccess("Saved", "Your settings have been updated.");
            parentController.initialize();
        } catch(Exception e){
            e.printStackTrace();
            showError("Save Failed", "Could not save your settings.");
        }
    }

    @FXML
    private void handleCancel(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private List<String> validateInputs(){
        List<String> list = new ArrayList<>();
        String name = nameInput.getText().trim();
        String email = emailInput.getText().trim();
        String password = passwordInput.getText();

        if (name.isEmpty()) {
            list.add("Full name is required.");
        }
        if (email.isEmpty()) {
            list.add("Email is required.");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            list.add("Email format is invalid.");
        }
        if (password.isEmpty()) {
            list.add("Password cannot be empty.");
        }
        return(list);
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }

    private void showSuccess(String header, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        a.setTitle("Success");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
