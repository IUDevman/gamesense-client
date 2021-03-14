package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ACCalculate implements Callable<Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>>> {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> EMPTY_LIST = new ArrayList<>();

    private final ACSettings settings;

    private final List<PlayerInfo> targets;
    private final List<EntityEnderCrystal> crystals;
    private final List<BlockPos> blocks;

    private final long globalTimeoutTime;

    public ACCalculate(ACSettings settings, List<PlayerInfo> targets, List<EntityEnderCrystal> crystals, List<BlockPos> blocks, long globalTimeoutTime) {
        this.settings = settings;

        this.targets = targets;
        this.crystals = crystals;
        this.blocks = blocks;

        this.globalTimeoutTime = globalTimeoutTime;
    }

    @Override
    public Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> call() {
        return getPlayers(startThreads());
    }

    @Nonnull
    private List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> startThreads() {
        // remove all placements that deal more than max self damage
        // no point in checking these
        final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        blocks.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamage((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, mc.player);
            if (damage > settings.maxSelfDamage) {
                return true;
            } else return settings.antiSuicide && damage > playerHealth;
        });
        if (blocks.size() == 0) {
            return EMPTY_LIST;
        }

        List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> output = new ArrayList<>();

        for (PlayerInfo target : targets) {
            output.add(ACHelper.executor.submit(new ACSubThread(settings, blocks, crystals, target)));
        }

        return output;
    }

    private Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> getPlayers(List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> input) {
        List<CrystalInfo.PlaceInfo> place = new ArrayList<>();
        List<CrystalInfo.BreakInfo> breaks = new ArrayList<>();
        for (Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>> future : input) {
            while (!future.isDone() && !future.isCancelled()) {
                if (System.currentTimeMillis() > globalTimeoutTime) {
                    break;
                }
            }
            if (future.isDone()) {
                Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo> crystal = null;
                try {
                    crystal = future.get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
                if (crystal != null) {
                    place.add(crystal.getValue());
                    breaks.add(crystal.getKey());
                }
            } else {
                future.cancel(true);
            }
        }

        // sort by damage
        return new Pair<>(breaks.stream().sorted(Comparator.comparing(breakInfo -> breakInfo.damage)).collect(Collectors.toList()), place.stream().sorted(Comparator.comparing(placeInfo -> placeInfo.damage)).collect(Collectors.toList()));
    }
}
