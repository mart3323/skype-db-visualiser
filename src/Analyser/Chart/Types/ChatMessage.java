package Analyser.Chart.Types;


public class ChatMessage {
    public String author;
    public String author_id;
    public int timestamp;
    public int leaveReason;
    public int convo_id;
    public String chatName;

    public ChatMessage(String author, String author_id, int timestamp, int leaveReason, int convo_id, String chatName) {
        this.author = author;
        this.author_id = author_id;
        this.timestamp = timestamp;
        this.leaveReason = leaveReason;
        this.convo_id = convo_id;
        this.chatName = chatName;
    }
}
