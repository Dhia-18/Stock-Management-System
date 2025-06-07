package main.Controllers;

import java.io.File;
import java.io.FileOutputStream;
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
import main.Entities.Order;
import main.Models.OrderModel;
import main.Models.WarehouseModel;

public class OrdersMenuController {
    @FXML
    private TableColumn<Order, Void> actionCol;
    @FXML
    private TableColumn<Order, String> destinationWarehouseCol;
    @FXML
    private TableColumn<Order, Integer> idCol;
    @FXML
    private TableColumn<Order, String> nameCol;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> quantityCol;
    @FXML
    private TableColumn<Order, String> sourceWarehouseCol;
    @FXML
    private TableColumn<Order, String> deliverCol;
    @FXML
    private TableColumn<Order, String> statusCol;
    @FXML
    private HBox addButton;
    @FXML
    private HBox exportPdfButton;
    @FXML
    private HBox exportExcelButton;

    // Filter components
    @FXML
    private Button applyBtn;
    @FXML
    private DatePicker deliveredAfterInput;
    @FXML
    private DatePicker deliveredBeforeInput;
    @FXML
    private VBox filterBox;
    @FXML
    private HBox filterButton;
    @FXML
    private TextField refInput;
    @FXML
    private Button resetBtn;
    @FXML
    private ChoiceBox<String> statusInput;
    @FXML
    private ChoiceBox<String> warehouseDestinationInput;
    @FXML
    private ChoiceBox<String> warehouseSourceInput;

    public void initialize(){
        setCellValueFactory();
        loadAllOrders();

        exportPdfButton.setOnMouseClicked(event -> {
            exportTableToPDF(ordersTable);
        });
        exportExcelButton.setOnMouseClicked(event -> {
            exportTableToExcel(ordersTable);
        });

        // For Filter's logic
        statusInput.getItems().addAll("All Status", "Pending", "Shipped", "Cancelled");
        statusInput.setValue("All Status");
    }

    private void setCellValueFactory(){
        class ButtonCell extends TableCell<Order, Void>{
            private HBox buttons = new HBox();
            private Button shipBtn = new Button("Ship");
            private Button cancelBtn = new Button("Cancel");
            private OrderModel model = new OrderModel();

            public ButtonCell(){
                buttons.setSpacing(5);
                buttons.getChildren().addAll(shipBtn, cancelBtn);

                /* Styling */
                shipBtn.setPrefWidth(55);
                buttons.setStyle("-fx-alignment: center");
                shipBtn.getStyleClass().add("shipBtn");
                cancelBtn.getStyleClass().add("cancelBtn");

                shipBtn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());

                    shipOrderAndReload(order);
                });

                cancelBtn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());

                    showCancelConfirmation(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setGraphic(null);
                } else{
                    Order order = getTableView().getItems().get(getIndex());

                    boolean canShip = order.getStatus().equals("Pending") && !order.getDeliver_date().toLocalDate().isAfter(LocalDate.now());
                    shipBtn.setDisable(!canShip);
                    
                    cancelBtn.setDisable(!order.getStatus().equals("Pending"));

                    setGraphic(buttons);
                }
            }

            private void showCancelConfirmation(Order order){
                Alert confirm = new Alert(AlertType.CONFIRMATION, "Cancel this Order? No stock will change.", ButtonType.OK, ButtonType.CANCEL);
                confirm.setHeaderText("Ship Order #"+ order.getId());

                Optional<ButtonType> result = confirm.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.cancelOrder(order.getId());
                            loadAllOrders();
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }).start();
                }
            }

            private void shipOrderAndReload(Order order){
                Alert confirm = new Alert(AlertType.CONFIRMATION, "Ship this order now?", ButtonType.OK, ButtonType.CANCEL);
                confirm.setHeaderText("Ship order #"+order.getId());
                Optional<ButtonType> res = confirm.showAndWait();
                if(res.isPresent() && res.get() == ButtonType.OK){
                    new Thread(()->{
                        try{
                            model.shipOrder(order.getId());
                            loadAllOrders();
                        } catch(SQLException ex){
                            ex.printStackTrace();
                        }
                    }).start();
                }
            }
        }
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        sourceWarehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouseFromName"));
        destinationWarehouseCol.setCellValueFactory(new PropertyValueFactory<>("warehouseToName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliverCol.setCellValueFactory(new PropertyValueFactory<>("deliver_date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionCol.setCellFactory(param -> new ButtonCell());
    }

    public void loadAllOrders(){
        OrderModel model = new OrderModel();

        try{
            ObservableList<Order> ordersList = FXCollections.observableArrayList(model.getAllOrders());

            ordersTable.setItems(ordersList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addOrder(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../ressources/Views/addOrder.fxml"));
            Parent root = loader.load();
            AddOrderController addOrderController = loader.getController();
            addOrderController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e){
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
        String ref = refInput.getText().trim();
        String warehouseSourceName = warehouseSourceInput.getValue();
        String warehouseDestinationName = warehouseDestinationInput.getValue();
        LocalDate deliveredBefore = deliveredBeforeInput.getValue();
        LocalDate deliveredAfter = deliveredAfterInput.getValue();
        String status = statusInput.getValue();

        loadAllOrders();

        WarehouseModel warehouseModel = new WarehouseModel();
        try{
            int warehouseSourceId = warehouseSourceName == null ? -1 : warehouseModel.findIdByName(warehouseSourceName);
            int warehouseDestinationId = warehouseDestinationName == null ? -1 : warehouseModel.findIdByName(warehouseDestinationName);

            ObservableList<Order> filtered = ordersTable.getItems().filtered(order -> {
                if(!ref.isEmpty() && !String.valueOf(order.getRef()).contains(ref)){
                    return(false);
                }
                if(warehouseSourceId != -1 && order.getWarehouse_from_id() != warehouseSourceId){
                    return(false);
                }
                if(warehouseDestinationId != -1 && order.getWarehouse_to_id() != warehouseDestinationId){
                    return(false);
                }
                if(!status.equals("All Status") && !status.equals(order.getStatus())){
                    return(false);
                }

                java.sql.Date deliveredDate = order.getDeliver_date();
                if(deliveredBefore!=null && deliveredDate!=null && deliveredDate.toLocalDate().isAfter(deliveredBefore)){
                    return(false);
                }
                if(deliveredAfter!=null && deliveredDate!=null && deliveredDate.toLocalDate().isBefore(deliveredAfter)){
                    return(false);
                }
                return(true);
            });
            filterBox.setVisible(false);
            ordersTable.setItems(filtered);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void resetAll(){
        refInput.clear();
        warehouseDestinationInput.setValue(null);
        warehouseSourceInput.setValue(null);
        deliveredAfterInput.setValue(null);
        deliveredBeforeInput.setValue(null);
        statusInput.setValue("All Status");
    }

    // Exporting the table to PDF
    @FXML
    private void exportTableToPDF(TableView<Order> ordersTable){
        File directory = new File("PDF");
        File file = new File(directory, "Orders.pdf");
            
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(file)){
            PdfWriter.getInstance(document, fos);
            document.open();

            List<TableColumn<Order, ?>> cols = Arrays.asList(
                idCol, nameCol, sourceWarehouseCol, destinationWarehouseCol, quantityCol, deliverCol, statusCol
            );
            PdfPTable pdfPTable = new PdfPTable(cols.size());
            pdfPTable.setWidthPercentage(100);

            for (TableColumn<Order, ?> col : cols) {
                pdfPTable.addCell(
                    new Phrase(col.getText(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
                );
            }

            for(Order item: ordersTable.getItems()){
                for(TableColumn<Order, ?> col: cols){
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

    private void exportTableToExcel(TableView<Order> ordersTable){
        File directory = new File("Excel");
        File file = new File(directory, "Orders.xlsx");

        List<TableColumn<Order, ?>> cols = Arrays.asList(idCol, nameCol, sourceWarehouseCol, destinationWarehouseCol, quantityCol, deliverCol, statusCol);

        try(Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)){
            Sheet sheet = wb.createSheet("Orders");

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

            for (int r = 0; r < ordersTable.getItems().size(); r++) {
                Order p = ordersTable.getItems().get(r);
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
