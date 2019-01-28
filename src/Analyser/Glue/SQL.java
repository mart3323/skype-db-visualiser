package Analyser.Glue;

import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class SQL implements AutoCloseable{

    public Connection database;

    public SQL(String username) throws SQLException {
        database = JDBC.createConnection("jdbc:sqlite:skype_logs/" + username + ".db", new Properties());
    }

    /**
     * Returns an ArrayList of {@link SQLTypes.Chat} objects in order from most recent to least recent
     * @throws SQLException
     */
    public ArrayList<SQLTypes.Chat> getChats() throws SQLException {
        String statement = "SELECT COALESCE(c.displayname,c.meta_name), c.id " +
                           "FROM Conversations c " +
                           "WHERE c.type == 2 " + // Group anchor
                           "ORDER BY c.last_activity_timestamp DESC";
        final PreparedStatement query = database.prepareStatement(statement);
        final ResultSet result = query.executeQuery();
        ArrayList<SQLTypes.Chat> chats = new ArrayList<SQLTypes.Chat>();
        while(result.next()){
            final String chatName = result.getString(1);
            chats.add(new SQLTypes.Chat(chatName, result.getInt(2)));
        }
        return chats;
    }

    public ArrayList<SQLTypes.Message> getMessages(int chatID) throws SQLException {
        String statement = "SELECT m.author, m.timestamp, m.leavereason " +
                           "FROM Messages m " +
                           "WHERE m.convo_id == ?";
        System.out.println("chatID = " + chatID);
        final PreparedStatement query = database.prepareStatement(statement);
        query.setInt(1, chatID);
        final ResultSet result = query.executeQuery();
        ArrayList<SQLTypes.Message> messages = new ArrayList<SQLTypes.Message>();
        while(result.next()){
            final String author = result.getString(1);
            final int timestamp = result.getInt(2);
            final int leaveReason = result.getInt(3);
            messages.add(new SQLTypes.Message(author, timestamp, leaveReason));
        }
        System.out.println("messages.size() = " + messages.size());
        return messages;
    }
    @Override
    public void close() throws SQLException{
        database.close();
    }
}
