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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Entities.Supplier;
import main.Models.SupplierModel;

public class SuppliersMenuController {
    @FXML
    private TableView<Supplier> suppliersTable;
    @FXML
    private TableColumn<Supplier, Void> actionCol;
    @FXML
    private HBox addButton;
    @FXML
    private TableColumn<Supplier, String> addressCol;
    @FXML
    private TableColumn<Supplier, String> emailCol;
    @FXML
    private TableColumn<Supplier, Integer> idCol;
    @FXML
    private TableColumn<Supplier, String> nameCol;
    @FXML
    private TableColumn<Supplier, String> phoneCol;
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
    private TextField keywordInput;
    @FXML
    private TextField addressInput;
    @FXML
    private Button resetAllBtn;
    @FXML
    private Button searchResetBtn;
    @FXML
    private Button addressResetBtn;
    @FXML
    private Button applyFilterBtn;

    public void initialize(){
        setCellValueFactory();
        loadAllSuppliers();
        exportPdfButton.setOnMouseClicked(event -> {
            exportTableToPDF(suppliersTable);
        });
        exportExcelButton.setOnMouseClicked(event -> {
            exportTableToExcel(suppliersTable);
        });
    }

    private void setCellValueFactory(){
        class ButtonCell extends TableCell<Supplier, Void>{
            private HBox buttons = new HBox();
            private Button modifyBtn = new Button("Modify");
            private Button deleteBtn = new Button("Delete");
            private SupplierModel model = new SupplierModel();

            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(modifyBtn, deleteBtn);

                /* Styling */
                buttons.setStyle("-fx-alignment: center");
                modifyBtn.getStyleClass().add("modifyBtn");
                deleteBtn.getStyleClass().add("deleteBtn");

                modifyBtn.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    
                    modifySupplier(supplier);
                });
                deleteBtn.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    
                    showDeleteConfirmation(supplier); 
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty ? null: buttons);
            }

            private void showDeleteConfirmation(Supplier supplier){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Deleting Supplier: "+ supplier.getName());
                alert.setContentText("Are your sure you want to delete this Supplier?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.deleteSupplier(supplier.getId());
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                    getTableView().getItems().remove(supplier);
                }
            }
        }
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));;
        actionCol.setCellFactory(param -> new ButtonCell());
    }

    public void loadAllSuppliers(){
        SupplierModel model = new SupplierModel();

        try{
            ObservableList<Supplier> suppliersList = FXCollections.observableArrayList(model.getAllSupplier());

            suppliersTable.setItems(suppliersList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addSupplier(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/AddSupplier.fxml"));
            Parent root = loader.load();
            AddSupplierController addSupplierController = loader.getController();
            addSupplierController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void modifySupplier(Supplier supplier){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/ModifySupplier.fxml"));
            Parent root = loader.load();
            ModifySupplierController modifySupplierController = loader.getController();
            modifySupplierController.setParentController(this);
            modifySupplierController.setSupplier(supplier);

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
        String address = addressInput.getText().toLowerCase().trim();

        loadAllSuppliers();
        ObservableList<Supplier> filtered = suppliersTable.getItems().filtered(supplier ->{
            if(!keyword.isEmpty() && !supplier.getName().toLowerCase().contains(keyword)){
                return(false);
            }

            if(!address.isEmpty() && !supplier.getAddress().toLowerCase().contains(address)){
                return(false);
            }

            return(true);
        });

        filterBox.setVisible(false);
        suppliersTable.setItems(filtered);
    }

    @FXML
    private void resetKeyword(){
        keywordInput.clear();
    }

    @FXML
    private void resetAddress(){
        addressInput.clear();
    }

    @FXML
    private void resetAll(){
        resetKeyword();
        resetAddress();
    }

    private void exportTableToPDF(TableView<Supplier> suppliersTable){
        File directory = new File("PDF");
        File file = new File(directory, "Suppliers.pdf");
            
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(file)){
            PdfWriter.getInstance(document, fos);
            document.open();

            List<TableColumn<Supplier, ?>> cols = Arrays.asList(
                idCol, nameCol, addressCol, emailCol, phoneCol
            );
            PdfPTable pdfPTable = new PdfPTable(cols.size());
            pdfPTable.setWidthPercentage(100);

            for (TableColumn<Supplier, ?> col : cols) {
                pdfPTable.addCell(
                    new Phrase(col.getText(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
                );
            }

            for(Supplier item: suppliersTable.getItems()){
                for(TableColumn<Supplier, ?> col: cols){
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

    private void exportTableToExcel(TableView<Supplier> suppliersTable){
        File directory = new File("Excel");
        File file = new File(directory, "Suppliers.xlsx");

        List<TableColumn<Supplier, ?>> cols = Arrays.asList(idCol, nameCol, addressCol, emailCol, phoneCol);

        try(Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)){
            Sheet sheet = wb.createSheet("Suppliers");

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

            for (int r = 0; r < suppliersTable.getItems().size(); r++) {
                Supplier p = suppliersTable.getItems().get(r);
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
