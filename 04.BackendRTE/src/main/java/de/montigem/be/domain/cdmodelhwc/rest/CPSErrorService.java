package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.gson.*;
import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.domain.cdmodelhwc.classes.emailrecipients.EmailRecipients;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.domain.cdmodelhwc.classes.cpserror.CPSError;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Stateless
@Path("/domain/receive-json")
@Produces(MediaType.APPLICATION_JSON)
@Interceptors(APIExceptionInterceptor.class)
public class CPSErrorService {

    @Inject
    private DAOLib daoLib;

    @Inject
    private UserActivationManager activationManager;

    /**
     * @Description: retrieving CPSError messages
     * POST http://localhost:8080/montigem-be/api/domain/receive-json/cppserror
     * Example POST-request body as json:
     * {"id":1, "code":"errcode", "msg":"cpserr", "timeStamp":"01-01-2020 08:00:00", "recipients":"email1@mail.tld; email2@mail.tld; email3@mail.tld"}
     * @Param: [json]
     * @return: javax.ws.rs.core.Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/cppserror")
    public Response receiveError(String json){
        try {
            JsonElement element = new Gson().fromJson(json, JsonElement.class);

            Optional<String> sensorId = Optional.of(element.getAsJsonObject().get("id").getAsString());
            String errCode = element.getAsJsonObject().get("code").getAsString();
            String errMsg = element.getAsJsonObject().get("msg").getAsString();
            String timeStamp = element.getAsJsonObject().get("timeStamp").getAsString();
            String email = element.getAsJsonObject().get("recipients").getAsString();
            email = email.replaceAll("\\s+","");
            String[] emailArray = email.split(";");
            ArrayList<String> recipients = new ArrayList<>();
            Collections.addAll(recipients, emailArray);
            String source = getClass().getName();

            CPSError error = new CPSError();
            error.rawInitAttrs(null, sensorId, errCode, errMsg, timeStamp, source, recipients);

            EntityManager em = daoLib.getCPSErrorDAO().getEntityManager();
            em.persist(error);

            EmailRecipients emailRecipients = new EmailRecipients();
            emailRecipients.setRecipients(recipients);

            em = daoLib.getEmailRecipientsDAO().getEntityManager();
            em.persist(emailRecipients);

            activationManager.sendMail(error.toString(), recipients, "CPSError");

            return Responses.okResponse(error.toString());
        } catch (JsonParseException | MessagingException e) {
                Log.warn("CPSErrorService: incoming json is not parseable");
                Log.trace(json, "CPSErrorService");
        }

        return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
    }

}
