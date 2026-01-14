package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Setter;
import model.Item;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Setter
public class UpdateItemFormController implements Initializable {
    ObservableList<Item> itemList = FXCollections.observableArrayList();
    private Stage myStage;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnFind;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXComboBox<String> cmbSearchBy;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colPackSize;

    @FXML
    private TableColumn<?, ?> colQtyOnHand;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private Label lblSearchInstruction;

    @FXML
    private TableView<Item> tblItem;

    @FXML
    private JFXTextField txtSearchValue;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtUpdateDescription;

    @FXML
    private JFXTextField txtUpdatePackSize;

    @FXML
    private JFXTextField txtUpdateQty;

    @FXML
    private JFXTextField txtUpdateUnitPrice;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage!=null){
            myStage.close();
        }
    }

    @FXML
    void btnFindOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearchValueOnAction(event);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try{
            String itemCode = txtItemCode.getText();
            String updatedDescription = txtUpdateDescription.getText();
            String updatedPackSize = txtUpdatePackSize.getText();
            Integer updatedQtyOnHand;

            try{
                updatedQtyOnHand = Integer.parseInt(txtUpdateQty.getText());
            }catch (NumberFormatException _){
                new Alert(Alert.AlertType.WARNING,"QTY must be a valid number !").show();
                return;
            }
            Double updatedUnitPrice;
            try{
                updatedUnitPrice = Double.parseDouble(txtUpdateUnitPrice.getText());
            }catch(NumberFormatException _){
                new Alert(Alert.AlertType.WARNING,"Unit price must be a valid number !").show();
                return;
            }

            //Guard clauses
            if(updatedDescription.isEmpty() || updatedDescription.length()>30){
                showWarning("Description cannot be empty and must be less than 30 letters !",txtUpdateDescription);
                return;
            }
            if(updatedPackSize.isEmpty() || updatedPackSize.length()>20){
                showWarning("Pack size cannot be empty and must be less than 20 letters !",txtUpdatePackSize);
                return;
            }
            if(updatedUnitPrice<0){
                showWarning("Unit price cannot be less than 0 !",txtUpdateUnitPrice);
                return;
            }if(updatedQtyOnHand<0){
                showWarning("Qty cannot be less than 0 !",txtUpdateQty);
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Update this item?",ButtonType.YES,ButtonType.NO);
            if(alert.showAndWait().get() == ButtonType.YES){
                Item item = new Item(itemCode,updatedDescription,updatedPackSize,updatedUnitPrice,updatedQtyOnHand);
                if(updateItem(item)){
                    new Alert(Alert.AlertType.INFORMATION,"Item updated successfully !").show();
                    //refresh the table after update
                    txtSearchValueOnAction(new ActionEvent());
                    //clear the sections and fields
                    tblItem.getSelectionModel().clearSelection();
                    clearFields();
                    setEditableFieldsEnabled(false);

                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Update failed !").show();
                }
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();

        }
    }

    private boolean updateItem(Item item) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        try(PreparedStatement stm = connection.prepareStatement("UPDATE item SET description=?,packSize=?,unitPrice=?,qtyOnHand=? WHERE itemCode=?")){
            stm.setString(1,item.getDescription());
            stm.setString(2,item.getPackSize());
            stm.setDouble(3, item.getUnitPrice());
            stm.setInt(4,item.getQtyOnHand());
            stm.setString(5,item.getItemCode());
            return stm.executeUpdate()>0;
        }

    }

    @FXML
    void cmbSearchByOnAction(ActionEvent event) {
        String selectedSearchType = cmbSearchBy.getValue().toLowerCase();
        if(selectedSearchType!=null){
            setSearchInstruction(selectedSearchType);
        }
    }

    private void setSearchInstruction(String selectedSearchType) {
        lblSearchInstruction.setText("Enter the "+selectedSearchType+" : ");
    }

    @FXML
    void tblItemSearchOnMouseClicked(MouseEvent event) {
    }

    @FXML
    void txtSearchValueOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        itemList.clear();
        clearFields();
        setEditableFieldsEnabled(false);
        String searchText = txtSearchValue.getText().trim();
        if(cmbSearchBy.getValue()==null){
            showWarning("Please select a searching type first !",cmbSearchBy);
            return;
        }
        String selectedSearchingType = cmbSearchBy.getValue().toLowerCase().replace(" ","");
        if(searchText.isEmpty()){
            showWarning("Enter the "+cmbSearchBy.getValue().toLowerCase()+" to find the item !",txtSearchValue);
            return;
        }
        if("itemcode".equals(selectedSearchingType) && !validItemCode(searchText)){
            showWarning("Invalid item code...Enter in this format 'I####' !",txtSearchValue);
            return;
        }
        if("description".equals(selectedSearchingType)){
            searchText = "%"+searchText+"%";
        }
        if(findItems(searchText,selectedSearchingType)){
            loadItemsToTable();
        }else{
            showWarning("No items maching your search !",txtSearchValue);
        }

    }

    private boolean validItemCode(String itemCode) {
        return itemCode.matches("^[Ii]\\d{4}$");
    }

    private void loadItemsToTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItem.setItems(itemList);
    }

    private boolean findItems(String searchText, String selectedSearchingType) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "description".equals(selectedSearchingType)
                ?"SELECT * FROM item WHERE description LIKE ?"
                :"SELECT * FROM item WHERE itemCode = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,searchText);
            try(ResultSet rst = stm.executeQuery()){
                while (rst.next()){
                    itemList.add(new Item(
                       rst.getString("itemCode"),
                       rst.getString("description"),
                       rst.getString("packSize"),
                       rst.getBigDecimal("unitPrice").doubleValue(),
                       rst.getInt("qtyOnHand")
                    ));
                }
            }
        }
        return !itemList.isEmpty();
    }

    private void showWarning(String message, Control field) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
        field.requestFocus();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //load the combo box
        cmbSearchBy.getItems().addAll("Item Code","Description");

        //set text fields disable
        setEditableFieldsEnabled(false);
        tblItem.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,selectedItem)->{
            if(selectedItem != null){
                fillFields(selectedItem);
                setEditableFieldsEnabled(true);
                btnUpdate.setDisable(false);
            }else{
                clearFields();
                setEditableFieldsEnabled(false);
            }
        });
    }

    private void clearFields() {
        txtItemCode.clear();
        txtUpdateDescription.clear();
        txtUpdatePackSize.clear();
        txtUpdateUnitPrice.clear();
        txtUpdateQty.clear();
    }

    private void fillFields(Item selectedItem) {
        txtItemCode.setText(selectedItem.getItemCode());
        txtUpdateDescription.setText(selectedItem.getDescription());
        txtUpdatePackSize.setText(selectedItem.getPackSize());
        txtUpdateUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
        txtUpdateQty.setText(String.valueOf(selectedItem.getQtyOnHand()));
    }

    private void setEditableFieldsEnabled(boolean enabled) {
        //keep the item code disable due to primary key
        txtItemCode.setDisable(true);

        txtUpdateDescription.setDisable(!enabled);
        txtUpdatePackSize.setDisable(!enabled);
        txtUpdateUnitPrice.setDisable(!enabled);
        txtUpdateQty.setDisable(!enabled);
        btnUpdate.setDisable(!enabled);
    }
}
