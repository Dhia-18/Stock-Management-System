package main.Controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase;

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Entities.Inventory;
import main.Models.InventoryModel;
import main.Models.SupplierModel;
import main.Models.WarehouseModel;

public class InventoryMenuController {
    @FXML
    private TableColumn<Inventory, Void> actionsCol;
    @FXML
    private HBox addButton;
    @FXML
    private TableColumn<Inventory, Date> deliverCol;
    @FXML
    private TableColumn<Inventory, Date> expirationCol;
    @FXML
    private TableColumn<Inventory, Integer> idCol;
    @FXML
    private TableView<Inventory> inventoryTable;
    @FXML
    private TableColumn<Inventory, Integer> quantityCol;
    @FXML
    private TableColumn<Inventory, Integer> refCol;
    @FXML
    private TableColumn<Inventory, Integer> supplierCol;
    @FXML
    private TableColumn<Inventory, Integer> warehouseCol;
    @FXML
    private HBox exportPdfButton;
    @FXML
    private HBox exportExcelButton;

    // Filter components
    @FXML
    private VBox filterBox;
    @FXML
    private HBox filterBtn;
    @FXML
    private TextField refInput;
    @FXML
    private ChoiceBox<String> supplierInput;
    @FXML
    private ChoiceBox<String> warehouseInput;
    @FXML
    private DatePicker expirationBeforeInput;
    @FXML
    private DatePicker expirationAfterInput;
    @FXML
    private DatePicker deliverBeforeInput;
    @FXML
    private DatePicker deliverAfterInput;
    @FXML
    private Button resetBtn;
    @FXML
    private Button applyBtn;

    public void initialize(){
        setCellValueFactory();
        loadAllInventoryRows();

        exportPdfButton.setOnMouseClicked(event -> {
            exportTableToPDF(inventoryTable);
        });
        exportExcelButton.setOnMouseClicked(event -> {
            exportTableToExcel(inventoryTable);
        });
    }

    private void setCellValueFactory(){
        class ButtonCell extends TableCell<Inventory, Void>{
            private HBox buttons = new HBox();
            private Button modifyBtn = new Button("Modify");
            private Button deleteBtn = new Button("Delete");
            private InventoryModel model = new InventoryModel();
        
            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(modifyBtn, deleteBtn);
                
                /* Styling */
                buttons.setStyle("-fx-alignment: center");
                modifyBtn.getStyleClass().add("modifyBtn");
                deleteBtn.getStyleClass().add("deleteBtn");

                modifyBtn.setOnAction(event -> {
                    Inventory inventory = getTableView().getItems().get(getIndex());

                    modifyInventory(inventory);
                });
                deleteBtn.setOnAction(event -> {
                    Inventory inventory = getTableView().getItems().get(getIndex());

                    showDeleteConfirmation(inventory);

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: buttons);
            }

            private void showDeleteConfirmation(Inventory inventory){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Deleting Inventory Row");
                alert.setContentText("Are you sure you want to delete this row?");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.deleteInventory(inventory.getId());
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                    getTableView().getItems().remove(inventory);
                }
            }
        }
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        refCol.setCellValueFactory(new PropertyValueFactory<>("ref"));
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier_id"));
        supplierCol.setCellFactory(column -> new TableCell<Inventory, Integer>(){
            @Override
            protected void updateItem(Integer item, boolean empty){
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else if (item == 0) {
                    setText("NA");
                } else {
                    setText(String.valueOf(item));
                }
            }
        });
        deliverCol.setCellValueFactory(new PropertyValueFactory<>("deliver_date"));
        warehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouse_id"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        expirationCol.setCellValueFactory(new PropertyValueFactory<>("expiration_date"));
        expirationCol.setCellFactory(column -> new TableCell<Inventory, Date>(){
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else if (item == null) {
                    setText("NA");
                } else {
                    setText(item.toString());
                }
            }
        });
        actionsCol.setCellFactory(param -> new ButtonCell());
    }

    public void loadAllInventoryRows(){
        InventoryModel model = new InventoryModel();

        try{
            ObservableList<Inventory> inventoryList = FXCollections.observableArrayList(model.getAllInventoryRows());
            
            inventoryTable.setItems(inventoryList);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addInventory(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/AddInventory.fxml"));
            Parent root = loader.load();
            AddInventoryController addInventoryController = loader.getController();
            addInventoryController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void modifyInventory(Inventory inventory){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/ModifyInventory.fxml"));
            Parent root = loader.load();
            ModifyInventoryController modifyInventoryController = loader.getController();
            modifyInventoryController.setParentController(this);
            modifyInventoryController.setInventory(inventory);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Developing the filter's log
    @FXML
    private void addFilter(){
        if(filterBox.isVisible()){
            filterBox.setVisible(false);
        } else{
            filterBox.setVisible(true);
            loadWarehouses();
            loadSuppliers();
        }
    }

    private void loadWarehouses(){
        WarehouseModel model = new WarehouseModel();

        try{
            ObservableList<String> warehousesList = FXCollections.observableArrayList(model.getAllWarehouseNames());

            warehouseInput.setItems(warehousesList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void loadSuppliers(){
        SupplierModel model = new SupplierModel();

        try{
            ObservableList<String> suppliersList = FXCollections.observableArrayList(model.getAllSupplierNames());

            supplierInput.setItems(suppliersList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void applyFilter(){
        String ref = refInput.getText().trim();
        String supplierName = supplierInput.getValue();
        String warehouseName = warehouseInput.getValue();
        LocalDate expirationBefore = expirationBeforeInput.getValue();
        LocalDate expirationAfter = expirationAfterInput.getValue();
        LocalDate deliverBefore = deliverBeforeInput.getValue();
        LocalDate deliverAfter = deliverAfterInput.getValue();

        loadAllInventoryRows();

        SupplierModel supplierModel = new SupplierModel();
        WarehouseModel warehouseModel = new WarehouseModel();
        try{
            int supplierId = supplierName==null? -1 : supplierModel.findIdByName(supplierName);
            int warehouseId = warehouseName==null? -1 : warehouseModel.findIdByName(warehouseName);

            ObservableList<Inventory> filtered = inventoryTable.getItems().filtered(inventory -> {
                if(!ref.isEmpty() && !String.valueOf(inventory.getRef()).contains(ref)){
                    return(false);
                }
                if(supplierId != -1 && inventory.getSupplier_id() != supplierId){
                    return(false);
                }
                if(warehouseId != -1 && inventory.getWarehouse_id() != warehouseId){
                    return(false);
                }

                java.sql.Date expDate = inventory.getExpiration_date();
                java.sql.Date delDate = inventory.getDeliver_date();
    
                // Expiration date filters
                if (expirationBefore != null && expDate != null && expDate.toLocalDate().isAfter(expirationBefore)) {
                    return false;
                }
                if (expirationAfter != null && expDate != null && expDate.toLocalDate().isBefore(expirationAfter)) {
                    return false;
                }
    
                // Delivery date filters
                if (deliverBefore != null && delDate != null && delDate.toLocalDate().isAfter(deliverBefore)) {
                    return false;
                }
                if (deliverAfter != null && delDate != null && delDate.toLocalDate().isBefore(deliverAfter)) {
                    return false;
                }
                return(true);
            });
            filterBox.setVisible(false);
            inventoryTable.setItems(filtered);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void resetAll(){
        refInput.clear();
        supplierInput.setValue(null);
        warehouseInput.setValue(null);
        expirationAfterInput.setValue(null);
        expirationBeforeInput.setValue(null);
        deliverAfterInput.setValue(null);
        deliverBeforeInput.setValue(null);
    }

    // Exporting the table to PDF
    @FXML
    private void exportTableToPDF(TableView<Inventory> inventoryTable){
        File directory = new File("PDF");
        File file = new File(directory, "Inventory.pdf");

        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(file)){
            PdfWriter.getInstance(document, fos);
            document.open();

            List<TableColumn<Inventory, ?>> cols = Arrays.asList(
                idCol, refCol, supplierCol, deliverCol, warehouseCol, quantityCol, expirationCol
            );
            PdfPTable pdfPTable = new PdfPTable(cols.size());
            pdfPTable.setWidthPercentage(100);

            for (TableColumn<Inventory, ?> col : cols) {
                pdfPTable.addCell(
                    new Phrase(col.getText(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
                );
            }

            for(Inventory item: inventoryTable.getItems()){
                for(TableColumn<Inventory, ?> col: cols){
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

        private void exportTableToExcel(TableView<Inventory> inventoryTable){
        File directory = new File("Excel");
        File file = new File(directory, "Inventory.xlsx");

        List<TableColumn<Inventory, ?>> cols = Arrays.asList(idCol, refCol, supplierCol, deliverCol, warehouseCol, quantityCol, expirationCol);

        try(Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)){
            Sheet sheet = wb.createSheet("Inventory");

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

            for (int r = 0; r < inventoryTable.getItems().size(); r++) {
                Inventory p = inventoryTable.getItems().get(r);
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