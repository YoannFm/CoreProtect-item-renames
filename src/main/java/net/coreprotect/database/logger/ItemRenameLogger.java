package net.coreprotect.database.logger;

import java.sql.PreparedStatement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import net.coreprotect.CoreProtect;
import net.coreprotect.config.Config;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.database.statement.ItemRenameStatement;
import net.coreprotect.database.statement.UserStatement;
import net.coreprotect.event.CoreProtectPreLogEvent;
import net.coreprotect.utility.MaterialUtils;
import net.coreprotect.utility.WorldUtils;

/**
 * Logs items renamed by players (e.g. via an anvil), recording who renamed the
 * item, its original name, and its new name.
 */
public class ItemRenameLogger {

    private ItemRenameLogger() {
        throw new IllegalStateException("Database class");
    }

    public static void log(PreparedStatement preparedStmt, int batchCount, String user, Location location, Material type, String oldName, String newName) {
        try {
            if (ConfigHandler.isBlacklisted(user)) {
                return;
            }

            CoreProtectPreLogEvent event = new CoreProtectPreLogEvent(user, location, CoreProtectPreLogEvent.Action.ITEM_RENAME, 0, type, null, newName);
            if (Config.getGlobal().API_ENABLED && !Bukkit.isPrimaryThread()) {
                CoreProtect.getInstance().getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                return;
            }

            int userId = UserStatement.getId(preparedStmt, event.getUser(), true);
            Location eventLocation = event.getLocation();
            int wid = WorldUtils.getWorldId(eventLocation.getWorld().getName());
            int time = (int) (System.currentTimeMillis() / 1000L);
            int x = eventLocation.getBlockX();
            int y = eventLocation.getBlockY();
            int z = eventLocation.getBlockZ();
            int typeId = type != null ? MaterialUtils.getBlockId(type.name(), true) : 0;

            // Truncate defensively -- the column is varchar(256)
            if (oldName != null && oldName.length() > 256) {
                oldName = oldName.substring(0, 256);
            }
            if (newName != null && newName.length() > 256) {
                newName = newName.substring(0, 256);
            }

            ItemRenameStatement.insert(preparedStmt, batchCount, time, userId, wid, x, y, z, typeId, oldName, newName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
