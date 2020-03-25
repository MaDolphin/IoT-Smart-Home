/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.rte.dao;

import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnum;
import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnumType;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Write me!
 *
 */
@Stateless
@Lock(LockType.READ)
public class DynamicEnumDAO extends AbstractDomainDAO<DynamicEnum> {

  public Set<String> getByName(DynamicEnumType name, String resource) {
    DynamicEnum result = null;
    for (DynamicEnum d : getAll(resource)) {
      //Log.trace("dynamic enum: " + d.getName(), getClass().getName());
      if (d.getName().equals(name)) {
        if (null != result) {
          // TODO throw some error
          Log.error(getClass().getName() + " MAB0x0028: duplicated dynamic enum " + name);
          return new HashSet<>();
        }
        result = d;
      }
    }
    return result != null ? result.getAll() : new HashSet<>();
    // return result.getAll();
  }

  @Override
  public void removeAll(String resource) {

    router.setDataSource(resource);

    Query query = getEntityManager().createNativeQuery(""
        + "DELETE FROM DynamicEnum_types WHERE DynamicEnum_id != 0 ");
    query.executeUpdate();
    super.removeAll(resource);
  }

  /**
   * @see AbstractDomainDAO#getDomainClass()
   */
  @Override
  public Class<DynamicEnum> getDomainClass() {
    return DynamicEnum.class;
  }
}
