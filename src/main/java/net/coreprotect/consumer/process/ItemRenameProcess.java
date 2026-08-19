package net.coreprotect.consumer.process;

import java.sql.PreparedStatement;

import org.bukkit.Location;
import org.bukkit.Material;

import net.coreprotect.database.logger.ItemRenameLogger;

class ItemRenameProcess {

    static void process(PreparedStatement preparedStmt, int batchCount, String user, Object object) {
        if (!(object instanceof Object[])) {
            return;
        }

        Object[] renameData = (Object[]) object;
        if (renameData.length != 4 || !(renameData[0] instanceof Location)) {
            return;
        }

        Location location = (Location) renameData[0];
        Material type = (Material) renameData[1];
        String oldName = (String) renameData[2];
        String newName = (String) renameData[3];

        ItemRenameLogger.log(preparedStmt, batchCount, user, location, type, oldName, newName);
    }
}
