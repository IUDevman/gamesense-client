package com.gamesense.api.util.world.combat.ac;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.threads.ACCalculate;
import com.gamesense.client.GameSense;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public enum ACHelper {
    INSTANCE;

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> EMPTY_PAIR = new Pair<>(new ArrayList<>(), new ArrayList<>());

    // Threading Stuff
    public static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static final ExecutorService mainExecutors = Executors.newSingleThreadExecutor();
    private Future<Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>>> mainThreadOutput;

    // stores all the locations we have attempted to place crystals
    // and the corresponding crystal for that location (if there is any)
    private final Map<BlockPos, EntityEnderCrystal> placedCrystals = Collections.synchronizedMap(new HashMap<>());

    private ACSettings settings = null;
    private List<BlockPos> possiblePlacements = new ArrayList<>();
    private List<EntityEnderCrystal> targetableCrystals = new ArrayList<>();
    private final List<PlayerInfo> targetsInfo = new ArrayList<>();

    public void startCalculations(long timeout) {
        if (mainThreadOutput != null) {
            mainThreadOutput.cancel(true);
        }
        mainThreadOutput = mainExecutors.submit(new ACCalculate(settings, targetsInfo, targetableCrystals, possiblePlacements, timeout));
    }

    // returns null if still calculating
    // returns EMPTY_PAIR if finished or not started
    public Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> getOutput(boolean wait) {
        if (mainThreadOutput == null) {
            return EMPTY_PAIR;
        }

        if (wait) {
            while (!(mainThreadOutput.isDone() || mainThreadOutput.isCancelled())) {
            }
        } else {
            if (!(mainThreadOutput.isDone())) {
                return null;
            }
            if (mainThreadOutput.isCancelled()) {
                return EMPTY_PAIR;
            }
        }

        Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> output = EMPTY_PAIR;
        try {
            output = mainThreadOutput.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        mainThreadOutput = null;
        return output;
    }

    public void recalculateValues(ACSettings settings, PlayerInfo self, float armourPercent, double enemyDistance) {
        this.settings = settings;

        // entity range is the range from each crystal
        // so adding these together should solve problem
        // and reduce searching time
        final double entityRangeSq = (enemyDistance) * (enemyDistance);
        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(entity -> self.entity.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .filter(entity -> entity.getHealth() > 0.0f)
                .collect(Collectors.toList());

        targetableCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toList());

        final boolean own = settings.breakMode.equalsIgnoreCase("Own");
        if (own) {
            // remove own crystals that have been destroyed
            targetableCrystals.removeIf(crystal -> !placedCrystals.containsKey(EntityUtil.getPosition(crystal)));
            synchronized (placedCrystals) {
                placedCrystals.values().removeIf(crystal -> {
                    if (crystal == null) {
                        return false;
                    }
                    return crystal.isDead;
                });
            }
        }

        // remove all crystals that deal more than max self damage
        // and all crystals outside of break range
        // no point in checking these
        targetableCrystals.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamageThreaded(crystal.posX, crystal.posY, crystal.posZ, self);
            if (damage > settings.maxSelfDamage) {
                return true;
            } else return (settings.antiSuicide && damage > self.health) || self.entity.getDistanceSq(crystal) >= settings.breakRangeSq;
        });

        possiblePlacements = CrystalUtil.findCrystalBlocks(settings.placeRange, settings.endCrystalMode);
        // remove all placements that deal more than max self damage
        // no point in checking these
        possiblePlacements.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamageThreaded((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, settings.player);
            if (damage > settings.maxSelfDamage) {
                return true;
            } else return settings.antiSuicide && damage > settings.player.health;
        });

        targetsInfo.clear();
        for (EntityPlayer target : targets) {
            targetsInfo.add(new PlayerInfo(target, armourPercent));
        }
    }

    public void onPlaceCrystal(BlockPos target) {
        if (settings.breakMode.equalsIgnoreCase("Own")) {
            BlockPos up = target.up();
            placedCrystals.put(up, null);
        }
    }

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(entitySpawnListener);
    }

    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(entitySpawnListener);

        placedCrystals.clear();

        if (mainThreadOutput != null) {
            mainThreadOutput.cancel(true);
        }
    }

    @EventHandler
    private final Listener<EntityJoinWorldEvent> entitySpawnListener = new Listener<>(event -> {
        Entity entity = event.getEntity();
        if (entity instanceof EntityEnderCrystal) {
            if (settings != null && settings.breakMode.equalsIgnoreCase("Own")) {
                EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
                BlockPos crystalPos = EntityUtil.getPosition(crystal);
                placedCrystals.computeIfPresent(crystalPos, ((i, j) -> crystal));
            }
        }
    });

    public ACSettings getSettings() {
        return settings;
    }

    public List<BlockPos> getPossiblePlacements() {
        return possiblePlacements;
    }

    public List<EntityEnderCrystal> getTargetableCrystals() {
        return targetableCrystals;
    }
}
