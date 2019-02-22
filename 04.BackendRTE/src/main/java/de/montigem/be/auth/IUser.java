package de.montigem.be.auth;

import de.montigem.be.domain.rte.interfaces.IObject;

public interface IUser extends IObject {

  String getUsername();
  String getEncodedPassword();
  String getPasswordSaltBase64();
}
