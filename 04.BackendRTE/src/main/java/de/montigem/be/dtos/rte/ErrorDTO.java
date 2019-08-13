/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.dtos.rte;

import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;

/**
 * predefined DTO for errors
 * <p>
 * use when any error occured and a valid aggregate cannot be returned
 */
public class ErrorDTO extends DTO {

  private String errorCode;

  private MontiGemError error;

  protected ErrorDTO() {
    super("ErrorDTO");
  }

  public ErrorDTO(String errorCode, Exception e) {
    this();
    this.errorCode = errorCode;
    this.error = MontiGemErrorFactory.exceptionCaught(e);
  }

  public ErrorDTO(MontiGemError e) {
    this();
    this.errorCode = e.getErrorCode().getCode();
    this.error = e;
  }

  public ErrorDTO(String errorCode, MontiGemError error) {
    this();
    this.errorCode = errorCode;
    this.error = error;
  }

  public ErrorDTO(MontiGemErrorCode errorCode, String msg) {
    this();
    this.errorCode = errorCode.getCode();
    this.error = MontiGemErrorFactory.unknown(msg);
  }

  @Override
  public long getId() {
    return 0;
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getMessage() {
    return error.getMessage();
  }

  public MontiGemError getError() {
    return error;
  }
}
