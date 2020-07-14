package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

public class PlayerSkin extends GameSenseEvent {
    private final NetworkPlayerInfo networkPlayerInfo;

    public PlayerSkin(NetworkPlayerInfo networkPlayerInfo) {
        this.networkPlayerInfo = networkPlayerInfo;
    }

    public NetworkPlayerInfo getNetworkPlayerInfo() {
        return this.networkPlayerInfo;
    }

    public static class GetSkin extends PlayerSkin {
        public ResourceLocation skinLocation;

        public GetSkin(NetworkPlayerInfo networkPlayerInfo, ResourceLocation skinLocation) {
            super(networkPlayerInfo);
            this.skinLocation = skinLocation;
        }
    }

    public static class HasSkin extends PlayerSkin {
        public boolean result;

        public HasSkin(NetworkPlayerInfo networkPlayerInfo, boolean result) {
            super(networkPlayerInfo);
            this.result = result;
        }
    }
}
