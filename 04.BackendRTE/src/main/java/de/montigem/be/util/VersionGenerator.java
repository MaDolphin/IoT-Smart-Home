/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.util;

import com.google.common.base.Throwables;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class VersionGenerator {
  
  protected VersionGenerator() {
    // intentionally left empty
  }
  
  public static final String DEFAULT_FILE_LOCATION = "target/gen/Version.txt";
  
  public static String getVersion() {
    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String date = formatter.format(new Date(System.currentTimeMillis()));
    return "Version 0.0.1-SNAPSHOT, built at " + date;
  }
  
  public static void printToFile(String content, File file) {
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
      w.write(content);
      w.flush();
      w.close();
    } catch (IOException e) {
      Log.debug(Throwables.getStackTraceAsString(e), VersionGenerator.class.getName());
    }
  }

  public static void main(String[] args) {
    Log.debug("print version " + VersionGenerator.getVersion(), VersionGenerator.class.getName());
    File file = new File(DEFAULT_FILE_LOCATION);
    Log.debug(file + " " + file.exists(), VersionGenerator.class.getName());
    printToFile(getVersion(), file);
    Log.debug(file + " " + file.exists(), VersionGenerator.class.getName());
  }
}
