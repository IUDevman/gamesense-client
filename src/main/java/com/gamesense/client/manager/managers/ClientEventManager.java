package com.gamesense.client.manager.managers;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.NameUtil;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.manager.Manager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public enum ClientEventManager implements Manager {

    INSTANCE;

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

    @SubscribeEvent
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        GameSense.EVENT_BUS.post(event);
    }

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                    if (playerData.getProfile().getId() != getMinecraft().session.getProfile().getId()) {
                        new Thread(() -> {
                            String name = NameUtil.resolveName(playerData.getProfile().getId().toString());
                            if (name != null) {
                                if (getPlayer() != null && getPlayer().ticksExisted >= 1000) {
                                    GameSense.EVENT_BUS.post(new PlayerJoinEvent(name));
                                }
                            }
                        }).start();
                    }
                }
            }
            if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                    if (playerData.getProfile().getId() != getMinecraft().session.getProfile().getId()) {
                        new Thread(() -> {
                            final String name = NameUtil.resolveName(playerData.getProfile().getId().toString());
                            if (name != null) {
                                if (getPlayer() != null && getPlayer().ticksExisted >= 1000) {
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
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (getMinecraft().player == null || getMinecraft().world == null) return;

        if (event.getEntity().getEntityWorld().isRemote && event.getEntityLiving() == getPlayer()) {
            for (Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) continue;
                module.onUpdate();
            }

            GameSense.EVENT_BUS.post(event);
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) return;
        if (getMinecraft().player == null || getMinecraft().world == null) return;

        getProfiler().startSection("gamesense");
        getProfiler().startSection("setup");
        RenderUtil.prepare();
        RenderEvent event1 = new RenderEvent(event.getPartialTicks());
        getProfiler().endSection();

        for (Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) continue;
            getProfiler().startSection(module.getName());
            module.onWorldRender(event1);
            getProfiler().endSection();
        }

        getProfiler().startSection("release");
        RenderUtil.release();
        getProfiler().endSection();
        getProfiler().endSection();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (getMinecraft().player == null || getMinecraft().world == null) return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            for (Module module : ModuleManager.getModules()) {
                if (!module.isEnabled()) continue;
                module.onRender();
            }
            GameSense.INSTANCE.gameSenseGUI.render();
        }

        GameSense.EVENT_BUS.post(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState() || Keyboard.getEventKey() == Keyboard.KEY_NONE) return;

        EntityPlayerSP player = getPlayer();
        if (player != null && !player.isSneaking()) {
            String prefix = CommandManager.getCommandPrefix();
            char typedChar = Keyboard.getEventCharacter();
            if (prefix.length() == 1 && prefix.charAt(0) == typedChar) {
                getMinecraft().displayGuiScreen(new GuiChat(prefix));
            }
        }

        int key = Keyboard.getEventKey();

        if (key != Keyboard.KEY_NONE) {
            for (Module module : ModuleManager.getModules()) {
                if (module.getBind() != key) continue;
                module.toggle();
            }
        }

        GameSense.INSTANCE.gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
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
                getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                CommandManager.callCommand(event.getMessage().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + e.getMessage(), true);
            }
        }
    }
}