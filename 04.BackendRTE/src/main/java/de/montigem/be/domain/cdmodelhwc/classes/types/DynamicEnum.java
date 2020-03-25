/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.cdmodelhwc.classes.types;

import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DomainHelper;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
/**
 * Base class for adaptable enums
 *
 */
@Entity
public class DynamicEnum implements IObject {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  @Enumerated(EnumType.STRING)
  private DynamicEnumType name;

  @ElementCollection
  private Collection<String> types;

  /**
   * private constructor for JPA. DO NOT INVOKE MANUALLY!!!
   */
  protected DynamicEnum() {
    this.types = new HashSet<>();
  }

  /**
   * Create an enum with the given name
   *
   * @param name
   */
  public DynamicEnum(DynamicEnumType name) {
    this.types = new HashSet<>();
    this.name = name;
  }

  /**
   * @return
   */
  public Set<String> getAll() {
    return DomainHelper.getSet(types);
  }

  /**
   * @param type
   * @return
   */
  public boolean contains(String type) {
    return types.contains(type);
  }

  /**
   * @param types
   */
  public void set(Set<String> types) {
    this.types = types;
  }

  /**
   * @param type
   * @return
   */
  public boolean add(String type) {
    return types.add(type);
  }

  /**
   * @param type
   * @return
   */
  public boolean remove(String type) {
    return types.remove(type);
  }

  /**
   * @return name
   */
  public DynamicEnumType getName() {
    return this.name;
  }

  /**
   * @see de.montigem.be.domain.rte.interfaces.IObject#getId()
   */
  @Override
  public long getId() {
    return this.id;
  }

  /**
   * @see de.montigem.be.domain.rte.interfaces.IObject#merge(de.montigem.be.domain.rte.interfaces.IObject)
   */
  @Override
  public void merge(IObject nv) {
    if (nv instanceof DynamicEnum) {
      DynamicEnum newValues = (DynamicEnum) nv;
      set(newValues.getAll());
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public void mergeWithoutAssociations(IObject newValues) {
    merge(newValues);
  }
}
