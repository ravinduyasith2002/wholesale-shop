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


public class ItemMainMenuController {
@Setter
private Stage myStage;
    @FXML
    private JFXButton btnAddItem;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnDeleteItem;

    @FXML
    private JFXButton btnUpdateItem;

    @FXML
    private JFXButton btnViewItem;
    private Stage addItemStage;
    @FXML
    void btnAddItemOnAction(ActionEvent event) throws IOException {
        if(addItemStage != null && addItemStage.isShowing()) {
            addItemStage.toFront();
            return;
        }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_item_form.fxml"));
            Parent root = loader.load();
            AddItemFormController controller = loader.getController();
            addItemStage = new Stage();
            addItemStage.setScene(new Scene(root));
            controller.setMyStage(addItemStage);
            addItemStage.show();


    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        if(myStage!=null){
            myStage.close();
        }
    }

    Stage deleteItemStage;
    @FXML
    void btnDeleteItemOnAction(ActionEvent event) {
        try{
            if(deleteItemStage!=null && deleteItemStage.isShowing()){
                deleteItemStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/delete_item_form.fxml"));
            Parent root = loader.load();
            DeleteItemFormController controller = loader.getController();
            deleteItemStage = new Stage();
            deleteItemStage.setScene(new Scene(root));
            controller.setMyStage(deleteItemStage);
            deleteItemStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Stage updateItemStage;
    @FXML
    void btnUpdateItemOnAction(ActionEvent event) {
        try{
            if(updateItemStage != null && updateItemStage.isShowing()){
                updateItemStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update_item_form.fxml"));
            Parent root = loader.load();
            UpdateItemFormController controller = loader.getController();
            updateItemStage = new Stage();
            updateItemStage.setScene(new Scene(root));
            controller.setMyStage(updateItemStage);
            updateItemStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    Stage viewItemMainStage;
    @FXML
    void btnViewItemOnAction(ActionEvent event) {
        try {
            if (viewItemMainStage != null && viewItemMainStage.isShowing()) {
                viewItemMainStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_item_main_form.fxml"));
            Parent root = loader.load();
            ViewItemMainFormController controller = loader.getController();
            viewItemMainStage = new Stage();
            viewItemMainStage.setScene(new Scene(root));
            controller.setMyStage(viewItemMainStage);
            viewItemMainStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
