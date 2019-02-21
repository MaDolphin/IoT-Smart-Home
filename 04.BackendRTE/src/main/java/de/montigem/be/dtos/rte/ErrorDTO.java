package de.montigem.be.dtos.rte;

import de.montigem.be.error.MaCoCoError;
import de.montigem.be.error.MaCoCoErrorCode;
import de.montigem.be.error.MaCoCoErrorFactory;

/**
 * predefined DTO for errors
 * <p>
 * use when any error occured and a valid aggregate cannot be returned
 */
public class ErrorDTO extends DTO {

  private String errorCode;

  private MaCoCoError error;

  protected ErrorDTO() {
    super("ErrorDTO");
  }

  public ErrorDTO(String errorCode, Exception e) {
    this();
    this.errorCode = errorCode;
    this.error = MaCoCoErrorFactory.exceptionCaught(e);
  }

  public ErrorDTO(MaCoCoError e) {
    this();
    this.errorCode = e.getErrorCode().getCode();
    this.error = e;
  }

  public ErrorDTO(String errorCode, MaCoCoError error) {
    this();
    this.errorCode = errorCode;
    this.error = error;
  }

  public ErrorDTO(MaCoCoErrorCode errorCode, String msg) {
    this();
    this.errorCode = errorCode.getCode();
    this.error = MaCoCoErrorFactory.unknown(msg);
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

  public MaCoCoError getError() {
    return error;
  }
}
