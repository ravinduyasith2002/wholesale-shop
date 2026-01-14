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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewSelectedCustomerFormController implements Initializable {
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
    private JFXComboBox<String> cmbCustomerType;

    @FXML
    private TableView<Customer> tblCustomerDetails;


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
    private JFXTextField txtSearchText;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage != null){
            myStage.close();
        }
    }

    @FXML
    void btnFindOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearchTextOnAction(event);
    }

    private Customer findTheCustomerByID(String customerID) throws SQLException, ClassNotFoundException {
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
                    rst.getString("postalCode"));
        }
        return null;
    }

    private boolean checkForValidID(String searchText) {
        if(searchText.length() != 5 || searchText.toLowerCase().charAt(0) != 'c'){
            return false;
        }
        return true;
    }

    @FXML
    void txtSearchTextOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        customerList.clear();
        String searchText = txtSearchText.getText();
        String selectedSearchingType = cmbCustomerType.getValue();
        if(selectedSearchingType == null){
            new Alert(Alert.AlertType.ERROR, "Select the searching type first !").showAndWait();
            cmbCustomerType.requestFocus();

        }else{
            if(searchText.isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Enter the customer "+selectedSearchingType.toLowerCase()+" !").showAndWait();
                txtSearchText.requestFocus();
            }else{
                //selected the search type and text field is not empty
                if(selectedSearchingType.toLowerCase().equals("id")){
                    //if id selected
                    boolean isValidID = checkForValidID(searchText);
                    if(isValidID){
                        //if id is valid
                        Customer customer = findTheCustomerByID(searchText);
                        if(customer == null){
                            //if customer is not in the database
                            new Alert(Alert.AlertType.ERROR, "Sorry, No Customer in this ID ! ").showAndWait();
                            txtSearchText.requestFocus();
                        }else {
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
                }else {
                    //if name selected
                    customerList.clear();
                    boolean isCustomerExist = findTheCustomerByName(searchText);
                    if(!isCustomerExist){
                        //if no customer in this name
                        new Alert(Alert.AlertType.ERROR, "Sorry, No customer in this name !").showAndWait();
                    }else{
                        //if customer exist on that name
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

    private boolean findTheCustomerByName(String searchText) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String SQL = "SELECT * FROM customer WHERE name LIKE ?";
        String searchPattern = "%"+searchText+"%";
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbCustomerType.getItems().addAll("ID","Name");
        //cmbCustomerType.getSelectionModel().selectFirst();

        //add the listener
//        cmbCustomerType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
//            if(newValue != null){
//                updateLblSearchingType(newValue);
//            }
//        });
    }
    @FXML
    void cmbCustomerTypeOnAction(ActionEvent event) {
        String selectedValue = cmbCustomerType.getValue();
        if(selectedValue != null){
            updateLblSearchingType(selectedValue);
        }
    }

    private void updateLblSearchingType(String selectedValue) {
        lblSearchingType.setText("Enter the customer "+selectedValue+" : ");
    }


}
