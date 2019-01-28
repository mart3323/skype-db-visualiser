package Analyser.Graph;

import Analyser.Glue.SQL;
import Analyser.Glue.SQLTypes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GraphController {
    public ProgressIndicator prog;
    public VBox pane;
    public SQL sql;
    public HBox names;
    public VBox root;


    @FXML
    public void initialize(){
        prog.setProgress(-1);
    }

    public void draw(final Settings settings){
        final GraphController self = this;
        new Thread(() -> {
            self.updateProgress(0.2);
            try {

                final ArrayList<SQLTypes.Message> messages = sql.getMessages(settings.chatID);
                self.updateProgress(0.4);

                // Get min and max, give sequential ID's to names
                int maxHeight = Integer.MIN_VALUE;
                int minHeight = Integer.MAX_VALUE;
                HashMap<String, Integer> names = new HashMap<>();
                for (SQLTypes.Message msg : messages) {
                    if(!names.containsKey(msg.author)) names.put(msg.author, names.size());
                    maxHeight = Math.max(maxHeight, msg.timestamp);
                    minHeight = Math.min(minHeight, msg.timestamp);
                }

                // prepare canvas space
                final Rectangle2D size = Screen.getPrimary().getVisualBounds();
                final int canvasHeight = (int)size.getHeight()-200;
                final int canvasWidth = (int)size.getWidth()-40;
                final int nameSpacing = 25;//(int) (size.getWidth() - 100) / names.size();

                Stack<Pair<Integer, Integer>> cl = new Stack<>();
                for (SQLTypes.Message message : messages) {
                    final int YRange = maxHeight - minHeight;
                    final int deltaY = message.timestamp - minHeight;
                    final double relPos = 1 - ((double) deltaY) / ((double) YRange);
                    cl.push(new Pair<>(
                        names.get(message.author)*nameSpacing,
                        (int)(canvasHeight* relPos)
                    ));
                }

                final Canvas canvas = new Canvas(nameSpacing*names.size(), canvasHeight);
                final GraphicsContext gc = canvas.getGraphicsContext2D();


                // Draw on canvas
                self.updateProgress(0.6);
                final Stack<Pair<Integer, Integer>> canvasLocations = cl;
                Platform.runLater(() -> {
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);
                    // Draw message lines
                    for (Pair<Integer, Integer> loc : canvasLocations) {
                        final int X = loc.getKey();
                        final int Y = loc.getValue();
                        gc.strokeLine(X, Y+0.5, X+10, Y+0.5);
                    }
                    // Prepare names
                    for (int j = 0; j < names.size(); j++) {
                        final Label label = new Label("LOADING");
                        label.getStyleClass().addAll("graph","name");
                        label.setMinHeight(nameSpacing);
                        label.setMaxHeight(nameSpacing);
                        self.names.getChildren().add(new Group(label));
                    }
                    // Specify names
                    for (Map.Entry<String, Integer> entry : names.entrySet()) {
                        final String name = entry.getKey();
                        final Integer pos = entry.getValue();
                        ((Label) ((Group) self.names.getChildren().get(pos)).getChildren().get(0)).setText(name);
                    }
                    pane.getChildren().set(1, canvas);
                    {
                        final Scene scene = root.getScene();
                        final Stage stage = (Stage) scene.getWindow();
                        stage.setFullScreen(true);
                        scene.setOnKeyPressed(e -> {
                            if (e.getCode() == KeyCode.ESCAPE) {stage.close();}
                        });
                    }
                });

            } catch (SQLException e) {
                self.prog.resize(0,0);
                e.printStackTrace();
            }
        }).start();

    }

    private void updateProgress(final double progress) {
        Platform.runLater(() -> prog.setProgress(progress));
    }

    public static class Settings {
        int chatID;
        public Settings(int chatID) {
            this.chatID = chatID;
        }
    }
}
