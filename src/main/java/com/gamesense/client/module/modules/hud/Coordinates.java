package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

@Module.Declaration(name = "Coordinates", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 0)
public class Coordinates extends HUDModule {

    BooleanSetting showNetherOverworld = registerBoolean("Show Nether", true);
    BooleanSetting thousandsSeparator = registerBoolean("Thousands Separator", true);
    IntegerSetting decimalPlaces = registerInteger("Decimal Places", 1, 0, 5);

    private final String[] coordinateString = {"", ""};

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener = new Listener<>(event -> {
        if (event.phase != TickEvent.Phase.END) return;

        Entity viewEntity = mc.getRenderViewEntity();
        EntityPlayerSP player = mc.player;

        if (viewEntity == null) {
            if (player != null) {
                viewEntity = player;
            } else {
                return;
            }
        }

        int dimension = viewEntity.dimension;

        coordinateString[0] = "XYZ " + getFormattedCoords(viewEntity.posX, viewEntity.posY, viewEntity.posZ);

        switch (dimension) {
            case -1: // Nether
                coordinateString[1] = "Overworld "
                        + getFormattedCoords(viewEntity.posX * 8.0, viewEntity.posY, viewEntity.posZ * 8.0);
                break;
            case 0: // Overworld
                coordinateString[1] = "Nether "
                        + getFormattedCoords(viewEntity.posX / 8.0, viewEntity.posY, viewEntity.posZ / 8.0);
                break;
            default:
                break;
        }
    });

    private String getFormattedCoords(double x, double y, double z) {
        return roundOrInt(x) + ", " + roundOrInt(y) + ", " + roundOrInt(z);
    }

    private String roundOrInt(double input) {
        String separatorFormat;

        if (thousandsSeparator.getValue()) {
            separatorFormat = ",";
        } else {
            separatorFormat = "";
        }

        return String.format('%' + separatorFormat + '.' + decimalPlaces.getValue() + 'f', input);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new CoordinateLabel());
    }

    private class CoordinateLabel implements HUDList {
        @Override
        public int getSize() {
            EntityPlayerSP player = mc.player;
            int dimension = player != null ? player.dimension : 1;

            if (showNetherOverworld.getValue() && (dimension == -1 || dimension == 0)) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public String getItem(int index) {
            return coordinateString[index];
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}