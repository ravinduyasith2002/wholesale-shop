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
import javafx.stage.Stage;
import lombok.Setter;
import model.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Setter
public class DeleteItemFormController implements Initializable {
    ObservableList<Item> itemList = FXCollections.observableArrayList();
    private Stage myStage;
    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnFind;

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
    void btnBackOnAction(ActionEvent event) {
        if(myStage!=null){
            myStage.close();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Item selectedItem = tblItem.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Delete "+selectedItem.getItemCode()+" : "+selectedItem.getDescription()+" ?",ButtonType.YES,ButtonType.NO);
        alert.showAndWait().ifPresent(response->{
            if(response==ButtonType.YES){

                try {
                    if(deleteItem(selectedItem)){
                        new Alert(Alert.AlertType.INFORMATION, "Item deleted successfully !").show();
                        //refresh the table
                        txtSearchValueOnAction(new ActionEvent());
                    }else {
                        new Alert(Alert.AlertType.INFORMATION, "Item delete failed !").show();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }else{
                new Alert(Alert.AlertType.INFORMATION, "User canceled the deletion !").show();
            }
        });
    }

    private boolean deleteItem(Item selectedItem) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        try(PreparedStatement stm = connection.prepareStatement("DELETE FROM item WHERE itemCode=?")){
            stm.setString(1,selectedItem.getItemCode());
            return stm.executeUpdate()>0;
        }
    }

    @FXML
    void btnFindOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearchValueOnAction(event);

    }

    @FXML
    void cmbSearchByOnAction(ActionEvent event) {
        String selectedSearchingType = cmbSearchBy.getValue();
        if(selectedSearchingType!=null){
            updateLabelSearchInstruction(selectedSearchingType);

        }
    }

    private void updateLabelSearchInstruction(String selectedSearchingType) {
        lblSearchInstruction.setText("Enter the "+selectedSearchingType.toLowerCase()+" : ");
    }

    @FXML
    void txtSearchValueOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        itemList.clear();
        String searchText = txtSearchValue.getText().trim();
        if(cmbSearchBy.getValue()==null){
            showWarning("Select searching type first !",cmbSearchBy);
            return;
        }
        String selectedSearchingType = cmbSearchBy.getValue().toLowerCase().replace(" ", "");
        if(searchText.isEmpty()){
            showWarning("Enter the "+cmbSearchBy.getValue().toLowerCase()+" to find the item !",txtSearchValue);
            return;
        }
        if("itemcode".equals(selectedSearchingType) && !validItemCode(searchText)){
            showWarning("Invalid item code...Enter in this format 'I####' !", txtSearchValue);
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

    private void loadItemsToTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItem.setItems(itemList);
    }

    private boolean findItems(String seacrhText, String selectedSearchingType) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql= "description".equals(selectedSearchingType)
                ?"SELECT * FROM item WHERE description LIKE ?"
                :"SELECT * FROM item WHERE itemCode=?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,seacrhText);
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

    private boolean validItemCode(String searhText) {
        return searhText.matches("^[Ii]\\d{4}$");
    }

    private void clearFields() {

    }

    private void showWarning(String message, Control field) {
        new Alert(Alert.AlertType.WARNING,message).showAndWait();
        field.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbSearchBy.getItems().addAll("Item Code","Description");
        //disable the delete button at the begin
        btnDelete.setDisable(true);
        tblItem.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,selectedCustomer)->{
            if(selectedCustomer!=null){
                //set the delete button on
                btnDelete.setDisable(false);
            }
        });
    }
}
