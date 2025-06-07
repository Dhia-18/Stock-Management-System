package main.Controllers;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Entities.Admin;
import main.Models.AdminModel;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MainController {
    private HBox currentHBoxSelected;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private HBox dashboardMenu;
    @FXML
    private HBox customersMenu;
    @FXML
    private HBox inventoryMenu;
    @FXML
    private HBox ordersMenu;
    @FXML
    private HBox productsMenu;
    @FXML
    private HBox suppliersMenu;
    @FXML
    private HBox warehousesMenu;
    @FXML
    private HBox settingsMenu;
    @FXML
    private HBox helpMenu;
    @FXML
    private HBox logoutMenu;
    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView avatarImage;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() throws IOException{
        // Adding some animation to my avatar
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), avatarImage);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.15);
        pulse.setToY(1.15);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        loadDashboard();
        loadUser();
    }

    private void loadUser(){
        try{
            Admin user = new AdminModel().loadAdmin();

            nameLabel.setText(user.getFirstName() + " " + user.getLastName());
            emailLabel.setText(user.getEmail());
            welcomeLabel.setText("Welcome back, "+user.getFirstName());
            String path = user.getAvatarPath();
            Image image = new Image(new File(path).toURI().toString());
            avatarImage.setImage(image);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void loadDashboard() throws IOException {
        AnchorPane dashboard = FXMLLoader.load(getClass().getResource("../../ressources/Views/DashboardMenu.fxml"));
        contentArea.getChildren().setAll(dashboard);

        if(currentHBoxSelected!=null){
            currentHBoxSelected.getStyleClass().remove("selected");
            currentHBoxSelected.getStyleClass().add("menu-item");
        }
        dashboardMenu.getStyleClass().remove("menu-item");
        dashboardMenu.getStyleClass().add("selected");
        currentHBoxSelected = dashboardMenu;
    }

    @FXML
    private void loadProducts() throws IOException {
        AnchorPane products = FXMLLoader.load(getClass().getResource("../../ressources/Views/ProductsMenu.fxml"));
        contentArea.getChildren().setAll(products);

        handleClick(productsMenu);
    }

    @FXML
    private void loadWarehouses() throws IOException{
        AnchorPane warehouses = FXMLLoader.load(getClass().getResource("../../ressources/Views/WarehousesMenu.fxml"));
        contentArea.getChildren().setAll(warehouses);

        handleClick(warehousesMenu);
    }

    @FXML
    private void loadSuppliers() throws IOException{
        AnchorPane suppliers = FXMLLoader.load(getClass().getResource("../../ressources/Views/SuppliersMenu.fxml"));
        contentArea.getChildren().setAll(suppliers);

        handleClick(suppliersMenu);
    }

    @FXML
    private void loadCustomers() throws IOException{
        AnchorPane customers = FXMLLoader.load(getClass().getResource("../../ressources/Views/CustomersMenu.fxml"));
        contentArea.getChildren().setAll(customers);

        handleClick(customersMenu);
    }

    @FXML
    private void loadOrders() throws IOException{
        AnchorPane orders = FXMLLoader.load(getClass().getResource("../../ressources/Views/OrdersMenu.fxml"));
        contentArea.getChildren().setAll(orders);

        handleClick(ordersMenu);
    }

    @FXML
    private void loadInventory() throws IOException{
        AnchorPane inventory = FXMLLoader.load(getClass().getResource("../../ressources/Views/InventoryMenu.fxml"));
        contentArea.getChildren().setAll(inventory);

        handleClick(inventoryMenu);
    }

    @FXML
    private void loadSettings() throws Exception{
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/SettingsMenu.fxml"));
            Parent root = loader.load();
            SettingsMenuController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleClick(HBox menu){
        currentHBoxSelected.getStyleClass().remove("selected");
        currentHBoxSelected.getStyleClass().add("menu-item");
        menu.getStyleClass().remove("menu-item");
        menu.getStyleClass().add("selected");
        currentHBoxSelected = menu;
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(AlertType.CONFIRMATION, "Are your sure you want to log out?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Logout Confirmation");
        confirm.setHeaderText("Confirm Logout");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        } 

        try {
            // Close current window
            Stage stage = (Stage) currentHBoxSelected.getScene().getWindow();
            stage.close();

            // Load login screen
            Parent root = FXMLLoader.load(getClass().getResource("../../ressources/Views/LoginMenu.fxml"));
    
            Image icon = new Image(getClass().getResourceAsStream("../../ressources/Images/Logo.png"));
            Scene frame = new Scene(root);
            stage.isResizable();
            stage.setTitle("StoreWise");
            stage.getIcons().add(icon);
            stage.setScene(frame);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
