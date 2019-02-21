package de.montigem.be.util;

import javax.ejb.Stateless;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class SessionManager {

  private Map<Session, MaCoCoSecurityContext> sessions = new HashMap<>();

  public void addSession(Session session) {
    sessions.put(session, new MaCoCoSecurityContext(false));
  }

  public void removeSession(Session session) {
    sessions.remove(session);
  }

  public MaCoCoSecurityContext getSecurityContext(Session session) {
    return sessions.get(session);
  }
}
