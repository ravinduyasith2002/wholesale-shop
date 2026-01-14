package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class CustomerMainMenuController {

    @FXML
    private JFXButton btnAddCustomer;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnDeleteCustomer;

    @FXML
    private JFXButton btnUpdateCustomer;

    @FXML
    private JFXButton btnViewCustomers;
    private Stage myStage;
    public void setMyStage(Stage stage){
        this.myStage = stage;
    }
    private Stage addCustomerStage;
    @FXML
    void btnAddCustomerOnAction(ActionEvent event) {
        if(addCustomerStage != null && addCustomerStage.isShowing()){
            addCustomerStage.toFront();
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_customer_form.fxml"));
            Parent root = loader.load();
            AddCustomerFormController controller = loader.getController();
            addCustomerStage = new Stage();
            addCustomerStage.setScene(new Scene(root));
            controller.setMyStage(addCustomerStage);
            addCustomerStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        myStage.close();
    }

    private Stage deleteCustomerStage;
    @FXML
    void btnDeleteCustomerOnAction(ActionEvent event) {
        try {
            if (deleteCustomerStage != null && deleteCustomerStage.isShowing()) {
                deleteCustomerStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/delete_customer_form.fxml"));
            Parent root = loader.load();
            DeleteCustomerFormController controller = loader.getController();
            deleteCustomerStage = new Stage();
            deleteCustomerStage.setScene(new Scene(root));
            controller.setMyStage(deleteCustomerStage);
            deleteCustomerStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private Stage updateCustomerStage;
    @FXML
    void btnUpdateCustomerOnaAction(ActionEvent event) {
        try {
            if (updateCustomerStage != null && updateCustomerStage.isShowing()) {
                updateCustomerStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update_customer.fxml"));
            Parent root = loader.load();
            UpdateCustomerFormController controller = loader.getController();
            updateCustomerStage = new Stage();
            updateCustomerStage.setScene(new Scene(root));
            controller.setMyStage(updateCustomerStage);
            updateCustomerStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private Stage viewCustomerMainStage;
    @FXML
    void btnViewCustomersOnAction(ActionEvent event) throws IOException {
        if(viewCustomerMainStage != null && viewCustomerMainStage.isShowing()){
            viewCustomerMainStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_customer_main.fxml"));
            Parent root = loader.load();
            ViewCustomerMainController controller = loader.getController();
            viewCustomerMainStage = new Stage();
            viewCustomerMainStage.setScene(new Scene(root));
            controller.setMyStage(viewCustomerMainStage);
            viewCustomerMainStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
