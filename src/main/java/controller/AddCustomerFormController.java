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
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import model.Customer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class AddCustomerFormController implements Initializable {


    @FXML
    private JFXButton btnAddCustomer;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXComboBox<String> cmbTitle;

    @FXML
    private DatePicker dpDOB;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtCity;

    @FXML
    private JFXTextField txtCustomerId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPostalCode;

    @FXML
    private JFXTextField txtProvince;

    @FXML
    private JFXTextField txtSalary;

    private Stage myStage;



    @FXML
    void btnAddCustomerOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        String id = txtCustomerId.getText();
        String title = cmbTitle.getSelectionModel().getSelectedItem();
        String name = txtName.getText();
        LocalDate dob = dpDOB.getValue();
        String dobString = (dob!=null) ? dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        String salaryString = txtSalary.getText();
        Double salary = salaryString.isBlank() ? -1.0 : Double.parseDouble(salaryString);
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String postlCode = txtPostalCode.getText();
        if(id.isBlank() || title==null || name.isBlank() || dobString.isBlank() || salary < 0 || address.isBlank() || city.isBlank() || postlCode.isBlank() || province.isBlank()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Make sure to fill all the fields correctly !");
            alert.showAndWait();
        }else {
            //
            System.out.println("+-----------------+----------------------------+");
            System.out.println("| Field           | Value                      |");
            System.out.println("+-----------------+----------------------------+");
            System.out.printf("| ID              | %-26s |%n", id);
            System.out.printf("| Title           | %-26s |%n", title);
            System.out.printf("| Name            | %-26s |%n", name);
            System.out.printf("| DOB             | %-26s |%n", dobString);
            System.out.printf("| Salary          | %-26.2f |%n", salary);
            System.out.printf("| Address         | %-26s |%n", address);
            System.out.printf("| City            | %-26s |%n", city);
            System.out.printf("| Province        | %-26s |%n", province);
            System.out.printf("| Postal Code     | %-26s |%n", postlCode);
            System.out.println("+-----------------+----------------------------+");
            //
            Customer customer = new Customer(id,title,name, dobString,salary,address,city,province,postlCode);
            boolean isCustomerAdded = addNewCustomer(customer);
            if(isCustomerAdded){
                Alert alertSuccessfullyAdded = new Alert(Alert.AlertType.INFORMATION);
                alertSuccessfullyAdded.setTitle("Success");
                alertSuccessfullyAdded.setHeaderText("Customer saved Successfully !");
                alertSuccessfullyAdded.setContentText("Customer " + name + " has been added to the system.");
                alertSuccessfullyAdded.showAndWait();
                txtCustomerId.setText("");
                cmbTitle.setValue(null);
                txtName.setText("");
                dpDOB.setValue(null);
                txtSalary.setText("");
                txtAddress.setText("");
                txtCity.setText("");
                txtProvince.setText("");
                txtPostalCode.setText("");
                loadNewCustomerId();

            }

        }
    }

    private boolean addNewCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO Customer VALUES(?,?,?,?,?,?,?,?,?)");
        stm.setObject(1,customer.getId());
        stm.setObject(2,customer.getTitle());
        stm.setObject(3,customer.getName());
        stm.setObject(4,customer.getDob());
        stm.setObject(5,customer.getSalary());
        stm.setObject(6,customer.getAddress());
        stm.setObject(7,customer.getCity());
        stm.setObject(8,customer.getProvince());
        stm.setObject(9,customer.getPostalCode());
        int res = stm.executeUpdate();
        return res>0;
    }

    public void setMyStage(Stage stage){
        this.myStage = stage;
    }
    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage != null){
            myStage.close();
        }
    }

    @FXML
    void txtAddressOnAction(ActionEvent event) {
        txtCity.requestFocus();
    }

    @FXML
    void txtCityOnAction(ActionEvent event) {
        txtProvince.requestFocus();
    }

    @FXML
    void txtPostalCodeOnAction(ActionEvent event) {

    }

    @FXML
    void txtProvinceOnAction(ActionEvent event) {
        txtPostalCode.requestFocus();
    }

    @FXML
    void txtSalaryOnAction(ActionEvent event) {
        txtAddress.requestFocus();
    }

    @FXML
    void txxtNameOnAction(ActionEvent event) {
        dpDOB.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTitleComboBox();
        try {
            loadNewCustomerId();
        } catch (SQLException e) {
            try {
                throw new RuntimeException(e);
            } catch (RuntimeException ex) {
                throw new RuntimeException(ex);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateTitleComboBox() {
        ObservableList<String> titles = FXCollections.observableArrayList(
                "Mr",
                "Ms",
                "Mrs",
                "Miss",
                "Dr",
                "Ven"
        );
        cmbTitle.setItems(titles);
    }
    //Load the new customer id when the form is loaded
    private void loadNewCustomerId() throws SQLException, ClassNotFoundException {
        String lastId = null;
        String SQL = "SELECT id from Customer ORDER BY id DESC LIMIT 1";
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement(SQL);
        ResultSet rst = stm.executeQuery();
        if(rst.next()){
            lastId = rst.getString("id");
            int lastIdNumber = Integer.parseInt((lastId.substring(1)));
            txtCustomerId.setText(String.format("C%04d",lastIdNumber+1));
        }else{
            txtCustomerId.setText("C0001");
        }
    }
}
