package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.checker.*;
import org.bukkit.entity.Player;

import java.util.*;

public class RelocateAction implements WarpAction<Void> {
    private static final List<Checker> checkers = new ArrayList<>();
    static {
//        if (HookManager.isHookEnabled(HookManager.getBentoBoxHook())) checkers.add(new BentoBoxIslandChecker());
        if (HookManager.isHookEnabled(HookManager.getResidenceHook())) checkers.add(new ResidenceChecker());
        if (HookManager.isHookEnabled(HookManager.getWorldGuardHook())) checkers.add(new WorldGuardChecker());
//        if (HookManager.isHookEnabled(HookManager.getTerritoryHook())) checkers.add(new TerritoryChecker());
        if (HookManager.isHookEnabled(HookManager.getSuperiorSkyBlockHook())) checkers.add(new SuperiorSkyBlockChecker());
        if (HookManager.isHookEnabled(HookManager.getAngeschossenLands())) checkers.add(new AngeschossenLandsChecker());
//        if (HookManager.isHookEnabled(HookManager.getGriefPreventionHook())) checkers.add(new GriefPreventationChecker());
    }

    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (PlayerWarpsPlugin.getWarpHandler().getBannedWorlds().contains(worldName)
                && !PermissionUtil.hasPermission(player, PermissionUtil.Permission.ADMIN_PERMISSION)) {
            player.sendMessage(Lang.TRIED_TO_RELOCATE_WARP_TO_DISABLED_WORLD.asColoredString().replace("%world%", worldName));
            return false;
        }

        for (Checker checker : checkers) {
            if (!checker.check(player)) {
                return false;
            }
        }

        warp.setLocation(player.getLocation());
        player.sendMessage(Lang.WARP_RELOCATED.asReplacedString(player, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.RELOCATE_WARP;
    }

    @Override
    public int getFee() {
        return Config.RELOCATE_WARP_FEE.asInteger();
    }
}
