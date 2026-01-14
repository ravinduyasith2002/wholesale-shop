package controller;

import com.jfoenix.controls.JFXButton;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class ViewAllItemsFormController implements Initializable {
    ObservableList<Item> itemList = FXCollections.observableArrayList();
    private Stage myStage;
    @FXML
    private JFXButton btnBack;

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
    private TableView<Item> tblItemDetails;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage != null){
            myStage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemList.clear();
        try {
            if(addItemsToItemList()){
                loadItemsToTable();
            }else{
                new Alert(Alert.AlertType.ERROR, "Sorry, No Customers Added Yet !").showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadItemsToTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItemDetails.setItems(itemList);
    }

    private boolean addItemsToItemList() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("SELECT * FROM item");
        ResultSet rst = stm.executeQuery();
        while (rst.next()){
            itemList.add(new Item(
                    rst.getString("itemCode"),
                    rst.getString("description"),
                    rst.getString("packSize"),
                    rst.getBigDecimal("unitPrice").doubleValue(),
                    rst.getInt("qtyOnHand")
            ));
        }
        return !itemList.isEmpty();
    }
}
