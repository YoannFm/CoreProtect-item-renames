package net.coreprotect.database.statement;

import java.sql.PreparedStatement;

public class ItemRenameStatement {

    private ItemRenameStatement() {
        throw new IllegalStateException("Database class");
    }

    public static void insert(PreparedStatement preparedStmt, int batchCount, int time, int userId, int wid, int x, int y, int z, int typeId, String oldName, String newName) {
        try {
            preparedStmt.setInt(1, time);
            preparedStmt.setInt(2, userId);
            preparedStmt.setInt(3, wid);
            preparedStmt.setInt(4, x);
            preparedStmt.setInt(5, y);
            preparedStmt.setInt(6, z);
            preparedStmt.setInt(7, typeId);
            preparedStmt.setString(8, oldName);
            preparedStmt.setString(9, newName);
            preparedStmt.addBatch();

            if (batchCount > 0 && batchCount % 1000 == 0) {
                preparedStmt.executeBatch();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
