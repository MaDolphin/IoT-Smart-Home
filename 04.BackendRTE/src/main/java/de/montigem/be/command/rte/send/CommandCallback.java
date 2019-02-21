package de.montigem.be.command.rte.send;

public interface CommandCallback<Response> {
  void onOk(Response response);
  void onError(Exception e);
}
