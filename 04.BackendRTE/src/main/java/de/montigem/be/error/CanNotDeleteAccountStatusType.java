/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.error;

import javax.ws.rs.core.Response;

/**
 * Custom status code for external accounts.
 */
public class CanNotDeleteAccountStatusType implements Response.StatusType {
  @Override public int getStatusCode() {
    return 420;
  }

  @Override public Response.Status.Family getFamily() {
    return Response.Status.Family.CLIENT_ERROR;
  }

  @Override public String getReasonPhrase() {
    return "Account is used and can not be deleted.";
  }
}
