/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package de.montigem.be.config;

import de.montigem.be.marshalling.JsonMarshal;
import de.se_rwth.commons.logging.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Properties;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Date$<br>
 * $Revision$
 */
public class ConfigInitializer implements javax.servlet.ServletContextListener {

  protected String defaultCurrency = "EUR";

  protected boolean developerMode = false;

  protected long maxSubBudgetDepth = 3;

  protected String version = "UNKNOWN";

  protected String buildTime = ZonedDateTime.now().toString();

  protected final String CONFIG_FILE_PATH = "META-INF/config.yml";

  protected String commit = "No Commit";

  protected String commitTime = ZonedDateTime.now().toString();

  protected String branch = "Unknown";

  protected boolean sendMails = true;

  /**
   * @return defaultCurrency
   */
  public String getCurrency() {
    return this.defaultCurrency;
  }

  /**
   * @param defaultCurrency the defaultCurrency to set
   */
  public void setCurrency(String defaultCurrency) {
    this.defaultCurrency = defaultCurrency;
  }

  public void setCommit(String commit) {
    this.commit = commit;
  }

  public String getCommit() {
    return commit;
  }

  public String getCommitTime() {
    return commitTime;
  }

  public void setCommitTime(String commitTime) {
    this.commitTime = commitTime;
  }

  /**
   * @return developerMode
   */
  public boolean isDeveloperMode() {
    return this.developerMode;
  }

  /**
   * @param developerMode the developerMode to set
   */
  public void setDeveloperMode(boolean developerMode) {
    this.developerMode = developerMode;
  }

  public long getMaxSubBudgetDepth() {
    return maxSubBudgetDepth;
  }

  public void setMaxSubBudgetDepth(long maxSubBudgetDepth) {
    this.maxSubBudgetDepth = maxSubBudgetDepth;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBuildTime() {
    return buildTime;
  }

  public void setBuildTime(String buildTime) {
    this.buildTime = buildTime;
  }

  public boolean isSendMails() {
    return sendMails;
  }

  public void setSendMails(boolean sendMails) {
    this.sendMails = sendMails;
  }

  /**
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    loadConfig();
    setValues();
  }

  /**
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // TODO Auto-generated method stub

  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  public String getBranch() {
    return branch;
  }

  private void loadConfig() {
    Yaml yaml = new Yaml();

    try {
      @SuppressWarnings("unchecked")
      Map<String, String> values = (Map<String, String>) yaml
          .load(
              Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_PATH));

      if (!values.get("DEFAULT_CURRENCY").isEmpty()) {
        setCurrency(values.get("DEFAULT_CURRENCY"));
      }
      Log.info("DEFAULT_CURRENCY: " + getCurrency(), getClass().getName());

      if (!values.get("DEVELOPER_MODE").isEmpty()) {
        if (values.get("DEVELOPER_MODE").equals("true")) {
          setDeveloperMode(true);
        }
      }
      Log.info("DEVELOPER_MODE: " + developerMode, getClass().getName());

      if (!values.get("SEND_MAILS").isEmpty()) {
        if (values.get("SEND_MAILS").equals("false")) {
          setSendMails(false);
        }
      }
      Log.info("SEND_MAILS: " + sendMails, getClass().getName());

      try {
        if (!values.get("MAX_SUB_BUDGET_DEPTH").isEmpty()) {
          setMaxSubBudgetDepth(Long.parseLong(values.get("MAX_SUB_BUDGET_DEPTH")));
        }
      } catch (NumberFormatException e) {
        Log.warn("Couldn't read MAX_SUB_BUDGET_DEPTH from config", e);
      }
    } catch (YAMLException e) {
      Log.warn(getClass().getName() + ": could not read yaml", e);
    }

    Properties p = new Properties();
    try {
      p.load(
          Thread.currentThread().getContextClassLoader().getResourceAsStream("macoco.properties"));

      if (p.getProperty("git.build.version") != null) {
        setVersion(p.getProperty("git.build.version"));
        Log.info("Set the Version to " + this.version, getClass().getName());
      } else {

        Log.info("Couldn't set Version, use fallback: " + this.version, getClass().getName());
      }

      if (p.getProperty("git.commit.id.abbrev") != null) {
        setCommit(p.getProperty("git.commit.id.abbrev"));
      } else {

        Log.info("Couldn't set Commit, use fallback: " + this.commit, getClass().getName());
      }

      if (p.getProperty("git.commit.time") != null) {
        setCommitTime(p.getProperty("git.commit.time", ZonedDateTime.now().toString()));
      } else {
        Log.info("Couldn't set Commit_Time , use fallback: " + this.commitTime,
            getClass().getName());
      }
      if (p.getProperty("git.build.time") != null) {
        setBuildTime(p.getProperty("git.build.time", ZonedDateTime.now().toString()));
        Log.info("Set the BuildTime to " + this.buildTime, getClass().getName());

      } else {
        Log.info("Couldn't set BuildTime, use fallback: " + this.buildTime, getClass().getName());
      }
      if (p.getProperty("git.branch") != null) {
        setBranch(p.getProperty("git.branch"));
      } else {
        Log.info("Couldn't set Branch, use fallback: " + this.branch, getClass().getName());
      }
    } catch (IOException e) {
      Log.warn(getClass().getName() + ": could not read build.properties", e);
    }
  }

  public void setValues() {
    Config.DEFAULT_CURRENCY = defaultCurrency;
    Config.DEVELOPER_MODE = developerMode;
    Config.MAX_SUB_BUDGET_DEPTH = maxSubBudgetDepth;
    Config.VERSION = version;
    Config.TIMESTAMP = buildTime;
    Config.COMMIT = commit;
    Config.COMMIT_TIME = commitTime;
    Config.BRANCH = branch;
    Config.SEND_MAILS = sendMails;
  }

  public static String initializeConstants() {
    ConfigInitializer cfg = new ConfigInitializer();
    cfg.loadConfig();
    cfg.setValues();

    return JsonMarshal.getInstance().marshal(cfg);
  }

}
