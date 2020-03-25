/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth.jwt.blacklist;

/**
 * TODO: Write me!
 *
 *          $Date$
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
