/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.domain.rte;

import java.io.Serializable;

/**
 * The identifier for all persistable classes of the MaCoCo Backend. It
 * comprises an id and a revision. The combination of both should be globally
 * unique.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class Identifier implements Serializable {
  
  /**
   * TODO: Write me!
   */
  private static final long serialVersionUID = -8543674261682652645L;
  
  private long revision;
  
  private long id;
  
  /**
   * Only to be used by JPA!
   */
  public Identifier() {
  }
  
  /**
   * Constructor for de.macoco.be.Identifier
   *
   * @param revision
   * @param id
   */
  public Identifier(long id, long revision) {
    super();
    this.revision = revision;
    this.id = id;
  }
  
  /**
   * @return revision
   */
  public long getRevision() {
    return this.revision;
  }
  
  /**
   * @return id
   */
  public long getId() {
    return this.id;
  }
  
  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (this.id ^ (this.id >>> 32));
    result = prime * result + (int) (this.revision ^ (this.revision >>> 32));
    return result;
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Identifier other = (Identifier) obj;
    if (this.id != other.id)
      return false;
    if (this.revision != other.revision)
      return false;
    return true;
  }
  
}
