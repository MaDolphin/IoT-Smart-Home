/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util;

import javax.ejb.Stateless;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class SessionManager {

  private Map<Session, MontiGemSecurityContext> sessions = new HashMap<>();

  public void addSession(Session session) {
    sessions.put(session, new MontiGemSecurityContext(false));
  }

  public void removeSession(Session session) {
    sessions.remove(session);
  }

  public MontiGemSecurityContext getSecurityContext(Session session) {
    return sessions.get(session);
  }
}
