package com.gamesense.api.friends;

import java.util.ArrayList;
import java.util.List;

public class Friends {
    public static List<Friend> friends;
    public Friends(){
        friends = new ArrayList<>();
    }

    public static List<Friend> getFriends(){
        return friends;
    }

    public static boolean isFriend(String name){
        boolean b = false;
        for(Friend f : getFriends()){
            if(f.getName().equalsIgnoreCase(name)) b = true;
        }
        return b;
    }

    public Friend getFriendByName(String name){
        Friend fr = null;
        for(Friend f : getFriends()){
            if(f.getName().equalsIgnoreCase(name)) fr = f;
        }
        return fr;
    }

    public void addFriend(String name){
        friends.add(new Friend(name));
    }

    public void delFriend(String name){
        friends.remove(getFriendByName(name));
    }
}
