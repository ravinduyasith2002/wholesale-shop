package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewCustomerMainController {
    private Stage myStage;
    public void setMyStage(Stage stage){
        this.myStage = stage;
    }

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnViewAllCustomers;

    @FXML
    private JFXButton btnViewSelectedCustomer;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage != null){
            myStage.close();
        }
    }

    private Stage viewAllCustomersStage;
    @FXML
    void btnViewAllCustomersOnAction(ActionEvent event) {
        try {
            if (viewAllCustomersStage != null && viewAllCustomersStage.isShowing()) {
                viewAllCustomersStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_all_customers_form.fxml"));
            Parent root = loader.load();
            ViewAllCustomersFormController controller = loader.getController();
            viewAllCustomersStage = new Stage();
            viewAllCustomersStage.setScene(new Scene(root));
            controller.setMyStage(viewAllCustomersStage);
            viewAllCustomersStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Stage viewSelectedCustomerStage;
    @FXML
    void btnViewSelectedCustomerOnAction(ActionEvent event) {
        try {
            if (viewSelectedCustomerStage != null && viewSelectedCustomerStage.isShowing()) {
                viewSelectedCustomerStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_selected_customer_form.fxml"));
            Parent root = loader.load();
            ViewSelectedCustomerFormController controller = loader.getController();
            viewSelectedCustomerStage = new Stage();
            viewSelectedCustomerStage.setScene(new Scene(root));
            controller.setMyStage(viewSelectedCustomerStage);
            viewSelectedCustomerStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
