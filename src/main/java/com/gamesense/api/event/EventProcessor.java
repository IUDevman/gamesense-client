package com.gamesense.api.event;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.ModuleManager;
import com.google.common.collect.Maps;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class EventProcessor {

    public static EventProcessor INSTANCE;
    Minecraft mc = Minecraft.getMinecraft();
    CommandManager commandManager = new CommandManager();

    public EventProcessor() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderScreen(RenderGameOverlayEvent.Text event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onPlayerPush(PlayerSPPushOutOfBlocksEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        GameSense.EVENT_BUS.post(event);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                    if (playerData.getProfile().getId() != mc.session.getProfile().getId()) {
                        new Thread(() -> {
                            String name = resolveName(playerData.getProfile().getId().toString());
                            if (name != null) {
                                if (mc.player != null && mc.player.ticksExisted >= 1000) {
                                    GameSense.EVENT_BUS.post(new PlayerJoinEvent(name));
                                }
                            }
                        }).start();
                    }
                }
            }
            if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                    if (playerData.getProfile().getId() != mc.session.getProfile().getId()) {
                        new Thread(() -> {
                            final String name = resolveName(playerData.getProfile().getId().toString());
                            if (name != null) {
                                if (mc.player != null && mc.player.ticksExisted >= 1000) {
                                    GameSense.EVENT_BUS.post(new PlayerLeaveEvent(name));
                                }
                            }
                        }).start();
                    }
                }
            }
        }
    });

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player != null) {
            ModuleManager.onUpdate();
        }

        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ModuleManager.onWorldRender(event);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        GameSense.EVENT_BUS.post(event);
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            ModuleManager.onRender();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState() || Keyboard.getEventKey() == Keyboard.KEY_NONE) return;

        EntityPlayerSP player = mc.player;
        if (player != null && !player.isSneaking()) {
            String prefix = CommandManager.getCommandPrefix();
            char typedChar = Keyboard.getEventCharacter();
            if (prefix.length() == 1 && prefix.charAt(0) == typedChar) {
                mc.displayGuiScreen(new GuiChat(prefix));
            }
        }

        ModuleManager.onBind(Keyboard.getEventKey());
        GameSense.getInstance().gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            GameSense.EVENT_BUS.post(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(CommandManager.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                commandManager.callCommand(event.getMessage().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + e.getMessage(), true);
            }
        }
    }

    private final Map<String, String> uuidNameCache = Maps.newConcurrentMap();

    public String resolveName(String uuid) {
        uuid = uuid.replace("-", "");
        if (uuidNameCache.containsKey(uuid)) {
            return uuidNameCache.get(uuid);
        }

        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = (JSONObject) jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        return latestName.get("name").toString();
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void init() {
        GameSense.EVENT_BUS.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
    }
}