package de.montigem.be.database;

import de.montigem.be.MontiGemInitUtils;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Stateless
public class DatabaseDataSource {

  public void initalDataSource(boolean isOnServer) {
    String myUrl = DatabaseDataSourceUtil.setMappingUrl(isOnServer);
    try {
      Connection conn = DatabaseDataSourceUtil.getConnection(myUrl, "admin", "pass");
      Statement st = conn.createStatement();
      st.executeUpdate("CREATE TABLE IF NOT EXISTS DataSource " +
          "(dbname VARCHAR(255), " +
          " bezeichnung VARCHAR(255), " +
          " ip VARCHAR(255), " +
          " username VARCHAR(255), " +
          " password VARCHAR(255), " +
          " iadmin VARCHAR(255), " +
          " imail VARCHAR(255), " +
          " PRIMARY KEY ( dbname ))");
      String query = "SELECT * FROM datasource.DataSource db";
      st = conn.createStatement();
      ResultSet resultSet = st.executeQuery(query);
      if (!resultSet.next()) {
        st = conn.createStatement();
        st.executeUpdate(
            "insert into DataSource (dbname, bezeichnung, ip, username, password, iadmin, imail) values ('TestDB', 'TestDB', 'localhost', 'admin', 'pass', 'admin', 'macoco@se.rwth-aachen.de' )");
      }
      conn.close();
    } catch (SQLException e) {
      Log.error("MABx5009", e);
    }
  }

  public Optional<String> getDatabaseName(String dbBezeichner) {
    boolean isOnServer = MontiGemInitUtils.isOnServer();
    String query =
        "SELECT * FROM datasource.DataSource db WHERE db.bezeichnung = '" + dbBezeichner + "'";
    String myUrl = DatabaseDataSourceUtil.setMappingUrl(isOnServer);
    try {
      Connection conn = DatabaseDataSourceUtil.getConnection(myUrl, "admin", "pass");
      ResultSet rs = DatabaseDataSourceUtil.prepareConnectionAndExecute(query, conn);
      if (rs.next()) {
        String result = rs.getString(1);
        conn.close();
        return Optional.of(result);
      }
      conn.close();
      return Optional.empty();
    } catch (SQLException e) {
      Log.error("MABx5010", e);
    }
    return Optional.empty();
  }

  public List<String> getAllDatabaseNames() {
    List<String> names = new ArrayList<>();
    boolean isOnServer = MontiGemInitUtils.isOnServer();
    String query = "SELECT * FROM datasource.DataSource";
    String myUrl = DatabaseDataSourceUtil.setMappingUrl(isOnServer);
    try {
      Connection conn = DatabaseDataSourceUtil.getConnection(myUrl, "admin", "pass");
      ResultSet rs = DatabaseDataSourceUtil.prepareConnectionAndExecute(query, conn);
      while (rs.next()) {
        names.add(rs.getString(1));
      }
      conn.close();
    } catch (SQLException e) {
      Log.error("MABx5011", e);
    }
    return names;
  }

  public Map<String, String> getUserForDB(String dbName) {
    boolean isOnServer = MontiGemInitUtils.isOnServer();
    String query = "SELECT * FROM datasource.DataSource db WHERE db.dbname = '" + dbName + "'";
    String myUrl = DatabaseDataSourceUtil.setMappingUrl(isOnServer);
    Map<String, String> admin = new HashMap<>();
    try {
      Connection conn = DatabaseDataSourceUtil.getConnection(myUrl, "admin", "pass");
      ResultSet rs = DatabaseDataSourceUtil.prepareConnectionAndExecute(query, conn);
      if (rs.next()) {
        admin.put(rs.getString(6), rs.getString(7));
      }
      conn.close();
    } catch (SQLException e) {
      Log.error("MABx5012", e);
    }
    return admin;
  }
}