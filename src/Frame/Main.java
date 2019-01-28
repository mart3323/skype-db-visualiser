package Frame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Frame.fxml"));
        primaryStage.setTitle("Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        /*
        for (int size = 10; size < 10000; size*=10) {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                strings.add("exampleVariable"+i);
            }
            long average = 0;
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000*1000; i++) {
                int randomNumber = (int) Math.floor(Math.random() * size);
                average += randomNumber;
                strings.indexOf("exampleVariable"+ randomNumber);
            }
            average /= 1000*1000;
            long end = System.currentTimeMillis();
            long time_spent = end-start;
            System.out.println("Ran "+1000+" indexOf calls on an array of size "+size);
            System.out.println("   "+average+" was the average position of each entry searched for");
            System.out.println("   Total time (including generating random nunmbers) was "+(time_spent)+" milliseconds");
        }
        */
        //launch(args);
    }
}

