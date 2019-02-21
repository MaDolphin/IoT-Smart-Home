package de.montigem.be.dtos.rte;

public class IdDTO extends DTO {

  protected IdDTO() {
    super("IdDTO");
  }

  public IdDTO(long id) {
    this();
    this.id = id;
  }
}
