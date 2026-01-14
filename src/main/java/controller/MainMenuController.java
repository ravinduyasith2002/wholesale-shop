package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private JFXButton btnManageCustomer;

    @FXML
    private JFXButton btnManageItem;

    private Stage customerMainMenuStage;
    @FXML
    void btnManageCustomerOnAction(ActionEvent event) {
        if(customerMainMenuStage != null && customerMainMenuStage.isShowing()){
            customerMainMenuStage.toFront();
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer_main_menu.fxml"));
            Parent root = loader.load();
            CustomerMainMenuController controller = loader.getController();
            customerMainMenuStage = new Stage();
            customerMainMenuStage.setScene(new Scene(root));
            controller.setMyStage(customerMainMenuStage);
            customerMainMenuStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private Stage itemMainMenuStage;
    @FXML
    void btnManageItemOnAction(ActionEvent event) throws IOException {
        if(itemMainMenuStage != null && itemMainMenuStage.isShowing()){
            itemMainMenuStage.toFront();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/item_main_menu.fxml"));
        Parent root  = loader.load();
        ItemMainMenuController controller = loader.getController();
        itemMainMenuStage = new Stage();
        itemMainMenuStage.setScene(new Scene(root));
        controller.setMyStage(itemMainMenuStage);
        itemMainMenuStage.show();
    }

}
