/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.config;

import java.time.ZonedDateTime;

/**
 * Configuration class for storing constant values that might be reconfigured at
 * some point in time
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class Config {

  public static String DEFAULT_CURRENCY = "EUR";

  public static final String DOMAIN_DB = "de.montigem.db.TestDB";

  public static boolean DEVELOPER_MODE = true;

  public static String VERSION = "UNKNOWN";

  public static String TIMESTAMP = ZonedDateTime.now().toString();

  public static String COMMIT = "No Commit";

  public static String COMMIT_TIME = ZonedDateTime.now().toString();

  public static String BRANCH = "Unknown";

  public static boolean SEND_MAILS = true;
}

