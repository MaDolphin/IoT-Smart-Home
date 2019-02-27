/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.dtos.rte;

public class NotImplementedDTO extends DTO {

  private String msg;

  protected NotImplementedDTO() {
    super("NotImplementedDTO");
  }

  public NotImplementedDTO(String msg) {
    this();
    this.msg = msg;
  }
}
