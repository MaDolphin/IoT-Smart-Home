
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.dtos;

import de.montigem.be.dtos.rte.DTO;

public class LongValueDTO
    
    extends DTO

{
  
  protected long value = -1L;
  
  public LongValueDTO()
  
  {
    this.typeName = "LongValueDTO";
  }
  
  public LongValueDTO(long id, long value)
  
  {
    super();
    this.typeName = "LongValueDTO";
    this.setId(id);
    this.setValue(value);
  }
  
  public void setValue(long o)
  
  {
    
    this.value = o;
  }
  
  public long getValue()
  
  {
    return this.value;
  }
  
  public String toString()
  
  {
    StringBuilder builder = new StringBuilder();
    builder.append("LongValueDTO [");
    builder.append(" value=");
    builder.append(this.value);
    builder.append("]");
    
    return builder.toString();
  }
  
}
