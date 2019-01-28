package Analyser.Custom;

import Analyser.Chart.Chart;
import Analyser.Chart.ChartMaker;
import Analyser.Chart.IChartType;
import Analyser.Glue.SQL;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;

public class CustomController {
    public Label user_name;
    public ListView filterList;
    public ChoiceBox XSelect;
    public ChoiceBox YSelect;

    public void initialize(){

    }

    public void next(ActionEvent actionEvent) throws SQLException {
        final ChartMaker cm = new ChartMaker(IChartType.Axis.user, IChartType.Axis.time, 245);
        cm.groupChats(new Integer[]{56, 57, 61}, "TeamOkay");
        final Chart chart = cm.make(new SQL("mart3323est"));

        Group root = new Group();
        Canvas canvas = new Canvas(600,400);

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        root.getChildren().add(canvas);
        stage.show();

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Pair<Double, Double> m : chart.pairs) {
            final Double k = m.getKey()*10.0;
            final Double v = m.getValue()*600.0;
            gc.strokeLine(k - 2, v, k + 2, v);
        }
    }

    public static class Filter{
        enum Type { Show, Group, Hide }
        Type type;
        String scope;
        public Filter(String scope, Type type) {
            this.scope = scope;
            this.type = type;
        }

        @Override
        public String toString() {
            return ""+scope+"  -  "+type;
        }
    }
}
