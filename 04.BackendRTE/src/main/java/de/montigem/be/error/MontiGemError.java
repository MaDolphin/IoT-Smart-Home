/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.error;

import de.montigem.be.config.Config;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MontiGemError extends RuntimeException {

  private MontiGemErrorCode errorCode;

  private Response.StatusType httpStatusCode;

  private String description;

  /**
   * Constructor for MontiGemError
   *
   * @param errorCode
   * @param httpStatusCode
   * @param description
   */
  public MontiGemError(MontiGemErrorCode errorCode, Response.StatusType httpStatusCode, String description) {
    super();
    this.errorCode = errorCode;
    this.httpStatusCode = httpStatusCode;
    this.description = description;
  }

  /**
   * @return errorCode
   */
  public MontiGemErrorCode getErrorCode() {
    return this.errorCode;
  }

  /**
   * @param errorCode the errorCode to set
   */
  public void setErrorCode(MontiGemErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * @return httpStatusCode
   */
  public Response.StatusType getHttpStatusCode() {
    return this.httpStatusCode;
  }

  /**
   * @param httpStatusCode the httpStatusCode to set
   */
  public void setHttpStatusCode(Status httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String toString(boolean developerMode) {
    StringBuilder sb = new StringBuilder();
    sb.append(errorCode.getCode());
    if (developerMode) {
      sb.append(": ");
      sb.append(description);
      sb.append("! (HTTP Code ");
      sb.append(httpStatusCode);
      sb.append(")");
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toString(Config.DEVELOPER_MODE);
  }

  public JsonObject toJson() {
    return Json.createObjectBuilder()
        .add("errorCode", getErrorCode().getCode())
        .add("message", getDescription())
        .build();
  }

  public String toJsonString() {
    return toJson().toString();
  }

}
