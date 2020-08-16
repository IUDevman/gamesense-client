package com.gamesense.api.util.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeUtils {
    List<UUID> uuids = new ArrayList<>();
    public CapeUtils(){
        try {
            URL pastebin = new URL("https://pastebin.com/raw/1CjwdKYZ");
            BufferedReader in = new BufferedReader(new InputStreamReader(pastebin.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                uuids.add(UUID.fromString(inputLine));
            }
        } catch(Exception e){}
    }

    public boolean hasCape(UUID id){
        return uuids.contains(id);
    }
}
