package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

@Setter
public class ViewItemMainFormController {
    Stage myStage;
    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnViewAllItems;

    @FXML
    private JFXButton btnViewSelectedItem;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage!=null){
            myStage.close();
        }
    }
    private Stage viewAllItemsStage;
    @FXML
    void btnViewAllItemsOnAction(ActionEvent event) {
        try{
            if(viewAllItemsStage !=null && viewAllItemsStage.isShowing()){
                viewAllItemsStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_all_items_form.fxml"));
            Parent root = loader.load();
            ViewAllItemsFormController controller = loader.getController();
            viewAllItemsStage = new Stage();
            viewAllItemsStage.setScene(new Scene(root));
            controller.setMyStage(viewAllItemsStage);
            viewAllItemsStage.show();
        }catch (IOException e){e.printStackTrace();}
    }

    Stage viewSelecedItemStage;
    @FXML
    void btnViewSelectedItemOnAction(ActionEvent event) {
        try{
            if(viewSelecedItemStage != null && viewSelecedItemStage.isShowing()){
                viewSelecedItemStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_selected_item_form.fxml"));
            Parent root = loader.load();
            ViewSelectedItemFormController controller = loader.getController();
            viewSelecedItemStage = new Stage();
            viewSelecedItemStage.setScene(new Scene(root));
            controller.setMyStage(viewSelecedItemStage);
            viewSelecedItemStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

}
