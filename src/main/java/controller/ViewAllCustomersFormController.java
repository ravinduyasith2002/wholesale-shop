package controller;

import com.jfoenix.controls.JFXButton;
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

public class ViewAllCustomersFormController implements Initializable {
    ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private Stage myStage;
    public void setMyStage(Stage stage){
        this.myStage = stage;
    }

    @FXML
    private JFXButton btnBack;

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
    void btnBackOnAction(ActionEvent event) {
        if(myStage != null){
            myStage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerList.clear();
        try {
            boolean isCustomersExist = addCustomerstoCustomerList();
            if(!isCustomersExist){
                new Alert(Alert.AlertType.ERROR, "Sorry, No Customers Added Yet !").showAndWait();
            }else{
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addCustomerstoCustomerList() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        String SQL = "SELECT * FROM customer";
        PreparedStatement stm = connection.prepareStatement(SQL);
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
}
