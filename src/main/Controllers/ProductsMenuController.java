package main.Controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Entities.Product;
import main.Models.ProductModel;

public class ProductsMenuController {
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, Integer> refCol;
    @FXML
    private TableColumn<Product, String> categoryCol;
    @FXML
    private TableColumn<Product, Boolean> criticismCol;
    @FXML
    private TableColumn<Product, Void> actionCol;
    @FXML
    private HBox addButton;
    @FXML
    private HBox exportPdfButton;
    @FXML
    private HBox exportExcelButton;

    // Filter components
    @FXML
    private VBox filterBox;
    @FXML
    private Button categoryResetBtn;
    @FXML
    private ChoiceBox<String> categoryInput;
    @FXML
    private Button searchResetBtn;
    @FXML
    private TextField keywordInput;
    @FXML
    private RadioButton criticismRadioBtn1;
    @FXML
    private RadioButton criticismRadioBtn2;
    @FXML
    private RadioButton criticismRadioBtn3;
    @FXML
    private ToggleGroup criticismGroup;
    @FXML
    private Button resetAllBtn;
    @FXML
    private Button applyBtn;

    public void initialize(){
        setCellValueFactory();
        loadAllProducts();
        exportPdfButton.setOnMouseClicked(event -> {
            exportTableToPDF(productsTable);
        });
        exportExcelButton.setOnMouseClicked(event -> {
            exportTableToExcel(productsTable);
        });


        // For Filter's Logic
        categoryInput.getItems().addAll("All Products","Consumable","Non-Consumable");
        categoryInput.setValue("All Products");
        criticismGroup.selectToggle(criticismRadioBtn3);
    }

    private void setCellValueFactory(){
        class ButtonCell extends TableCell<Product, Void>{
            private HBox buttons = new HBox();
            private Button modifyBtn = new Button("Modify");
            private Button deleteBtn = new Button("Delete");
            private ProductModel model = new ProductModel();

            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(modifyBtn, deleteBtn);

                /* Styling */
                buttons.setStyle("-fx-alignment:center");
                modifyBtn.getStyleClass().add("modifyBtn");
                deleteBtn.getStyleClass().add("deleteBtn");

                modifyBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());

                    modifyProduct(product);
                });
                deleteBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());

                    showDeleteConfirmation(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: buttons);
            }

            private void showDeleteConfirmation(Product product){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Deleting Product: " + product.getName());
                alert.setContentText("Are you sure you want to delete this product?");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.deleteProduct(product.getRef());
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                    getTableView().getItems().remove(product);
                }
            }
        }
        refCol.setCellValueFactory(new PropertyValueFactory<>("ref"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        criticismCol.setCellValueFactory(new PropertyValueFactory<>("criticism"));
        actionCol.setCellFactory(param -> new ButtonCell());
    }
    
    public void loadAllProducts(){
        ProductModel model = new ProductModel();

        try{
            ObservableList<Product> productsList = FXCollections.observableArrayList(model.getAllProduct());

            productsTable.setItems(productsList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addProduct(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/AddProduct.fxml"));
            Parent root = loader.load();
            AddProductController addProductController = loader.getController();
            addProductController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void modifyProduct(Product product){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/ModifyProduct.fxml"));
            Parent root = loader.load();
            ModifyProductController modifyProductController = loader.getController();
            modifyProductController.setParentController(this);
            modifyProductController.setProduct(product);

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
        String selectedCategory = categoryInput.getValue();
        String keyword = keywordInput.getText().toLowerCase().trim();
        Toggle selectedToggle = criticismGroup.getSelectedToggle();

        loadAllProducts();
        ObservableList<Product> filtered = productsTable.getItems().filtered(product ->{
            if(!selectedCategory.equals("All Products") && !product.getCategory().equalsIgnoreCase(selectedCategory)){
                return (false);
            } 
            
            if(!keyword.isEmpty() && !product.getName().toLowerCase().contains(keyword)){
                return (false);
            }

            if(selectedToggle == criticismRadioBtn1 && !product.getCriticism()){
                return (false);
            }
                
            if(selectedToggle == criticismRadioBtn2 && product.getCriticism()){
                return (false);
            }

           return (true);
        });

        filterBox.setVisible(false);
        productsTable.setItems(filtered);
    }

    @FXML
    private void resetCategory(){
        categoryInput.setValue("All Products");
    }

    @FXML
    private void resetKeyword(){
        keywordInput.clear();
    }

    @FXML
    private void resetAll(){
        resetCategory();
        resetKeyword();
        criticismGroup.selectToggle(criticismRadioBtn3);
    }

    // Exporting the table to PDF
    private void exportTableToPDF(TableView<Product> productsTable){
        File directory = new File("PDF");
        File file = new File(directory, "Products.pdf");
            
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(file)){
            PdfWriter.getInstance(document, fos);
            document.open();

            List<TableColumn<Product, ?>> cols = Arrays.asList(
                refCol, nameCol, categoryCol, criticismCol
            );
            PdfPTable pdfPTable = new PdfPTable(cols.size());
            pdfPTable.setWidthPercentage(100);

            for (TableColumn<Product, ?> col : cols) {
                pdfPTable.addCell(
                    new Phrase(col.getText(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
                );
            }

            for(Product item: productsTable.getItems()){
                for(TableColumn<Product, ?> col: cols){
                    Object cell = col.getCellData(item);
                    pdfPTable.addCell(new Phrase(cell == null ? "" : cell.toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                }
            }
            document.add(pdfPTable);
            document.close();
            showSuccess("Exported to "+ file.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
            showError("Export failed");
        }
    }

    private void exportTableToExcel(TableView<Product> productsTable){
        File directory = new File("Excel");
        File file = new File(directory, "Products.xlsx");

        List<TableColumn<Product, ?>> cols = Arrays.asList(refCol, nameCol, categoryCol, criticismCol);

        try(Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)){
            Sheet sheet = wb.createSheet("Products");

            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols.get(i).getText());
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
            }

            for (int r = 0; r < productsTable.getItems().size(); r++) {
                Product p = productsTable.getItems().get(r);
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < cols.size(); c++) {
                    Object value = cols.get(c).getCellData(p);
                    Cell cell = row.createCell(c);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else {
                        cell.setCellValue(value == null ? "" : value.toString());
                    }
                }
            }
          
            for (int i = 0; i < cols.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(fos);
            showSuccess("Excel exported to "+ file.getAbsolutePath());
        } catch(Exception e){
            e.printStackTrace();
            showError("Excel export failed");
        }
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

        alert.show();
    }
}
