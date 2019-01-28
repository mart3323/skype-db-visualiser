package Analyser.Chart;

import java.util.ArrayList;

/**
 * Represents all the options with regards to how a graph can be drawn
 */
public interface IChartType {
    enum Axis {chat, user, time}

    void whitelistTime(int end, int start);

    //region Users
    /**
     * Set users to be filtered according to a blacklist
     * users in the blacklist will not be fetched, everyone else  will
     */
    void blacklistUsers(String[] names);

    /**
     * Set users to be filtered according to a whitelist
     * only users in the whitelist will be fetched
     */
    void whitelistUsers(String[] names);

    /**
     * Marks a set of users to be grouped, making all their data appear under one name
     * Grouping overrides filters
     * @param names list of user ID's
     * @param groupName name to assign the group
     */
    void groupUsers(String[] names, String groupName);
    //endregion

    //region Chats
    /**
     * Set chats to be filtered according to a blacklist
     * chats in the blacklist will not be fetched, all the rest will
     */
    void blacklistChats(Integer[] names);
    /**
     * Set chats to be filtered according to a whitelist
     * only chats in the whitelist will be fetched
     */
    void whitelistChats(Integer[] names);

    /**
     * Marks a set of chats to be grouped, making all their data appear under one name
     * Grouping overrides filters
     * @param names list of chat ID's
     * @param groupName name to assign the group
     */
    void groupChats(Integer[] names, String groupName);
    //endregion

    //region Classes
    class filterList<T> extends ArrayList<T> {}
    class Blacklist<T> extends filterList<T>{}
    class Whitelist<T> extends filterList<T>{}

    class Group<T> extends ArrayList<T>{
        String name;
        public void setName(String name){ this.name = name; }
    }
    //endregion
}
