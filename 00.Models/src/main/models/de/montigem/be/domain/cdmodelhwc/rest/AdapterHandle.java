/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.rest;

import de.montigem.be.domain.cdmodelhwc.classes.adapter.Adapter;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DAOLib;

import java.util.Optional;

/**
 * Default behavior for Adapter relations. Provided methods are meant to be
 * replaced by subclass, and the handler of AdapterService reassigned. In a
 * future version the handler system may be replaced by the TOP mechanism.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 * @see de.montigem.be.domain.cdmodelhwc.rest.AdapterService
 */
public class AdapterHandle {
	/**
	 * Called if an Adapter entity is recreated. The given adapter was already
	 * deleted from the database. The associated Entities are ignored. The returned
	 * Object will be assigned to the replacing Adapter. Returning empty enables
	 * default AdapterFactory behavior. By default classes required for Adapter
	 * creation will be created by the AdapterFactory.
	 *
	 * @daoLib daoLibrary
	 * @adapter removed adapter from the database
	 * @resource database name
	 * @return empty
	 */
	public Optional<IObject> handleAdapterChange(DAOLib daoLib, Adapter adapter, String resource) {
		return Optional.empty();
	}

	/**
	 * Called if an Adapter entity is created. The returned Object will be assigned
	 * to the new Adapter. Returning empty enables default AdapterFactory behavior.
	 * By default classes required for Adapter creation will be created by the
	 * AdapterFactory.
	 *
	 * @daoLib daoLibrary
	 * @port The unique port name of the component-based system requiring a new
	 *       Adapter
	 * @identifier The unique name of the Adapter, that will be created
	 * @group The unique group to find the identifying class for the Adapter
	 * @resource database name
	 * @return empty
	 */
	public Optional<IObject> handleAdapterCreation(DAOLib daoLib, String port, String identifier, String group,
			String resource) {
		return Optional.empty();
	}
}
