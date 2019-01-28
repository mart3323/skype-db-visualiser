package Analyser.Glue;

public class SQLTypes {

    /**
     * Represents a single skype chat, name and ID
     */
    public static class Chat{
        public String name;
        public int id;
        public Chat(String name, int id) {
            this.name = name; this.id = id;
        }
    }

    /**
     * Represents a single message, author, timestamp, and leaveReason
     */
    public static class Message {
        public String author;
        public int timestamp;
        public int leaveReason;
        public Message(String author, int timestamp, int leaveReason) {
            this.author = author;
            this.timestamp = timestamp;
            this.leaveReason = leaveReason;
        }
    }
}
