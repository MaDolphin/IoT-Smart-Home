/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util.websocket;

import javax.websocket.Session;
import java.util.Objects;

public class SessionContainer {
  private String userHash;
  private Session session;
  private MessageHandler messageHandler;

  public SessionContainer(String userHash, Session session) {
    this.userHash = userHash;
    this.session = session;
    this.messageHandler = null;
  }

  public SessionContainer(String userHash, Session session, MessageHandler messageHandler) {
    this.userHash = userHash;
    this.session = session;
    this.messageHandler = messageHandler;
  }

  public String getUserHash() {
    return userHash;
  }

  public void setUserHash(String userHash) {
    this.userHash = userHash;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public MessageHandler getMessageHandler() {
    return messageHandler;
  }

  public void setMessageHandler(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionContainer that = (SessionContainer) o;
    return userHash == that.userHash &&
        session.equals(that.session);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userHash, session);
  }

  public boolean equalsSession(Session s) {
    if (s == null || this.session == null) {
      return false;
    }
    return session.equals(s);
  }

  public String handleMessage(String msg) {
    if (messageHandler != null) {
      return messageHandler.handleMessage(msg);
    }
    return null;
  }

}

