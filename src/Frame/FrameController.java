package Frame;

import Analyser.Main.AnalyserController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class FrameController {

    @FXML public Button leftButton;
    @FXML public Button rightButton;
    @FXML public Label title;
    @FXML public Label text;
          public static FrameController running;

    @FXML
    private BorderPane border_pane;
    @FXML void initialize() { // This method is called by the FXMLLoader when initialization is complete
        loadToCenter("/Loader/AccountList.fxml");
        running = this;
    }

    public void onBackButton(ActionEvent actionEvent) {
        loadToCenter("/Main_Menu/Welcome.fxml");
    }

    public void onRightButton(ActionEvent actionEvent){ }

    public void onExit(){
        Platform.exit();
    }

    public void onClickLoad(){
        loadToCenter("/Loader/AccountList.fxml");
    }
    public void onUserSelect(String username){
        final AnalyserController controller = (AnalyserController) loadToCenter("/Analyser/Main/Analyser.fxml");
        if (controller != null) {
            controller.load(username);
        }
    }

    private Object loadToCenter(String layout) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(layout));
            border_pane.setCenter((Parent)loader.load());
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Failed to load substage");
            e.printStackTrace();
            return null;
        }
    }
}
