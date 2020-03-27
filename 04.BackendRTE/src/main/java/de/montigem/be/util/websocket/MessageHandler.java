/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util.websocket;

@FunctionalInterface
public interface MessageHandler {
  String handleMessage(String msg);
}
