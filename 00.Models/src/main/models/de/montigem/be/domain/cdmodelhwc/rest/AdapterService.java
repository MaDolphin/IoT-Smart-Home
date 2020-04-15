/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.rest;

import de.montigem.be.domain.cdmodelhwc.classes.adapter.AdapterFactory;
import de.montigem.be.domain.cdmodelhwc.classes.adapter.Adapter;
import de.montigem.be.domain.cdmodelhwc.daos.AdapterDAO;
import de.montigem.be.database.DatabaseDataSource;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.error.JsonException;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.Responses;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Services for Adapter.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Stateless
@Path("/domain/receive-json")
public class AdapterService {

	@Inject
	private DAOLib daoLib;

	/**
	 * Used for handling Adapters
	 */
	private static AdapterHandle handle = new AdapterHandle();

	/**
	 * Called by appropriat url GET request from a CP-system. Tries to get the
	 * requested data and on success responds with code 200 and a json containing
	 * the data. If something goes wrong, it responds with an appropriate code and
	 * error in json form. In certain cases like locks, code!=200 is normal and
	 * should be expected.
	 *
	 * @group group to identify data
	 * @port unique component identification in requesting system
	 * @id unique system identification
	 * @return responding with code 200 and json containing requested data if no
	 *         error occurs. Otherwise code!=200 and json containing error.
	 */
	@GET
	@Path("/{group}/{port}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response adapterGetData(@PathParam("group") String group, @PathParam("port") String port,
			@PathParam("id") String id) {
		String json;
		Adapter adapter;
		// TODO advanced error handling
		try {
			adapter = getAdapter(port, id, group);
		} catch (Exception e) {
			return Responses.userError(MontiGemErrorFactory.loadIDError(port + id), getClass());
		}
		if (adapter == null) {
			return Responses.userError(MontiGemErrorFactory.loadIDError(port + id), getClass());
		}
		json = adapter.getJson();
		if (json == null) {
			return Responses.userError(MontiGemErrorFactory.exceptionCaught(new JsonException("Failed to get Data")),
					getClass());
		}
		return Responses.okResponseJson(json);
	}

	/**
	 * Called by appropriat url POST request from a CP-system. Tries to save the
	 * received data from given json object and on success responds with code 200
	 * and an empty json. If something goes wrong, it responds with an appropriate
	 * code and error in json form. In certain cases like locks, code!=200 is normal
	 * and should be expected.
	 *
	 * @group group to identify data
	 * @port unique component identification in posting system
	 * @id unique system identification
	 * @return responding with code 200 and empty json if no error occures.
	 *         Otherwise code!=200 and json containing error.
	 */
	@POST
	@Path("/{group}/{port}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response adapterPostData(@PathParam("group") String group, @PathParam("port") String port,
			@PathParam("id") String id, String json) {
		boolean setJsonOk;
		Adapter adapter;
		try {
			adapter = getAdapter(port, id, group);
		} catch (Exception e) {
			return Responses.userError(MontiGemErrorFactory.loadIDError(port + id), getClass());
		}
		if (adapter == null) {
			return Responses.userError(MontiGemErrorFactory.loadIDError(port + id), getClass());
		}
		setJsonOk = adapter.setJson(json);
		if (!setJsonOk) {
			return Responses.userError(MontiGemErrorFactory.exceptionCaught(new JsonException("Failed to set Data")),
					getClass());
		}
		return Responses.okResponse();
	}

	/**
	 * Gets Adapter with given identification. If such an Adapter does not exist, it
	 * is created. The Adapter association can be handled. If the group has changed
	 * changeAdapter() is invoked and the result returned. On null input an
	 * Exception is thrown
	 *
	 * @group group of Adapter
	 * @port unique component identification in CP-system
	 * @systemID unique system identification
	 * @return Adapter with given identification
	 * @throws Exception
	 *             thrown if the session needs to be rolled back, or required
	 *             arguments are missing.
	 */
	public Adapter getAdapter(String port, String systemID, String group) throws Exception {
		// check arguments
		if (daoLib == null) {
			throw new Exception("DaoLib not Injected!");
		}
		if (port == null || group == null || systemID == null) {
			throw new Exception("Empty Field not Allowed!");
		}
		AdapterDAO dao = daoLib.getAdapterDAO();
		// create unique Adapter identifier
		String registeredId = port + systemID;
		// find adapter
		Optional<Adapter> adapterOpt;
		adapterOpt = dao.findByIdentifier(registeredId, "TestDB");
		// if adapter not exists, create it
		if (!adapterOpt.isPresent()) {
			return AdapterFactory.createAdapter(port, registeredId, group, daoLib, "TestDB",
					handle.handleAdapterCreation(daoLib, port, registeredId, group, "TestDB"));
		}
		// if the group has changed
		if (!adapterOpt.get().getGroup().equals(group)) {
			return changeAdapter(adapterOpt.get(), port, registeredId, group, daoLib, "TestDB");
		}
		return adapterOpt.get();
	}

	/**
	 * Disables the possibility for all Adapters to overwrite the specified
	 * attribute value.
	 *
	 * @classId id of the entity containing the attribute
	 * @attributeName simple attribute name
	 * @throws NamingException
	 *             thrown if the DAOLib could not be retrieved
	 */
	public static void lockWrite(long classId, String attributeName) throws NamingException {
		for (Adapter adapter : getAdaptersWithAttr(classId, attributeName)) {
			adapter.setAllowWrite(false);
		}
	}

	/**
	 * Disables the possibility for all Adapters to read the specified attribute
	 * value.
	 *
	 * @classId id of the entity containing the attribute
	 * @attributeName simple attribute name
	 * @throws NamingException
	 *             thrown if the DAOLib could not be retrieved
	 */
	public static void lockRead(long classId, String attributeName) throws NamingException {
		for (Adapter adapter : getAdaptersWithAttr(classId, attributeName)) {
			adapter.setAllowRead(false);
		}
	}

	/**
	 * Enables the possibility for all Adapters to overwrite the specified attribute
	 * value.
	 *
	 * @classId id of the entity containing the attribute
	 * @attributeName simple attribute name
	 * @throws NamingException
	 *             thrown if the DAOLib could not be retrieved
	 */
	public static void unlockWrite(long classId, String attributeName) throws NamingException {
		for (Adapter adapter : getAdaptersWithAttr(classId, attributeName)) {
			adapter.setAllowWrite(true);
		}
	}

	/**
	 * Enables the possibility for all Adapters to read the specified attribute
	 * value.
	 *
	 * @classId id of the entity containing the attribute
	 * @attributeName simple attribute name
	 * @throws NamingException
	 *             thrown if the DAOLib could not be retrieved
	 */
	public static void unlockRead(long classId, String attributeName) throws NamingException {
		for (Adapter adapter : getAdaptersWithAttr(classId, attributeName)) {
			adapter.setAllowRead(true);
		}
	}

	/**
	 * Retrieves all Adapters from the database that have access to the specified
	 * attribute.
	 *
	 * @classId id of the entity containing the attribute
	 * @attributeName simple attribute name
	 * @return Adapters accessing the specified attribute
	 * @throws NamingException
	 *             thrown if the DAOLib could not be retrieved
	 */
	private static List<Adapter> getAdaptersWithAttr(long classId, String attributeName) throws NamingException {

		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

		InitialContext context = new InitialContext(props);

		/*
		 * DatabaseDataSource databaseDataSource = (DatabaseDataSource) context
		 * .lookup("java:global/montigem-be/DatabaseDataSource");
		 */

		DAOLib daoLib = (DAOLib) context.lookup("java:global/montigem-be/DAOLib");

		AdapterDAO adapterDAO = daoLib.getAdapterDAO();
		return adapterDAO.findByAttribute(classId, attributeName, "TestDB");
	}

	/**
	 * Deletes given Adapter and recreates it with given values. The created Adapter
	 * is returned. The created Adapter association can be handled. On null input,
	 * null is returned.
	 *
	 * @adapter adapter that is replaced
	 * @port unique component identification in CP-system
	 * @identifier unique Adapter identification
	 * @group group to identify data
	 * @daoLib dao library
	 * @return replaced Adapter with given values
	 */
	@Transactional
	public Adapter changeAdapter(Adapter adapter, String port, String identifier, String group, DAOLib daoLib,
			String resource) {
		if (adapter == null || port == null || identifier == null || group == null || daoLib == null
				|| resource == null) {
			return null;
		}
		daoLib.getAdapterDAO().getEntityManager().flush();
		daoLib.getAdapterDAO().getEntityManager().detach(adapter);
		daoLib.getAdapterDAO().deleteById(adapter.getId(), resource);
		Optional<IObject> iObject = handle.handleAdapterChange(daoLib, adapter, resource);
		Adapter newAdapter = AdapterFactory.createAdapter(port, identifier, group, daoLib, resource, iObject);
		newAdapter.setAllowRead(adapter.getAllowRead());
		newAdapter.setAllowWrite(adapter.getAllowWrite());
		return newAdapter;
	}

	/**
	 * Gets handle.
	 * 
	 * @return handle
	 */
	public static AdapterHandle getHandle() {
		return handle;
	}

	/**
	 * Sets handle.
	 * 
	 * @handle set handler
	 */
	public static void setHandle(AdapterHandle handle) {
		AdapterService.handle = handle;
	}
}
