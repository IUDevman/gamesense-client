package com.gamesense.api.util.player.friend;

import java.util.ArrayList;
import java.util.List;

public class Friends {

	public static List<Friend> friends;

	public Friends(){
		friends = new ArrayList<>();
	}

	public static List<Friend> getFriends() {
		return friends;
	}

	public static List<String> getFriendsByName() {
		ArrayList<String> friendsName = new ArrayList<>();
		friends.forEach(friend -> friendsName.add(friend.getName()));

		return friendsName;
	}

	public static boolean isFriend(String name) {
		boolean b = false;
		for (Friend f : getFriends()) {
			if (f.getName().equalsIgnoreCase(name)) {
				b = true;
				break;
			}
		}

		return b;
	}

	public static Friend getFriendByName(String name) {
		Friend fr = null;
		for (Friend f : getFriends()) {
			if (f.getName().equalsIgnoreCase(name)) {
				fr = f;
			}
		}

		return fr;
	}

	public static void addFriend(String name) {
		friends.add(new Friend(name));
	}

	public static void delFriend(String name) {
		friends.remove(getFriendByName(name));
	}
}