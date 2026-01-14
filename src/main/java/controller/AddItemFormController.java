package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
public class AddItemFormController implements Initializable {
    private Stage myStage;

    @FXML
    private JFXButton btnAddItem;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtPackSize;

    @FXML
    private JFXTextField txtQtyOnHand;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    void txtDescriptionOnAction(ActionEvent event) {
        txtPackSize.requestFocus();
    }

    @FXML
    void txtPackSizeOnAction(ActionEvent event) {
        txtUnitPrice.requestFocus();
    }

    @FXML
    void txtUnitPriceOnAction(ActionEvent event) {
        txtQtyOnHand.requestFocus();
    }

    @FXML
    void btnAddItemOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
//will add
        String itemCode = txtItemCode.getText();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();
        Double unitPrice;
        try {
            unitPrice = Double.parseDouble(txtUnitPrice.getText());
        } catch (NumberFormatException _) {
            new Alert(Alert.AlertType.WARNING, "Unit price must be a valid number !").show();
            return;
        }
        Integer qtyOnHand;
        try {
            qtyOnHand = Integer.parseInt(txtUnitPrice.getText());
        } catch (NumberFormatException _) {
            new Alert(Alert.AlertType.WARNING, "Quntity must be a valid number !").show();
            return;
        }
        //Gudard clauses
        if (description.isEmpty() || description.length() > 20) {
            showWarning("Description cannot be empty and must be less than 30 letters !", txtDescription);
            return;
        }
        if (packSize.isEmpty() || description.length() > 20) {
            showWarning("Pack size cannot be empty and must be less than 20 letters !", txtPackSize);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Add this item?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().get() == ButtonType.YES) {
            Item item = new Item(itemCode, description, packSize, unitPrice, qtyOnHand);
            if (addNewItem(item)) {
                new Alert(Alert.AlertType.INFORMATION, "New item added successfully !").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Add failed !").show();
            }
        }


    }

    private boolean addNewItem(Item item) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement stm = connection.prepareStatement("INSERT INTO item VALUES(?,?,?,?,?)")) {
            stm.setString(1, item.getItemCode());
            stm.setString(2, item.getDescription());
            stm.setString(3, item.getPackSize());
            stm.setDouble(4, item.getUnitPrice());
            stm.setInt(5, item.getQtyOnHand());
            return stm.executeUpdate() > 0;
        }
    }

    private void showWarning(String message, JFXTextField field) {
        new Alert(Alert.AlertType.WARNING, message).show();
        field.requestFocus();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if (myStage != null) {
            myStage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadNewItemId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        txtDescription.requestFocus();
    }

    private void loadNewItemId() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement stm = connection.prepareStatement("SELECT itemCode FROM item ORDER BY itemCode DESC LIMIT 1")) {
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String lastId = rst.getString("itemCode");
                int lastIdNumber = Integer.parseInt((lastId.substring(1)));
                txtItemCode.setText(String.format("I%04d", lastIdNumber + 1));
            } else {
                txtItemCode.setText("I0001");
            }
        }

    }

}
