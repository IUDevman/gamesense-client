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
			URL pastebin = new URL("https://pastebin.com/raw/6D5JSYdC");
			BufferedReader in = new BufferedReader(new InputStreamReader(pastebin.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				uuids.add(UUID.fromString(inputLine));
			}
		}
		catch(Exception e) {

		}
	}

	public boolean hasCape(UUID id) {
		return uuids.contains(id);
	}
}