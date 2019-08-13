/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.database;

import org.apache.openejb.resource.jdbc.router.AbstractRouter;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseRouter extends AbstractRouter {

  private String dataSourceNames;

  private String defaultDataSourceName;

  private Map<String, DataSource> dataSources = null;

  private ThreadLocal<DataSource> currentDataSource = new ThreadLocal<DataSource>();

  /**
   * @param datasourceList datasource resource name, separator is a space
   */
  public void setDataSourceNames(String datasourceList) {
    dataSourceNames = datasourceList;
  }

  /**
   * lookup datasource in openejb resources
   */
  private void init() {
    dataSources = new ConcurrentHashMap<>();
    for (String ds : dataSourceNames.split(" ")) {
      try {
        Object o = getOpenEJBResource(ds);
        if (o instanceof DataSource) {
          dataSources.put(ds, DataSource.class.cast(o));
        }
      } catch (NamingException e) {
        // ignored
      }
    }
  }

  /**
   * @return the user selected data source if it is set
   * or the default one
   * @throws IllegalArgumentException if the data source is not found
   */
  @Override
  public DataSource getDataSource() {
    // lazy init of routed datasources
    if (dataSources == null) {
      init();
    }

    // if no datasource is selected use the default one
    if (currentDataSource.get() == null) {
      if (dataSources.containsKey(defaultDataSourceName)) {
        return dataSources.get(defaultDataSourceName);
      } else {
        throw new IllegalArgumentException("you have to specify at least one datasource");
      }
    }

    // set the datasource to use
    return currentDataSource.get();
  }

  /**
   * @param datasourceName data source name
   */
  public void setDataSource(String datasourceName) {
    if (dataSources == null) {
      init();
    }
    if (!dataSources.containsKey(datasourceName)) {
      throw new IllegalArgumentException(
          "data source called " + datasourceName + " can't be found.");
    }
    DataSource ds = dataSources.get(datasourceName);
    currentDataSource.set(ds);
  }

  /**
   * reset the data source
   */
  public void clear() {
    currentDataSource.remove();
  }

  public void setDefaultDataSourceName(String name) {
    this.defaultDataSourceName = name;
  }

}
