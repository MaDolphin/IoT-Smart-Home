/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.domain.rte.rest;

import com.google.common.base.Throwables;
import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnum;
import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnumType;
import de.montigem.be.domain.rte.dao.DynamicEnumDAO;
import de.montigem.be.error.JsonException;
import de.montigem.be.error.MaCoCoErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.Responses;
import de.montigem.be.util.SubListHelper;
import de.se_rwth.commons.logging.Log;

import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class DynamicEnumRestUtil {
  
  private DynamicEnumRestUtil() {
    // intentionally left empty
  }

  public static Response removeType(String toRemove, DynamicEnumType type, DynamicEnumDAO dynamDAO, String resource) {
    if (null == toRemove || "".equals(toRemove)) {
      Log.debug("To be removed dynamic type is empty or null", DynamicEnumRestUtil.class.getName());
      return Responses.error(MaCoCoErrorFactory.resolverError(toRemove), DynamicEnumRestUtil.class);
    }
    DynamicEnum oldObject = getByName(type, dynamDAO, resource);
    if (null == oldObject) {
      Log.debug("Couldnt find dynamic type " + type, DynamicEnumRestUtil.class.getName());
      return Responses.error(MaCoCoErrorFactory.resolverError(type), DynamicEnumRestUtil.class);
    }
    if (!oldObject.contains(toRemove)) {
      oldObject.remove(toRemove);
      dynamDAO.update(oldObject, resource);
    }
    return Responses.okResponse();
  }

  public static Response addType(String toAdd, DynamicEnumType type, DynamicEnumDAO dynamDAO, String resource) {
    try {
      String obj = JsonMarshal.getInstance()
          .unmarshal(new StringBuilder("\"").append(toAdd).append("\"").toString(), String.class);
      if (null == obj || "".equals(obj)) {
        Log.debug("To be added dynamic type is empty or null", DynamicEnumRestUtil.class.getName());
        return Responses.error(MaCoCoErrorFactory.resolverError(toAdd), DynamicEnumRestUtil.class);
      }
      DynamicEnum oldObject = getByName(type, dynamDAO, resource);
      if (null == oldObject) {
        Log.debug("Couldnt find dynamic type " + type, DynamicEnumRestUtil.class.getName());
        return Responses.error(MaCoCoErrorFactory.resolverError(type), DynamicEnumRestUtil.class);
      }
      if (!oldObject.contains(obj)) {
        oldObject.add(toAdd);
        dynamDAO.update(oldObject, resource);
      }
      return Responses.okResponse();
    } catch (JsonException e) {
      Log.debug(Throwables.getStackTraceAsString(e), DynamicEnumRestUtil.class.getName());
      return Responses
          .error(MaCoCoErrorFactory.deserializeError(e.getMessage()), DynamicEnumRestUtil.class);
    }
  }

  public static Response getAllTypes(int first, int max, DynamicEnumType type, DynamicEnumDAO dynamDAO, String resource) {
    Set<String> list = dynamDAO.getByName(type, resource);
    return Responses.okResponse(SubListHelper.get(list, first, max));
  }

  public static DynamicEnum getByName(DynamicEnumType name, DynamicEnumDAO dynamDAO, String resource) {
    DynamicEnum result = null;
    for (DynamicEnum d : dynamDAO.getAll(resource)) {
      if (d.getName().equals(name)) {
        if (null != result) {
          Log.debug("dublicated dynamic enum " + name, DynamicEnumRestUtil.class.getName());
          return null;
        }
        result = d;
      }
    }
    return result;
  }
  
}
