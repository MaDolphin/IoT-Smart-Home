/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.rest;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.config.Config;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Stateless
@Path("/domain/form")
@Produces("application/json")
@Interceptors(APIExceptionInterceptor.class)
public class FormService {

  @OPTIONS
  @Path("{id}")
  public Response preflight() {
    return Responses.okResponse();
  }

  @Inject
  private DAOLib daolib;

  @Inject
  private SecurityHelper securityHelper;

  protected EntityManager em;

  @PersistenceContext(unitName = Config.DOMAIN_DB)
  public void setEntityManager(EntityManager entityManager) {
    this.em = entityManager;
    em.setFlushMode(FlushModeType.AUTO);
  }

  public EntityManager getEntityManager() {
    return this.em;
  }


  @GET
  @Path("/pdf/{filename}")
  @Produces("application/pdf")
  public Response getPDF(@PathParam("filename") String filename) {
    Log.info("Getting PDF...", getClass().getName());
    File file = new File(filename);
    Response.ResponseBuilder responseBuilder = null;
    try {
      responseBuilder = Response.ok(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      Log.warn("MAB0x5013: " + getClass().getName(), e);
      return Response.serverError().build();
    }
    responseBuilder.type("application/pdf");
    responseBuilder.header("Content-Disposition", "filename=" + filename);
    Log.info("Successful got PDF...", getClass().getName());
    return responseBuilder.build();
  }

}
