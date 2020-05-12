package de.montigem.be.domain.cdmodelhwc.rest;

import com.google.gson.*;
import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.APIExceptionInterceptor;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.domain.cdmodelhwc.classes.cppserror.CPPSError;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
@Path("/domain/receive-json")
@Produces(MediaType.APPLICATION_JSON)
@Interceptors(APIExceptionInterceptor.class)
public class CPPSErrorService {

    @Inject
    private DAOLib daoLib;

    @Inject
    private UserActivationManager activationManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/cppserror")
    public Response receiveError(String json){
        try {
            JsonElement element = new Gson().fromJson(json, JsonElement.class);

            Optional<String> sensorId = Optional.of(element.getAsJsonObject().get("id").getAsString());
            String errCode = element.getAsJsonObject().get("code").getAsString();
            String errMsg = element.getAsJsonObject().get("msg").getAsString();


            Timestamp curTime = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timeStamp = "{" + formatter.format(curTime) + "} ";

            String source = getClass().getName();


            ArrayList<String> recipients = new ArrayList<>();
            recipients.add("lukas.tim.zimmermann@rwth-aachen.de");

            CPPSError error = new CPPSError();
            error.rawInitAttrs(null, sensorId, errCode, errMsg, timeStamp, source, recipients);

            EntityManager em = daoLib.getCPPSErrorDAO().getEntityManager();
            em.persist(error);

            activationManager.sendMail(error.toString(), recipients, "CPPSError");

            return Responses.okResponse(error.toString());
        } catch (JsonParseException | MessagingException e) {
                Log.warn("CPPSErrorService: incoming json is not parseable");
                Log.trace(json, "CPPSErrorService");
        }

        return Responses.error(MontiGemErrorFactory.deserializeError(json), getClass());
    }

}
