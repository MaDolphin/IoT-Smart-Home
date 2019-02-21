
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.dtos;

import de.montigem.be.dtos.rte.DTO;

public class StringContainerDTO
    
    extends DTO

{
  
  protected String value = "";
  
  public StringContainerDTO()
  
  {
    this.typeName = "StringContainerDTO";
  }
  
  public StringContainerDTO(long id, String value)
  
  {
    super();
    this.typeName = "StringContainerDTO";
    this.setId(id);
    this.setValue(value);
  }
  
  public void setValue(String o)
  
  {
    
    this.value = o;
  }
  
  public String getValue()
  
  {
    return this.value;
  }
  
  public String toString()
  
  {
    StringBuilder builder = new StringBuilder();
    builder.append("StringContainerDTO [");
    builder.append(" value=");
    builder.append(this.value);
    builder.append("]");
    
    return builder.toString();
  }
  
}
