package Analyser.Chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartType implements IChartType {
    //region Variables
    public int[] timeRange;
    public filterList<String> users;
    public filterList<Integer> chats;

    public List<Group<String>> userGroups = new ArrayList<>();
    public List<Group<Integer>> chatGroups = new ArrayList<>();

    public Axis x;
    public Axis y;
    /** Either an int representing a chat, or a String representing a user */
    public Object con;
    //endregion

    protected ChartType(){}
    public ChartType(Axis X, Axis Y, Object con) {
        this.con = con;
        this.x = X;
        this.y = Y;
    }

    //region Convenience (private)
    private <T> void addAll(List<T> list, T[] arr){
        Collections.addAll(list, arr);
    }
    private <T> Group<T> makeGroup(T[] values, String gName) {
        final Group<T> nameGroup = new Group<>();
        addAll(nameGroup, values);
        nameGroup.setName(gName);
        return nameGroup;
    }
    //endregion

    //region Overrides
    @Override
    public void whitelistTime(int end, int start) {
        this.timeRange = new int[]{start,end};
    }

    @Override
    public void blacklistUsers(String[] names) {
        this.users = new Blacklist<>();
        this.addAll(this.users, names);
    }

    @Override
    public void whitelistUsers(String[] names) {
        this.users = new Whitelist<>();
        this.addAll(this.users, names);
    }

    @Override
    public void groupUsers(String[] names, String groupName) {
        final Group<String> nameGroup = makeGroup(names, groupName);
        this.userGroups.add(nameGroup);
    }

    @Override
    public void blacklistChats(Integer[] chats) {
        this.chats = new Blacklist<>();
        addAll(this.chats, chats);
    }

    @Override
    public void whitelistChats(Integer[] chats) {
        this.chats = new Whitelist<>();
        addAll(this.chats, chats);
    }

    @Override
    public void groupChats(Integer[] chats, String groupName) {
        final Group<Integer> nameGroup = makeGroup(chats, groupName);
        this.chatGroups.add(nameGroup);
    }
    //endregion

}
