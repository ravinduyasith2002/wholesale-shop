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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Setter
public class ViewSelectedItemFormController implements Initializable {
    ObservableList<Item> itemList = FXCollections.observableArrayList();

    private Stage myStage;
    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnFind;

    @FXML
    private JFXComboBox<String> cmbItemSearchType;

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
    private Label lblSearchingType;

    @FXML
    private TableView<Item> tblItemDetails;

    @FXML
    private JFXTextField txtSearchText;

    @FXML
    void cmbItemSearchTypeOnAction(ActionEvent event) {
        String selectedSearchingType = cmbItemSearchType.getValue().toLowerCase();
        if(selectedSearchingType!=null){
            setSeachingInstruction(selectedSearchingType);
        }
    }

    private void setSeachingInstruction(String selectedSearchingType) {
        lblSearchingType.setText("Enter the "+selectedSearchingType+" : ");
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage!=null){
            myStage.close();
        }
    }

    @FXML
    void btnFindOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearchTextOnAction(event);
    }

    @FXML
    void txtSearchTextOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
         itemList.clear();
         String searchText = txtSearchText.getText().trim();
         if(cmbItemSearchType.getValue()==null){
             showWarning("Please select a searching type first !",cmbItemSearchType);
             return;
         }
         String selectedSearchingType = cmbItemSearchType.getValue().toLowerCase().replace(" ","");
         if(searchText.isEmpty()){
             showWarning("Enter text to find the item !",txtSearchText);
             return;
         }

         else if(selectedSearchingType.equals("description")){
             searchText = "%"+searchText+"%";
         }
        if("itemcode".equals(selectedSearchingType) && !validItemCode(searchText)){
            showWarning("Invalid item code...Enter in this format 'I####' !",txtSearchText);
            return;
        }

         if(findTheItem(searchText,selectedSearchingType)){
             loadItemToTheTable();
         }else{
             showWarning("No items matching your search !",txtSearchText);
         }
    }

    private void loadItemToTheTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItemDetails.setItems(itemList);
    }

    private boolean findTheItem(String searchText, String selectedSearchingType) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = selectedSearchingType.equals("description")
                ? "SELECT * FROM item WHERE description LIKE ?"
                : "SELECT * FROM item WHERE itemCode = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1, searchText);
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
        }return !itemList.isEmpty();
    }

    private boolean validItemCode(String itemCode) {
        return itemCode.matches("^[Ii]\\d{4}$");
    }

    private void showWarning(String message, Control field) {
        new Alert(Alert.AlertType.WARNING, message).show();
        field.requestFocus();
    }
    private void flag(String message) {
        new Alert(Alert.AlertType.WARNING, message).show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbItemSearchType.getItems().addAll("Item Code","Description");
    }
}
