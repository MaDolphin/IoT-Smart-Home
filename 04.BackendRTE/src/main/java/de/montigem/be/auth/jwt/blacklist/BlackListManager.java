/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.auth.jwt.blacklist;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class BlackListManager {
  
  protected static ITokenBlacklist blacklist;

  /**
   * @return blacklist
   */
  public static ITokenBlacklist getBlacklist() {
    if(null==blacklist){
//      blacklist = new DatabaseTokenBlacklist();
      blacklist = new MemoryTokenBlacklist();
    }
    return blacklist;
  }

  /**
   * @param blacklist the blacklist to set
   */
  public static void setBlacklist(ITokenBlacklist blacklist) {
    BlackListManager.blacklist = blacklist;
  }
  
}
