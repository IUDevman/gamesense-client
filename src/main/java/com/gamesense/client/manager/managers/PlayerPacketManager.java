package com.gamesense.client.manager.managers;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.util.misc.CollectionUtils;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.client.manager.Manager;
import me.zero.alpine.listener.Listener;

import java.util.ArrayList;
import java.util.List;

// Sponsored by KAMI Blue
// https://github.com/kami-blue/client/blob/master/src/main/kotlin/org/kamiblue/client/manager/managers/PlayerPacketManager.kt
public enum PlayerPacketManager implements Manager {
    INSTANCE;

    private final List<PlayerPacket> packets = new ArrayList<>();

    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.BY) return;

        if (!packets.isEmpty()) {
            PlayerPacket packet = CollectionUtils.maxOrNull(packets, PlayerPacket::getPriority);
            if (packet != null) {
                event.apply(packet);
            }
        }
    });

    public void addPacket(PlayerPacket packet) {
        packets.add(packet);
    }

}
