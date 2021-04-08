package com.gamesense.api.util.player.social;

import java.util.ArrayList;

public class SocialManager {

    private static ArrayList<Friend> friends;
    private static ArrayList<Enemy> enemies;

    public static void init() {
        friends = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public static ArrayList<String> getFriendsByName() {
        ArrayList<String> friendNames = new ArrayList<>();

        getFriends().forEach(friend -> friendNames.add(friend.getName()));
        return friendNames;
    }

    public static ArrayList<String> getEnemiesByName() {
        ArrayList<String> enemyNames = new ArrayList<>();

        getEnemies().forEach(enemy -> enemyNames.add(enemy.getName()));
        return enemyNames;
    }

    public static boolean isFriend(String name) {
        boolean value = false;

        for (Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }

        return value;
    }

    public static boolean isEnemy(String name) {
        boolean value = false;

        for (Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }

        return value;
    }

    public static Friend getFriend(String name) {
        return getFriends().stream().filter(friend -> friend.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Enemy getEnemy(String name) {
        return getEnemies().stream().filter(enemy -> enemy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void addFriend(String name) {
        getFriends().add(new Friend(name));
    }

    public static void delFriend(String name) {
        getFriends().remove(getFriend(name));
    }

    public static void addEnemy(String name) {
        getEnemies().add(new Enemy(name));
    }

    public static void delEnemy(String name) {
        getEnemies().remove(getEnemy(name));
    }
}