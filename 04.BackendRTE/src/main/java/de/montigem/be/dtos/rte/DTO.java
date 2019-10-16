/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.dtos.rte;

import de.montigem.be.error.MontiGemErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base interface for each data set
 */
public abstract class DTO {

  protected String typeName; // used for serialization, contains the typename of the specific class

  protected long id = -1;

  public DTO() {
    typeName = "UnknownType";
  }

  public DTO(String typeName) {
    this.typeName = typeName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<String> getLabels() {
    return new ArrayList<>();
  }

  public Optional<String> getData() {
    return Optional.empty();
  }

  public String getErrorCode() {
    return MontiGemErrorCode.OK.getCode();
  }

  public String getMessage() {
    return MontiGemErrorCode.OK.name();
  }
}
