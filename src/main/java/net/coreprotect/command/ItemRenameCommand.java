package net.coreprotect.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import net.coreprotect.config.ConfigHandler;
import net.coreprotect.database.Database;
import net.coreprotect.database.statement.UserStatement;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.ChatUtils;
import net.coreprotect.utility.Color;
import net.coreprotect.utility.MaterialUtils;
import net.coreprotect.utility.WorldUtils;

/**
 * Handles "/co itemrenames [player] [limit]" -- lists items that have been
 * renamed by players (e.g. via an anvil), showing who renamed each item,
 * the old/new name, item type, location, and how long ago it happened.
 */
public class ItemRenameCommand {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    protected static void runCommand(CommandSender sender, boolean permission, String[] args) {
        if (!permission) {
            Chat.sendMessage(sender, Color.DARK_AQUA + "CoreProtect " + Color.WHITE + "- You do not have permission to do that.");
            return;
        }

        String playerFilter = null;
        int limit = DEFAULT_LIMIT;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (arg.matches("\\d+")) {
                limit = Math.max(1, Math.min(Integer.parseInt(arg), MAX_LIMIT));
            }
            else {
                playerFilter = arg;
            }
        }

        final String filterUser = playerFilter;
        final int queryLimit = limit;

        Thread thread = new Thread(() -> {
            try (Connection connection = Database.getConnection(false, 1000)) {
                if (connection == null) {
                    Chat.sendMessage(sender, Color.DARK_AQUA + "CoreProtect " + Color.WHITE + "- Unable to connect to the database.");
                    return;
                }

                Integer userId = null;
                if (filterUser != null) {
                    Integer cached = ConfigHandler.playerIdCache.get(filterUser.toLowerCase(Locale.ROOT));
                    if (cached != null) {
                        userId = cached;
                    }
                    else {
                        int loaded = UserStatement.loadId(connection, filterUser, null);
                        if (loaded != -1) {
                            userId = loaded;
                        }
                    }

                    if (userId == null) {
                        Chat.sendMessage(sender, Color.DARK_AQUA + "CoreProtect " + Color.WHITE + "- No renames found for \"" + filterUser + "\".");
                        return;
                    }
                }

                Statement statement = connection.createStatement();
                StringBuilder query = new StringBuilder("SELECT time, user, wid, x, y, z, type, old_name, new_name FROM " + ConfigHandler.prefix + "item_rename");
                if (userId != null) {
                    query.append(" WHERE user = ").append(userId);
                }
                query.append(" ORDER BY rowid DESC LIMIT ").append(queryLimit);

                ResultSet resultSet = statement.executeQuery(query.toString());
                int currentTime = (int) (System.currentTimeMillis() / 1000L);

                Chat.sendMessage(sender, Color.WHITE + "----- " + Color.DARK_AQUA + "Item Renames" + Color.WHITE + " -----");

                boolean any = false;
                while (resultSet.next()) {
                    any = true;
                    int time = resultSet.getInt("time");
                    int uid = resultSet.getInt("user");
                    int wid = resultSet.getInt("wid");
                    int x = resultSet.getInt("x");
                    int y = resultSet.getInt("y");
                    int z = resultSet.getInt("z");
                    int typeId = resultSet.getInt("type");
                    String oldName = resultSet.getString("old_name");
                    String newName = resultSet.getString("new_name");

                    String userName = ConfigHandler.playerIdCacheReversed.get(uid);
                    if (userName == null) {
                        userName = UserStatement.loadName(connection, uid);
                    }
                    if (userName == null || userName.isEmpty()) {
                        userName = "#unknown";
                    }

                    String worldName = WorldUtils.getWorldName(wid);
                    String itemName = typeId > 0 ? MaterialUtils.getBlockNameShort(typeId) : "Item";

                    String timeSince = ChatUtils.getTimeSince(time, currentTime, false);

                    String oldDisplay = (oldName == null || oldName.isEmpty()) ? Color.GREY + "(unnamed " + itemName + ")" + Color.WHITE : Color.YELLOW + "\"" + oldName + Color.YELLOW + "\"" + Color.WHITE;
                    String newDisplay = (newName == null || newName.isEmpty()) ? Color.GREY + "(unnamed " + itemName + ")" + Color.WHITE : Color.YELLOW + "\"" + newName + Color.YELLOW + "\"" + Color.WHITE;

                    Chat.sendMessage(sender, Color.DARK_AQUA + userName + Color.WHITE + " renamed " + oldDisplay + " to " + newDisplay + Color.WHITE + " (" + itemName + ") " + Color.GREY + worldName + " " + x + "," + y + "," + z + Color.WHITE + " - " + timeSince);
                }

                resultSet.close();
                statement.close();

                if (!any) {
                    Chat.sendMessage(sender, Color.GREY + "No item renames found.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
