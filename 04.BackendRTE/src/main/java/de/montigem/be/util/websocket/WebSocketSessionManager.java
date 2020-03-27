/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util.websocket;

import de.montigem.be.service.UsageList;
import de.se_rwth.commons.logging.Log;

import javax.websocket.Session;
import java.util.*;
import java.util.stream.Collectors;

/**
 * storage for all websocket sessions
 */
public class WebSocketSessionManager {
  private static WebSocketSessionManager INSTANCE;

  // Collection of all connected clients
  private Map<String, List<SessionContainer>> sessionList;

  public static WebSocketSessionManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new WebSocketSessionManager();
    }
    return INSTANCE;
  }

  public WebSocketSessionManager() {
    sessionList = new HashMap<>();
  }

  /**
   * add a session to the list
   * @param session
   * @param resource
   * @param userHash
   */
  public void addSession(Session session, String resource, String userHash) {
    sessionList.getOrDefault(resource, new ArrayList<>()).add(new SessionContainer(userHash, session));
    Log.debug("New websocket session (" + session.getId() + ") in " + resource + " has been added", getClass().getName());
  }

  /**
   * add a session to the list
   * @param session
   * @param resource
   * @param userHash
   */
  public void addSession(Session session, String resource, String userHash, MessageHandler messageHandler) {
    sessionList.putIfAbsent(resource, new ArrayList<>());
    sessionList.get(resource).add(new SessionContainer(userHash, session, messageHandler));
    Log.debug("New websocket session (" + session.getId() + ") in " + resource + " has been added", getClass().getName());
  }

  public void removeSession(Session session) {
    for (String resource : sessionList.keySet()) {
      removeSession(session, resource);
    }
  }

  public void removeSession(Session session, String resource) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      sessions.removeIf(s -> s.equalsSession(session));
      Log.debug("Websocket session (" + session.getId() + ") from " + resource + " has been removed", getClass().getName());
    }
  }

  public void removeSessionsForUser(String resource, String userHash) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      Log.debug("Remove all sessions for user with hash " + userHash + " in " + resource, getClass().getName());

      ListIterator<SessionContainer> iter = sessions.listIterator();
      while (iter.hasNext()) {
        SessionContainer sc = iter.next();
        if (sc.getUserHash().equals(userHash)) {
          Log.debug("Websocket session (" + sc.getSession().getId() + ") from " + resource + " has been removed", getClass().getName());
          iter.remove();
        }
      }
    }
  }

  public Optional<SessionContainer> findSession(Session session) {
    for (String resource : sessionList.keySet()) {
      Optional<SessionContainer> s = findSession(session, resource);
      if (s.isPresent()) {
        return s;
      }
    }
    return Optional.empty();
  }

  public Optional<SessionContainer> findSession(Session session, String resource) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      return sessions.stream().filter(s -> s.getSession() == session).findAny();
    }

    return Optional.empty();
  }

  public Optional<String> handleMessage(Session session, String resource, String msg) {
    Optional<SessionContainer> s = findSession(session, resource);
    return s.map(o -> o.handleMessage(msg));
  }

  public List<SessionContainer> getSessions(String resource) {
    return getSessions(resource, null);
  }

  public List<SessionContainer> getSessions(String resource, String usage) {
    List<SessionContainer> sessionList = this.sessionList.getOrDefault(resource, Collections.emptyList());
    if (usage != null && !usage.isEmpty()) {
      sessionList = sessionList.stream().filter(s ->
          {
            UsageList usageList;
            try {
              usageList = (UsageList) s.getSession().getUserProperties().getOrDefault("usage", Collections.singletonList("All"));
            } catch (ClassCastException ex) {
              usageList = UsageList.all();
            }
            return usageList.contains("All") || usageList.contains(usage);
          }
      ).collect(Collectors.toList());
    }
    return sessionList;
  }

  public void sendToSessions(String resource, String msg) {
    sendToSessions(resource, null, msg);
  }

  public void sendToSessions(String resource, String usage, String msg) {
    getSessions(resource, usage).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }
}
