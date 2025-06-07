package main.Controllers;

import java.sql.SQLException;


import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Entities.Admin;
import main.Models.LoginModel;

public class LoginMenuController {

    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label usernameError;
    @FXML
    private Label passwordError;

    @FXML
    private void handleLogin(){
        boolean valid = true;

        String username = usernameInput.getText().trim();
        String password = passwordInput.getText().trim();

        if(username.isEmpty()){
            usernameError.setText("Error: Invalid username");
            valid = false;
        } else{
            usernameError.setText("");
        }

        if(password.isEmpty()){
            passwordError.setText("Error: Invalid password");
            valid = false;
        } else{
            passwordError.setText("");
        }

        if(valid){
            Admin admin = new Admin(username, password);

            LoginModel model = new LoginModel();
            try{
                if(model.validLogin(admin)){
                    loadMain();
                } else{
                    usernameError.setText("Invalid username or password");
                    passwordError.setText("Invalid username or password");
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private void loadMain(){
        Stage stage = (Stage) usernameInput.getScene().getWindow();

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5));
        fadeOut.setNode(stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../../ressources/Views/MainMenu.fxml"));

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }

    @FXML
    private void sendEmailWhenForgotPassword(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        LoginModel model = new LoginModel();
        try{
            String email = model.getEmail();
            alert.setTitle("Email Successfully Sent");
            alert.setHeaderText("A temporary password has been sent to "+email);
            alert.setContentText("Kindly check your inbox to proceed with signing in.");
    
            alert.show();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}