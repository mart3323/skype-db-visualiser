package Analyser.Chart;

import Analyser.Chart.Types.ChatMessage;
import Analyser.Chart.Types.Message;
import Analyser.Glue.SQL;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChartMaker extends ChartType{
    public ChartMaker(Axis X, Axis Y, Object con) {
        this.x = X;
        this.y = Y;
        this.con = con;
    }

    public Chart make(SQL sql) throws SQLException {
        final ResultSet result = makeQuery(sql.database);
        final ArrayList<ChatMessage> results = collectResults(result);
        Stream<ChatMessage> stream = results.parallelStream();
        stream = applyFilters(stream);
        return getScaledMessages(stream);
    }

    /**
     * Builds a resultset based on the given constant and time range (if specified)
     * @param sql an SQL connection to the database
     */
    private ResultSet makeQuery(Connection sql) throws SQLException {
        final boolean chatConstant = this.x != Axis.chat && this.y != Axis.chat;
        final boolean userConstant = this.x != Axis.user && this.y != Axis.user;

        //TODO: Get proper user and chat names
        String statement = "SELECT cnt.displayname, m.author, m.timestamp, m.leavereason, m.convo_id, c.displayname " +
                "FROM Messages m "+
                "INNER JOIN Conversations c ON m.convo_id == c.id "+
                "INNER JOIN Contacts cnt ON m.author == cnt.skypename "+
                "WHERE 1==1 ";

        // Apply constant
        if (chatConstant) { statement += "AND m.convo_id == \"" + this.con + "\"";}
        else if (userConstant) { statement += "AND m.author == \"" + this.con + "\"";}

        // Apply time range
        if(this.timeRange != null && this.timeRange.length == 2){
            final int min = this.timeRange[0];
            final int max = this.timeRange[1];
            statement += "AND m.timestamp BETWEEN "+min+" AND "+max;
        }
        System.out.println(statement);
        try {Thread.sleep(1000);} catch (InterruptedException e) {}
        final PreparedStatement query = sql.prepareStatement(statement);
        return query.executeQuery();
    }

    /**
     * Converts a resultset into an ArrayList of messages
     */
    private ArrayList<ChatMessage> collectResults(ResultSet result) throws SQLException {
        // REFACTOR: Possible to create a stream from a ResultSet?
        ArrayList<ChatMessage> results = new ArrayList<>();
        while(result.next()){
            results.add(new ChatMessage(result.getString(1),
                                    result.getString(2),
                                    result.getInt(3),
                                    result.getInt(4),
                                    result.getInt(5),
                                    result.getString(6)
            ));
        }
        return results;
    }

    static class MappingData {
        HashMap<String, Integer> nameMap;
        HashMap<Integer, Integer> chatMap;
        private final int minTime;
        private final int maxTime;

        public MappingData(HashMap<String, Integer> nameMap,
                           HashMap<Integer, Integer> chatMap,
                           int minTime,
                           int maxTime) {
            this.nameMap = nameMap;
            this.chatMap = chatMap;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }
    }

    /**
     * Applies the current filters by chat and user to the given stream
     */
    private Stream<ChatMessage> applyFilters(Stream<ChatMessage> stream) {
        // Filter by chat
        if(this.chats instanceof Blacklist){
            final Blacklist chats = (Blacklist) this.chats;
            stream = stream.filter(message -> !chats.contains(message.convo_id));
        } else if(this.chats instanceof Whitelist){
            final Whitelist chats = (Whitelist) this.chats;
            stream = stream.filter(message -> chats.contains(message.convo_id));
        }
        // Filter by user
        if(this.users instanceof Blacklist){
            final Blacklist users = (Blacklist) this.users;
            stream = stream.filter(message -> !users.contains(message.author));
        } else if(this.users instanceof Whitelist){
            final Whitelist users = (Whitelist) this.users;
            stream = stream.filter(message -> users.contains(message.author));
        }
        return stream;
    }

    /**
     * Collects the stream into a Set of pairs, with all axis mapped to the range 0..1
     * @param stream
     * @return
     */
    private Chart getScaledMessages(Stream<ChatMessage> stream) {
        final List<ChatMessage> filteredMessages = stream.collect(Collectors.toList());
        MappingData mappingData = getMinMax(filteredMessages);

        System.out.println("Mapping data of chats");
        for (Map.Entry<Integer, Integer> entry : mappingData.chatMap.entrySet()) {
            System.out.println(entry.getKey() + " " +entry.getValue());
        }
        System.out.println("Mapping data of chats");

        final Axis X = this.x;
        final Axis Y = this.y;

        final List<Pair<Double, Double>> pairs = filteredMessages.stream().map(new Function<ChatMessage, Pair<Double, Double>>() {
            public Pair<Double, Double> apply(ChatMessage message) {
                double xcor = PickByAxis(message, mappingData, X);
                double ycor = PickByAxis(message, mappingData, Y);
                System.out.println(xcor+" "+ycor);
                return new Pair<>(xcor, ycor);
            }
        }).collect(Collectors.toList());
        HashMap xMap=null, yMap=null;
        switch (x){
            case chat:
                xMap = mappingData.chatMap;
                break;
            case user:
                xMap = mappingData.nameMap;
                break;
        }
        switch (y){
            case chat:
                yMap = mappingData.chatMap;
                break;
            case user:
                yMap = mappingData.nameMap;
                break;
        }
        return new Chart(pairs, xMap, yMap);
    }

    /**
     * Gets the value of this message on the axis given, timestamps given as relative, others as positive integers
     */
    private static double PickByAxis(ChatMessage message, MappingData md, Axis x) {
        switch (x) {
            case chat:
                return md.chatMap.get(message.convo_id);
            case user:
                return md.nameMap.get(message.author_id);
            case time:
                return ((double)(message.timestamp - md.minTime)) / ((double)(md.maxTime-md.minTime));
            default:
                throw new RuntimeException("This line should not be reached");
        }
    }


    /**
     * Reads all the messages, returns the following
     * <ol>
     *     <li>minimum timestamp
     *     <li>maximum timestamp
     *     <li>hashmap of names
     *     <li>hashmap of conversations
     *     <li>
     */
    private MappingData getMinMax(List<ChatMessage> messages) {
        int minTime = messages.get(0).timestamp;
        int maxTime = messages.get(0).timestamp;
        HashMap<String, Integer> names = new HashMap<>();
        HashMap<Integer, Integer> chats = new HashMap<>();
        for (ChatMessage message : messages) {
            minTime = Math.min(minTime, message.timestamp);
            maxTime = Math.max(maxTime, message.timestamp);
            names.put(message.author_id, names.getOrDefault(message.author_id, names.size()));
            chats.put(message.convo_id, names.getOrDefault(message.convo_id, chats.size()));
        }
        return new MappingData(names, chats, minTime, maxTime);
    }

}
