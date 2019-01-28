package Analyser.Main;

import Analyser.Glue.SQL;
import Analyser.Glue.SQLTypes;
import Analyser.Graph.GraphController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class AnalyserController {
    @FXML public Label user_name;
    @FXML public ListView chatlist;
    @FXML public SQL sql;

    @FXML public void initialize(){
    }

    /**
     * Loads and populats the list of chats given a skype username
     */
    public void load(String username) {
        try {
            sql = new SQL(username);
            user_name.setText(username);
            loadRecentChats();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Populates the list of chats
     */
    private void loadRecentChats() {
        try {
            final ArrayList<SQLTypes.Chat> chats = sql.getChats();
            final ObservableList items = chatlist.getItems();
            items.clear();
            for (SQLTypes.Chat chat : chats) {
                items.add(new ChatEntry(chat.id, chat.name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // TODO: Filtering by (messages/time), (username), (display name)

    // TODO: Messages by user vs time (const chat)
    // TODO: Messages by chat vs time (const user)
    // TODO: Messages by user vs chat (const time) (heatmap) (?)

    public void listSelect(final Event e){ /*Do nothing*/}
    public void quickTimeline(final ActionEvent e) {
        final AnalyserController self = this;
        final GraphController controller;
        final ChatEntry chat = (ChatEntry)chatlist.getSelectionModel().getSelectedItem();
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Analyser/Graph/Graph.fxml"));
            Parent root = loader.load();
            stage.setTitle("Graph");
            stage.setScene(new Scene(root));
            stage.show();
            controller = loader.getController();
            controller.sql = sql;
            controller.draw(new GraphController.Settings(chat.id));
        } catch (IOException e1) { e1.printStackTrace(); }
    }

    public void customTimeline(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Analyser/Custom/AxisSelect.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e1) { e1.printStackTrace(); }

    }

    class ChatEntry{
        int id;
        String name;
        public ChatEntry(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String toString() {
            return this.name;
        }
    }

}
