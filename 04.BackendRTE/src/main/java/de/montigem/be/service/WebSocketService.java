/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.service;

import de.montigem.be.auth.jwt.JWToken;
import de.montigem.be.auth.jwt.ShiroJWTFilter;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.rte.general.CommandManager;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.daos.SensorDAO;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.SensorHandler;
import de.montigem.be.util.websocket.MessageHandler;
import de.montigem.be.util.websocket.MessageHandlerType;
import de.montigem.be.util.websocket.WebSocketSessionManager;
import de.se_rwth.commons.logging.Log;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.crypto.hash.Sha1Hash;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;

/**
 * example usage
 * ws://localhost:8080/montigem-be/websocket/<JWT>/CommandManager
 */
@Singleton
@ServerEndpoint(value = "/websocket/{token}/{messageHandler}/{usage}")
public class WebSocketService {
  @Inject
  private CommandManager commandManager;

  @Inject
  private DAOLib daoLib;

  @Inject
  private RolePermissionManager rolePermissionManager;

  private final int sessionMaxTimeout = 3_600_000; // 1 hour

  // if the session should be closed on any error
  private boolean shouldCloseOnError = false;

  /**
   * gets called when a new connection is opened
   *
   * @param session
   * @param token
   * @param messageHandler
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("token") String token, @PathParam("messageHandler") String messageHandler, @PathParam("usage") String usage) {
    JWToken jwt = new JWToken(token, ShiroJWTFilter.getUsernameFromToken(token),
            ShiroJWTFilter.getServerInstanceFromToken(token));

    boolean hasError = false;
    try {
      // try to validate given token
      ShiroJWTFilter.validateToken(token);
    }
    catch (ClaimJwtException | SignatureException e) {
      // any error in the jwt (wrong signature, ...)
      Log.debug("MAB0x32F6: Claim not valid", e, getClass().getName());
      hasError = true;
    }

    if (hasError) {
      try {
        if (session.isOpen()) {
          // close the session immediately on wrong token
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "wrong token"));
        }
      }
      catch (IOException e) {
        Log.debug("MAB0x32F7: Couldn't close session: " + e, getClass().getName());
        return;
      }
    }

    // add the jwt to the session
    session.getUserProperties().put("jwt", jwt);
    session.getUserProperties().put("usage", UsageList.valueOf(usage));
    session.setMaxIdleTimeout(sessionMaxTimeout);

    MessageHandler callback;

    // create the callback dependent on the chosen messageHandler
    MessageHandlerType messageHandlerType = getMessageHandlerType(messageHandler);
    switch (messageHandlerType) {
      case CommandManager:
        callback = msg -> JsonMarshal.getInstance().marshal(commandManager.manage(msg));
        break;
      case Sensor:
        SensorHandler.getInstance().add(session, SensorType.valueOf(usage));
        // + do the same as default
      case None:
      case Add: //If the websocket client wants to add sensorData in JSON format - the usage field has no effect
        callback = msg -> daoLib.getSensorDAO().parseSensorValue(msg);
        break;
      default:
        callback = msg -> msg;
        break;
    }

    // add the session to the session list
    WebSocketSessionManager.getInstance().addSession(session, getResource(session), getUserHash(session), callback);
  }

  private MessageHandlerType getMessageHandlerType(String messageHandler) {
    try {
      return MessageHandlerType.valueOf(messageHandler);
    }
    catch (IllegalArgumentException _e) {
      return MessageHandlerType.None;
    }
  }

  /**
   * gets called when either side is closing the session
   *
   * @param session
   */
  @OnClose
  public void onClose(Session session) {
    WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
  }

  /**
   * gets called if any error occured
   *
   * @param session
   * @param throwable
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    if (isJWTError(throwable)) {
      try {
        // close the session immediately on wrong token
        Log.info("Error in session " + session.getId(), getClass().getName());
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "wrong token"));
        }
      }
      catch (IOException e) {
        Log.debug("MAB0x32F7: Couldn't close session: " + e, getClass().getName());
        return;
      }
    } else {
      Log.info("Error in session " + session.getId() + " of " + getResource(session) + ": " + throwable.toString(), getClass().getName());
    }

    if (isClosedByClient(throwable)) {
      WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
    }
    else {
      if (shouldCloseOnError) {
        WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
        try {
          if (session.isOpen()) {
            session.close(new CloseReason(
                    CloseReason.CloseCodes.CLOSED_ABNORMALLY, "An exception occured: " + throwable.toString())
            );
          }
        }
        catch (IOException e) {
          Log.warn("Couldn't close session " + session.getId() + ": " + e.toString());
        }
      }
    }
  }

  /**
   * check if there is an error with the jwt
   * @param throwable
   * @return
   */
  private boolean isJWTError(Throwable throwable) {
    return throwable instanceof SignatureException || throwable instanceof ClaimJwtException || throwable instanceof NullPointerException;
  }

  /**
   * Check if the client closed the session,
   * based on a given Throwable
   * for example destroyed the session
   *
   * @param throwable
   * @return
   */
  private boolean isClosedByClient(Throwable throwable) {
    int count = 0;
    Throwable root = throwable;
    // find the root cause (max 20 recursive calls)
    while (root.getCause() != null && count < 20 && root.getCause() != root) {
      root = root.getCause();
      count++;
    }
    return root instanceof EOFException;
  }

  /**
   * gets called when a message is received
   *
   * @param msg
   * @param session contains the previous created session
   */
  @OnMessage
  public void onMessage(String msg, Session session) {
    Log.trace("Incoming message in session " + session.getId() + ": " + msg, getClass().getName());

    // set current session for thread
    try {
      setUserSession(session);
    }
    catch (NamingException | UnavailableSecurityManagerException e) {
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "An exception occured: " + e.toString()));
        }
      }
      catch (IOException ex) {
        Log.warn("Couldn't close session " + session.getId() + ": " + e.toString());
      }
    }

    // call the provided message handler
    WebSocketSessionManager.getInstance().handleMessage(session, getResource(session), msg).ifPresent(result -> {
      session.getAsyncRemote().sendText(result);
    });
  }

  /**
   * create the environment for a authenticated user
   *
   * @param session
   */
  private void setUserSession(Session session) throws NamingException, UnavailableSecurityManagerException {
    JWToken jwt = getJWT(session);

    SecurityHelper.createSubjectAndLogin(jwt.getUsername(), jwt.getResource(), daoLib, rolePermissionManager);
  }

  /**
   * get the stored jwt for connected user
   *
   * @param session
   * @return
   */
  public static JWToken getJWT(Session session) {
    return (JWToken) session.getUserProperties().get("jwt");
  }

  /**
   * get resource/instance for given session
   *
   * @param session
   * @return
   */
  private String getResource(Session session) {
    return getJWT(session).getResource();
  }

  /**
   * @param session
   * @return
   */
  private String getUsername(Session session) {
    return getResource(session) + getJWT(session).getUsername();
  }

  /**
   * get the hash of the username, e.g. for logging
   *
   * @param session
   * @return
   */
  private String getUserHash(Session session) {
    return new Sha1Hash(getUsername(session)).toString();
  }
}
