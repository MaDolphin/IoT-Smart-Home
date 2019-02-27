/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * The Configuration for the REST service Application. Sets the base path for URLs
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@ApplicationPath("api")
public class ApplicationConfig extends Application {
  
  public ApplicationConfig() {
    //Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
  }
}
