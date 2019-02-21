/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.database;

import de.montigem.be.MontiGemInitUtils;
import de.se_rwth.commons.logging.Log;

import java.sql.*;

public class DatabaseDataSourceUtil {

  public static ResultSet prepareConnectionAndExecute(String query, Connection con)
      throws SQLException {
    PreparedStatement pst = con.prepareStatement(query);
    return pst.executeQuery();
  }

  public static Connection getConnection(String myUrl, String user, String password)
      throws SQLException {
    return DriverManager.getConnection(myUrl, user, password);
  }

  public static String getURL(final String address, final String port,
      final String dbName) {
    return "jdbc:mysql://" + address + ":" + port + "/" + dbName;
  }

  public static String setMappingUrl(boolean isOnServer) {
    String myUrl = "";
    if (!isOnServer) {
      myUrl = getURL("localhost", "3305", "datasource");
    } else {
      myUrl = getURL("datasource", "3306", "datasource");
    }
    return myUrl;
  }

  public static String getDatenbankBezeichner(String resource) {
    boolean isOnServer = MontiGemInitUtils.isOnServer();
    String query =
        "SELECT * FROM datasource.DataSource db where " + resource + " = LOWER(db.dbname) limit 1";
    String myUrl = setMappingUrl(isOnServer);
    try {
      Connection conn = getConnection(myUrl, "admin", "pass");
      ResultSet rs = prepareConnectionAndExecute(query, conn);
      if (rs.next()) {
        String result = rs.getString(2);
        conn.close();
        return result;
      }
    } catch (SQLException e) {
      Log.warn("MABx5AAA", e);
    }
    return resource;
  }

}
