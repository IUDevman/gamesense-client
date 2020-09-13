package com.gamesense.api.players.enemy;

import java.util.ArrayList;
import java.util.List;

public class Enemies {

    public static List<Enemy> enemies;

    public Enemies() {
        enemies = new ArrayList<>();
    }

    public static List<Enemy> getEnemies() {
        return enemies;
    }

    public static boolean isEnemy(String name) {
        boolean b = false;
        for (Enemy e : getEnemies()) {
            if (e.getName().equalsIgnoreCase(name)) b = true;
        }
        return b;
    }

    public static Enemy getEnemyByName(String name) {
        Enemy en = null;
        for (Enemy e : getEnemies()) {
            if (e.getName().equalsIgnoreCase(name)) en = e;
        }
        return en;
    }

    public static void addEnemy(String name) {
        enemies.add(new Enemy(name));
    }

    public static void delEnemy(String name) {
        enemies.remove(getEnemyByName(name));
    }
}
