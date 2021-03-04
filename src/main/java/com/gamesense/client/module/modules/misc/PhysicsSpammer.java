package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Module.Declaration(name = "PhysicsSpammer", category = Category.Misc)
public class PhysicsSpammer extends Module {

    private IntegerSetting minDelay, maxDelay;

    public void setup() {
        minDelay = registerInteger("Min Delay", 5, 1, 100);
        maxDelay = registerInteger("Max Delay", 5, 1, 100);
        updateTimes();
    }

    private List<String> cache = new LinkedList<String>();
    private long lastTime, delay;
    private Random random = new Random(System.currentTimeMillis());

    public void onUpdate() {
        if (delay > Math.max(minDelay.getValue(), maxDelay.getValue()))
            delay = Math.max(minDelay.getValue(), maxDelay.getValue());
        else if (delay < Math.min(minDelay.getValue(), maxDelay.getValue()))
            delay = Math.min(minDelay.getValue(), maxDelay.getValue());
        if (System.currentTimeMillis() >= lastTime + 1000 * delay) {
            if (cache.size() == 0) {
                try {
                    Scanner scanner = new Scanner(new URL("http://snarxiv.org/").openStream());
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("<p>")) {
                            if (line.startsWith("<p><a") || line.startsWith("<p>Links to:")) continue;
                            line = line.substring(3);
                            while (true) {
                                int pos = line.indexOf(". ");
                                if (pos < 0) {
                                    cache.add(line);
                                    break;
                                } else {
                                    cache.add(line.substring(0, pos + 1));
                                    line = line.substring(pos + 2);
                                }
                            }
                        }
                    }
                    scanner.close();
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
            if (cache.size() == 0) cache.add("Error! :(");
            MessageBus.sendServerMessage("> " + cache.get(0));
            cache.remove(0);
            updateTimes();
        }
    }

    private void updateTimes() {
        lastTime = System.currentTimeMillis();
        int bound = Math.abs(maxDelay.getValue() - minDelay.getValue());
        delay = (bound == 0 ? 0 : random.nextInt(bound)) + Math.min(maxDelay.getValue(), minDelay.getValue());
    }
}