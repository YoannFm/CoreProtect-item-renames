package net.coreprotect.listener.player;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import net.coreprotect.CoreProtect;
import net.coreprotect.config.Config;
import net.coreprotect.consumer.Queue;
import net.coreprotect.thread.Scheduler;
import net.coreprotect.utility.ItemRenameUtil;

public final class PlayerCommandListener extends Queue implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (Config.getConfig(player.getWorld()).PLAYER_COMMANDS) {
            long timestamp = System.currentTimeMillis() / 1000L;
            Queue.queuePlayerCommand(player, event.getMessage(), timestamp);
        }

        checkRenameCommand(event);

        /*
        if (Config.getGlobal().ENTITY_KILLS && player.hasPermission("bukkit.command.kill")) {
            EntityDeathListener.parseEntityKills(event.getMessage());
        }
        */
    }

    // How often to re-check for a rename after a matching command, and for how long.
    // Some plugins (e.g. CMI's "/rename") don't apply the change synchronously -- they
    // open a chat prompt or GUI and wait for further player input -- so a single
    // fixed-delay check isn't enough. Poll periodically instead, for up to a minute.
    private static final int RENAME_POLL_PERIOD_TICKS = 20; // 1 second
    private static final int RENAME_POLL_MAX_ATTEMPTS = 60; // up to 1 minute

    /**
     * Best-effort detection of item renames performed via third-party "/rename"-style
     * commands (as opposed to the vanilla anvil, which is handled separately). Since
     * Bukkit has no generic "item renamed" event, this snapshots the player's held
     * items when the command runs, then polls periodically to see if the display name
     * changed. This only catches renames that end up applying to the player's held
     * item(s) -- plugins that rename something other than the held item aren't covered.
     * <p>
     * Matches "rename" anywhere in the message, not just as the command label, to
     * also cover subcommand-style plugins (e.g. "/cmi rename ...", "/es itemrename").
     * Known compatible commands this covers: EssentialsX ("/itemrename", "/irename"),
     * EpicRename ("/rename"), CMI ("/rename", in addition to its vanilla-anvil-based
     * "/anvil" which is handled separately by InventoryChangeListener's anvil hook).
     */
    private void checkRenameCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (!Config.getConfig(player.getWorld()).ITEM_RENAME_LOGGING) {
            return;
        }

        if (!event.getMessage().toLowerCase(Locale.ROOT).contains("rename")) {
            return;
        }

        ItemStack mainHandBefore = cloneOrNull(player.getInventory().getItemInMainHand());
        ItemStack offHandBefore = cloneOrNull(player.getInventory().getItemInOffHand());
        if (mainHandBefore == null && offHandBefore == null) {
            return;
        }

        pollForRename(player.getName(), player.getLocation().clone(), mainHandBefore, offHandBefore);
    }

    private void pollForRename(String playerName, Location location, ItemStack mainHandBefore, ItemStack offHandBefore) {
        Object[] taskHolder = new Object[1];
        int[] attempts = { 0 };
        ItemStack[] mainHandState = { mainHandBefore };
        ItemStack[] offHandState = { offHandBefore };

        Runnable check = () -> {
            attempts[0]++;

            Player onlinePlayer = Bukkit.getPlayerExact(playerName);
            if (onlinePlayer == null) {
                Scheduler.cancelTask(taskHolder[0]);
                return;
            }

            ItemStack newMainHand = onlinePlayer.getInventory().getItemInMainHand();
            ItemStack newOffHand = onlinePlayer.getInventory().getItemInOffHand();

            boolean foundMain = ItemRenameUtil.checkRename(playerName, location, mainHandState[0], newMainHand);
            boolean foundOff = ItemRenameUtil.checkRename(playerName, location, offHandState[0], newOffHand);

            if (foundMain || foundOff || attempts[0] >= RENAME_POLL_MAX_ATTEMPTS) {
                Scheduler.cancelTask(taskHolder[0]);
                return;
            }

            mainHandState[0] = cloneOrNull(newMainHand);
            offHandState[0] = cloneOrNull(newOffHand);
        };

        taskHolder[0] = Scheduler.scheduleSyncRepeatingTask(CoreProtect.getInstance(), check, location, RENAME_POLL_PERIOD_TICKS, RENAME_POLL_PERIOD_TICKS);
    }

    private static ItemStack cloneOrNull(ItemStack item) {
        return item != null ? item.clone() : null;
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        if (Config.getGlobal().ENTITY_KILLS && event.getCommand().toLowerCase(Locale.ROOT).startsWith("kill")) {
            EntityDeathListener.parseEntityKills(event.getCommand());
        }
    }
    */

}
