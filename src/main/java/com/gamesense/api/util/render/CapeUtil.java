package com.gamesense.api.util.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeUtil {

    private static final List<UUID> uuids = new ArrayList<>();

    public static void init() {
        try {
            URL capesList = new URL("https://raw.githubusercontent.com/IUDevman/gamesense-assets/main/files/capeslist.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                uuids.add(UUID.fromString(inputLine));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasCape(UUID id) {
        return uuids.contains(id);
    }
}