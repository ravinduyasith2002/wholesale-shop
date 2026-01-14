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
import model.Customer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UpdateCustomerFormController implements Initializable {
    ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Stage myStage;
    public void setMyStage(Stage stage){
        this.myStage = stage;
    }
    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnFind;

    @FXML
    private JFXButton btnUpdate;

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
    private DatePicker dateDOB;

    @FXML
    private JFXComboBox<String> cmbProvince;

    @FXML
    private JFXComboBox<String> cmbTitle;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtCity;

    @FXML
    private JFXTextField txtID;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPostalCode;

    @FXML
    private JFXTextField txtSalary;


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
    void btnUpdateOnAction(ActionEvent event)  {
        try{
         String id = txtID.getText();
         String name = txtName.getText();
         String title = cmbTitle.getValue();
         String address = txtAddress.getText();
         LocalDate dob = dateDOB.getValue();
         String dobString = formatDateDOB(dob);
         String city = txtCity.getText();
         String province = cmbProvince.getValue();
         String postalCode = txtPostalCode.getText();

        //validate salary
        double salary;
        try{
            salary = Double.parseDouble(txtSalary.getText());
        }catch (NumberFormatException _){
            new Alert(Alert.AlertType.WARNING,"Salary must be a valid number !").show();
            return;
        }

        //Guard clauses(validation)
            if (name.isEmpty() || name.length() > 30) {
                showWarning("Name cannot be empty and must be less than 30 letters !",txtName);
                return;
            }
            if(address.isEmpty() || address.length() > 40){
                showWarning("Address cannot be empty and must be less than 40 letters !", txtAddress);
                return;
            }
            if(salary<=0){
                showWarning("Salary must be grater than 0 !",txtSalary);
                return;
            }
            if(city.isEmpty() || city.length() > 20){
                showWarning("City cannot be empty and must be less than 20 letters !",txtCity);
                return;
            }
            if(postalCode.isEmpty() || postalCode.length()>5){
                showWarning("Postal code cannot be empty and must be less than 5 numbers !", txtPostalCode);
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Update this customer?", ButtonType.YES,ButtonType.NO);
            if(alert.showAndWait().get() == ButtonType.YES){
                Customer customer = new Customer(id,title,name,dobString,salary,address,city,province,postalCode);
                if(updateCustomer(customer)){
                    new Alert(Alert.AlertType.INFORMATION,"Customer updated successfully !").show();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Update failed !").show();
                }
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();

        }
    }

    private String formatDateDOB(LocalDate dob){
        if(dob != null){
            return dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return "";
    }

    //helper method to reduce repeated code
    private void showWarning(String message, TextField field){
        new Alert(Alert.AlertType.WARNING,message).show();
        field.requestFocus();
    }

    private boolean updateCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("UPDATE customer SET title=?,name=?,dob=?,salary=?,address=?,city=?,province=?,postalCode=? WHERE id=?");
        stm.setObject(1,customer.getTitle());
        stm.setObject(2, customer.getName());
        stm.setObject(3, customer.getDob());
        stm.setObject(4, customer.getSalary());
        stm.setObject(5, customer.getAddress());
        stm.setObject(6, customer.getCity());
        stm.setObject(7,customer.getProvince());
        stm.setObject(8, customer.getPostalCode());
        stm.setObject(9, customer.getId());
        return stm.executeUpdate()>0;

    }

    @FXML
    void txtSearchTextOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        customerList.clear();
        String searchtext = txtSearchText.getText();
        String selectedSearchingType = cmbCustomerType.getValue();
        if(selectedSearchingType == null){
            new Alert(Alert.AlertType.ERROR, "Select the searching type first !").showAndWait();
            cmbCustomerType.requestFocus();
        }else{
            if(searchtext.isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Enter the customer "+selectedSearchingType.toLowerCase()+" !").showAndWait();
                txtSearchText.requestFocus();
            }else{
                //selected the search type and the search field is not empty
                if(selectedSearchingType.toLowerCase().equals("id")){
                    //if id selected
                    boolean isValidID = checkForValidID(searchtext);
                    if(isValidID){
                        Customer customer = findTheCustomerByCustomerID(searchtext);
                        if(customer==null){
                            new Alert(Alert.AlertType.ERROR, "Sorry, No Customer in this ID ! ").showAndWait();
                            txtSearchText.requestFocus();
                        }else{
                            //if customer exists
                            customerList.add(customer);
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
                    }else{
                        new Alert(Alert.AlertType.ERROR, "Invalid id...Enter in this format 'C####' ! ").showAndWait();
                    }
                }else{
                    //if name is selected
                    customerList.clear();
                    boolean isCustomerExist = findCustomerByCustomerName(searchtext);
                    if(!isCustomerExist){
                        //if no customer in this name
                        new Alert(Alert.AlertType.ERROR, "Sorry, No customer in this name !").showAndWait();
                    }else{
                        //if customer exist in that name
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
                }
            }
        }

    }

    private boolean findCustomerByCustomerName(String searchtext) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String SQL = "SELECT * FROM customer WHERE name LIKE ?";
        String searchPattern = "%"+searchtext+"%";
        PreparedStatement stm = connection.prepareStatement(SQL);
        stm.setString(1,searchPattern);
        ResultSet rst = stm.executeQuery();
        while(rst.next()){
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
        if(customerList.isEmpty()){
            return false;
        }
        return true;

    }

    private Customer findTheCustomerByCustomerID(String customerID) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String SQL = "SELECT * FROM customer WHERE id=?";
        PreparedStatement stm = connection.prepareStatement(SQL);
        stm.setString(1,customerID);
        ResultSet rst = stm.executeQuery();
        if(rst.next()){
            return new Customer(
                    rst.getString("id"),
                    rst.getString("title"),
                    rst.getString("name"),
                    rst.getDate("dob").toString(),
                    rst.getBigDecimal("salary").doubleValue(),
                    rst.getString("address"),
                    rst.getString("city"),
                    rst.getString("province"),
                    rst.getString("postalCode")
            );
        }
        return null;
    }

    private boolean checkForValidID(String searchtext) {
        if(searchtext.length() != 5 || searchtext.toLowerCase().charAt(0) != 'c'){
            return false;
        }
        return true;
    }

    public void cmbCustomerTypeOnAction(ActionEvent actionEvent) {
        String selectedValue = cmbCustomerType.getValue();
        if(selectedValue!=null){
            updateLblSearchingType(selectedValue);
        }
    }

    private void updateLblSearchingType(String selectedValue) {
        lblSearchingType.setText("Enter the customer "+selectedValue+" : ");
    }

    //define the text fields and combo boxes as lists
    List<Control> fieldsAndCombos = Arrays.asList(txtName,txtAddress,txtSalary,txtCity,txtPostalCode,cmbTitle,cmbProvince);

    //switching the intput fields
    private void setFieldsEnabled(boolean enabled){
        List<Control> fields = Arrays.asList(txtName,txtAddress,txtSalary,txtCity,txtPostalCode,cmbTitle,cmbProvince,dateDOB);
        fields.forEach(field -> {
            field.setDisable(!enabled);
            if(enabled){
                field.setStyle("-fx-opacity: 1.0;");
            }else{
                field.setStyle("-fx-opacity: 0.5;");
            }
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the DOB non editable by custom texts
        dateDOB.setEditable(false);
        //set the customer title non editable by custom texts
        cmbTitle.setEditable(false);
        //set the province non editable by custom texts
        cmbProvince.setEditable(false);
        //set the update button to off until a correct customer details loaded
        btnUpdate.setDisable(true);
        //set id field disable throughout the whole programme
        txtID.setDisable(true);
        //set the rest of the editable fields to off at the begining
        setFieldsEnabled(false);
        //initializing the values in the combo boxes
        cmbCustomerType.getItems().addAll("ID","Name");
        cmbTitle.getItems().addAll("Ms","Mrs","Miss");
        cmbProvince.getItems().addAll(
                "Central",
                "Eastern",
                "North Central",
                "North Western",
                "Northern",
                "Sabaragamuwa",
                "Southern",
                "Uva",
                "Western"
        );

        //load selected raw into fields
        tblCustomerDetails.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selectedCustomer) -> {
                    if (selectedCustomer != null) {
                        //set the update button on
                        btnUpdate.setDisable(false);
                        //set the input fields active
                        setFieldsEnabled(true);

                        //fill the fields with customer details
                        txtID.setText(selectedCustomer.getId());
                        txtName.setText(selectedCustomer.getName());
                        txtAddress.setText(selectedCustomer.getAddress());
                        txtCity.setText(selectedCustomer.getCity());
                        cmbProvince.setValue(selectedCustomer.getProvince());
                        dateDOB.setValue(LocalDate.parse(selectedCustomer.getDob()));
                        txtPostalCode.setText(selectedCustomer.getPostalCode());
                        txtSalary.setText(String.valueOf(selectedCustomer.getSalary()));
                        cmbTitle.setValue(selectedCustomer.getTitle().trim());

                    }else{
                        setFieldsEnabled(false);
                    }
                });

    }

}
