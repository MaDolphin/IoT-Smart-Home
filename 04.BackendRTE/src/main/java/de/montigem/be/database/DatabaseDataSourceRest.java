package de.montigem.be.database;

import de.montigem.be.MontiGemInitUtils;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Stateless
@Path("/domain/datasource")
@Produces("application/json")
@Interceptors(APIExceptionInterceptor.class)
public class DatabaseDataSourceRest {

  @OPTIONS
  @Path("{id}")
  public Response preflight() {
    return Responses.okResponse();
  }

  @GET
  @Path("/dbname")
  public Response getDatabaseName() {
    boolean isOnServer = MontiGemInitUtils.isOnServer();
    String query = "SELECT * FROM datasource.DataSource db";
    String myUrl = DatabaseDataSourceUtil.setMappingUrl(isOnServer);
    try {
      Connection conn = DatabaseDataSourceUtil.getConnection(myUrl, "admin", "pass");
      ResultSet rs = DatabaseDataSourceUtil.prepareConnectionAndExecute(query, conn);
      ArrayList<String> dbnamen = new ArrayList<>();
      while (rs.next()) {
        dbnamen.add(rs.getString(2));
      }
      conn.close();
      return Responses.okResponse(dbnamen);
    } catch (SQLException e) {
      Log.warn("MABx500A", e);
    }
    return Responses.error(MontiGemErrorFactory.conflict(), DatabaseDataSource.class);
  }
}
