package Loader;

import Frame.FrameController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Account {

    private final String username;
    private final GridPane grid;
    private final int row;
    private final Button button;
    private final Label label;
    private final ProgressIndicator progress;
    private final Label name;
    private final Button select;

    public Account(String username, GridPane grid, int row) {
        this.username = username;
        this.grid = grid;
        this.row = row;

        select = new Button();
        button = new Button();
        label = new Label();
        progress = new ProgressIndicator();
        name = new Label();
        grid.add(select,0,row);
        grid.add(name,1,row);
        grid.add(label, 3, row);
        grid.add(button, 4, row);

        name.setText(username);
        button.setText("Update (Copy)");
        select.setText("Select");

        final Account self = this;
        select.setDisable(!(new File("skype_logs/"+username+".db").exists()));
        select.setOnMouseClicked(event -> self.select());
        button.setOnMouseClicked(event -> self.update());

        name.getStyleClass().add("name");
    }

    private void select() {
        final Account self = this;
        Platform.runLater(() -> FrameController.running.onUserSelect(self.username));
    }

    public Object update() {
        final Account self = this;
        new Thread(() -> {
                UI_startCopy();

                File file = new File(System.getenv("APPDATA")+"/Skype");
                final Path path1 = Paths.get(file.toPath() + "/" + self.username + "/main.db");
                final Path path2 = Paths.get("skype_logs/" + self.username + ".db");
                try {
                    Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING);

                    UI_Success();
                } catch (IOException e) {
                    UI_Failure();
                    e.printStackTrace();
                }
        }).start();
        return null;
    }

    private void UI_startCopy() {
        final Account self = this;
        Platform.runLater(() -> {
            self.grid.add(self.progress, 2, self.row);
            self.progress.setProgress(-1);
            self.label.setText("Copying database file");
            self.button.setDisable(true);
            self.select.setDisable(true);
        });
    }

    private void UI_Success() {
        final Account self = this;
        Platform.runLater(() -> {
            self.grid.getChildren().remove(self.progress);
            self.label.setText("Database file copied");
            self.button.setDisable(true);
            self.select.setDisable(false);
        });
    }

    private void UI_Failure() {
        final Account self = this;
        Platform.runLater(() -> {
            self.grid.getChildren().remove(self.progress);
            self.progress.setProgress(-1);
            self.label.setText("Failed to copy database file");
            self.button.setDisable(false);
            self.select.setDisable(true);
        });
    }

}
