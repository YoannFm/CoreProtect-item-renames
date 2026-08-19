package net.coreprotect.utility;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import net.coreprotect.config.Config;
import net.coreprotect.consumer.Queue;

/**
 * Shared logic for detecting and logging item renames, regardless of source
 * (anvil, third-party "/rename" style commands, etc). Extends Queue purely to
 * gain access to the protected Queue.queueItemRename(...) method, matching the
 * pattern already used by WorldUtils for the same reason.
 */
public class ItemRenameUtil extends Queue {

    private ItemRenameUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDisplayName(ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return null;
    }

    /**
     * Compares the before/after state of an item and, if its display name actually
     * changed (and it's still recognizably the "same" item, not just a swap), queues
     * an item-rename log entry crediting the given user.
     *
     * @param user
     *            The player responsible for the rename
     * @param location
     *            Where to attribute the log entry (typically the player's location)
     * @param before
     *            The item before the change
     * @param after
     *            The item after the change
     * @return true if a rename was detected and queued for logging, false otherwise
     */
    public static boolean checkRename(String user, Location location, ItemStack before, ItemStack after) {
        if (user == null || location == null || before == null || after == null) {
            return false;
        }

        if (!Config.getConfig(location.getWorld()).ITEM_RENAME_LOGGING) {
            return false;
        }

        // Must still be the same underlying item type -- otherwise this isn't a rename,
        // it's a swap (e.g. the player picked up a different item in the meantime).
        if (before.getType() != after.getType()) {
            return false;
        }

        String oldName = getDisplayName(before);
        String newName = getDisplayName(after);
        if (Objects.equals(oldName, newName)) {
            return false;
        }

        Queue.queueItemRename(user, location.clone(), after.getType(), oldName, newName);
        return true;
    }

}
