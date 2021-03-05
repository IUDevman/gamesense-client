package com.gamesense.api.util.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeUtil {

    List<UUID> uuids = new ArrayList<>();

    public CapeUtil() {
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

    public boolean hasCape(UUID id) {
        return uuids.contains(id);
    }
}