package com.gamesense.client.command.commands;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.waypoint.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.awt.*;

public class WaypointCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{
                "waypoint", "point", "wp", "waypoints", "points"
        };
    }

    @Override
    public String getSyntax() {
        return "waypoint <add | del | list> <name> <x> <y> <z> <(optional) color>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 6) {
                GameSenseMod.getInstance().waypointManager.addWaypoint(new Waypoint(
                        args[1],
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3]),
                        Double.parseDouble(args[4]),
                        Color.getColor(args[5])));
                sendClientMessage("added waypoint: Name: " + args[1] +
                        " x" + Double.parseDouble(args[2]) + " y" + Double.parseDouble(args[3]) + " z" + Double.parseDouble(args[4]) +
                        " Color: " + Color.getColor(args[5]));
            } else if (args.length == 5) {
                GameSenseMod.getInstance().waypointManager.addWaypoint(new Waypoint(
                        args[1],
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3]),
                        Double.parseDouble(args[4])));
                sendClientMessage("added waypoint: Name: " + args[1] +
                        " x" + Double.parseDouble(args[2]) + " y" + Double.parseDouble(args[3]) + " z" + Double.parseDouble(args[4]));

            }
        } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
            GameSenseMod.getInstance().waypointManager.delWaypoint(GameSenseMod.getInstance().waypointManager.getWaypointByName(args[1]));
        } else if (args[0].equalsIgnoreCase("list")) {
            TextComponentString tcs = new TextComponentString("");
            GameSenseMod.getInstance().waypointManager.getWaypoints().forEach(w -> {
                tcs.appendSibling(new TextComponentString(w.getName() + ", "))
                        .setStyle(new Style()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("x" + w.getX() + " y" + w.getY() + " z" + w.getZ()))));
            });
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(tcs);
        }
    }
}
