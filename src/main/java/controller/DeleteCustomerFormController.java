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
import model.Customer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DeleteCustomerFormController implements Initializable {
    ObservableList<Customer> customerList = FXCollections.observableArrayList();
    @Setter
    private Stage myStage;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnFind;

    @FXML
    private JFXComboBox<String> cmbCustomerType;

    @FXML
    private TableColumn<?, ?> colCustomerAddress;

    @FXML
    private TableColumn<?, ?> colCustomerCity;

    @FXML
    private TableColumn<?, ?> colCustomerDOB;

    @FXML
    private TableColumn<?, ?> colCustomerID;

    @FXML
    private TableColumn<?, ?> colCustomerName;

    @FXML
    private TableColumn<?, ?> colCustomerPostalCode;

    @FXML
    private TableColumn<?, ?> colCustomerProvince;

    @FXML
    private TableColumn<?, ?> colCustomerSalary;

    @FXML
    private TableColumn<?, ?> colCustomerTitle;

    @FXML
    private Label lblSearchingType;

    @FXML
    private TableView<Customer> tblCustomerDetails;

    @FXML
    private JFXTextField txtSearchText;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if (myStage != null) {
            myStage.close();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Customer selectedCustomer = tblCustomerDetails.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedCustomer.getId() + " : " + selectedCustomer.getName() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    if (deleteCustomer(selectedCustomer)) {
                        tblCustomerDetails.getItems().remove(selectedCustomer);
                        new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully !").show();
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Delete failed !").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    throw new IllegalStateException("Database connection or driver failure", e);
                }
            } else {
                new Alert(Alert.AlertType.INFORMATION, "User canceled the deletion !").show();
            }
        });
    }

    private boolean deleteCustomer(Customer selectedCustomer) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement stm = connection.prepareStatement("DELETE FROM customer WHERE id=?")) {
            stm.setObject(1, selectedCustomer.getId());
            return stm.executeUpdate() > 0;
        }

    }

    @FXML
    void btnFindOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearchTextOnAction(event);
    }

    @FXML
    void cmbCustomerTypeOnAction(ActionEvent event) {
        String selectedType = cmbCustomerType.getValue();
        if (selectedType != null) {
            updateLabelSearchingType(selectedType);
            txtSearchText.setDisable(false);
        }
    }

    private void updateLabelSearchingType(String selectedType) {
        lblSearchingType.setText("Enter the customer " + selectedType + " : ");
    }


    @FXML
    void txtSearchTextOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        customerList.clear();
        String searchText = txtSearchText.getText();
        String selectedSearchingType = cmbCustomerType.getValue().toLowerCase();
        if (searchText.isEmpty()) {
            showWarning("Enter the customer " + selectedSearchingType + " to find a customer !", cmbCustomerType);
            return;
        }
        if (selectedSearchingType.equals("id") && !validID(searchText)) {
            showWarning("Invalid ID...Enter in this format 'C####' ! ", txtSearchText);
        }
        searchText = selectedSearchingType.equals("name") ? "%" + searchText + "%" : searchText;
        if (findTheCustomerByIDOrName(searchText, selectedSearchingType)) {
            loadCustomersToTheTable();
        } else {
            String message = selectedSearchingType.equals("id") ? "No customer in this id !" : "No customer in this name !";
            showWarning(message, txtSearchText);
        }
    }

    private void loadCustomersToTheTable() {
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCustomerDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colCustomerSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colCustomerCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colCustomerProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colCustomerPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        tblCustomerDetails.setItems(customerList);
    }

    private boolean findTheCustomerByIDOrName(String searchedText, String searchedType) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = (searchedType.equals("id")) ? "SELECT id,title, name, dob, salary, address, city, province, postalCode FROM customer WHERE id=?" : "SELECT id,title, name, dob, salary, address, city, province, postalCode FROM customer WHERE name LIKE?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, searchedText);
            try (ResultSet rst = stm.executeQuery()) {
                while (rst.next()) {
                    customerList.add(new Customer(
                            rst.getString("id"),
                            rst.getString("title"),
                            rst.getString("name"),
                            rst.getDate("dob").toString(),
                            rst.getBigDecimal("salary").doubleValue(),
                            rst.getString("address"),
                            rst.getString("city"),
                            rst.getString("province"),
                            rst.getString("postalCode")
                    ));
                }
            }
        }
        return !customerList.isEmpty();
    }

    private boolean validID(String customerID) {
        // Regex breakdown:
        // ^      : Start of string
        // [Cc]   : Must start with 'C' or 'c'
        // \\d{4} : Must be followed by exactly 4 digits (0-9)
        // $      : End of string
        return customerID.matches("^[Cc]\\d{4}$");
    }

    private void showWarning(String message, Control field) {
        new Alert(Alert.AlertType.WARNING, message).show();
        field.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //disable the input text until user selects a searching type
        txtSearchText.setDisable(true);
        //disable the delete button until a correct record is found
        btnDelete.setDisable(true);
        cmbCustomerType.getItems().addAll("ID", "Name");

        //enable the delete button when user click the correct customer to be deleted
        tblCustomerDetails.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selectedCustomer) -> {
                    if (selectedCustomer != null) {
                        //set the delete button on
                        btnDelete.setDisable(false);
                    }
                });
    }
}
