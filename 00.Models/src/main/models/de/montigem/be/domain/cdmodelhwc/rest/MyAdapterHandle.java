package de.montigem.be.domain.cdmodelhwc.rest;

import de.montigem.be.domain.cdmodelhwc.classes.adapter.Adapter;
import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.SirenCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensor.SmokeSensor;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensor.TempSensor;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DAOLib;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Overrides standard AdapterHandle methods.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 * @see de.montigem.be.domain.cdmodelhwc.rest.AdapterHandle
 */
public class MyAdapterHandle extends AdapterHandle {

	/**
	 * Enables default AdapterFactory behavior by returning empty. By default
	 * classes required for Adapter creation will be created by the AdapterFactory.
	 * The AttributeClass associated to the Adapter is removed from the database,
	 * since they will be automatically recreated by the AdapterFactory.
	 *
	 *
	 * @param daoLib
	 *            daoLibrary
	 * @param adapter
	 *            removed adapter from the database
	 * @param resource
	 *            database name
	 * @return empty
	 */
	@Override
	@Transactional
	public Optional<IObject> handleAdapterChange(DAOLib daoLib, Adapter adapter, String resource) {
		IObject iObject = adapter.getAttributeClass();
		List<AlarmCtrl> alarmCtrl = daoLib.getAlarmCtrlDAO().getAll(resource);
		// remove association and delete attributeClass
		if (iObject instanceof TempSensor) {
			iObject = daoLib.getTempSensorDAO().getEntityManager().find(iObject.getClass(), iObject.getId());
			for (AlarmCtrl a : alarmCtrl) {
				a.removeTempSensor((TempSensor) iObject);
			}
			daoLib.getTempSensorDAO().getEntityManager().remove(iObject);

		} else if (iObject instanceof SmokeSensor) {
			iObject = daoLib.getSmokeSensorDAO().getEntityManager().find(iObject.getClass(), iObject.getId());
			for (AlarmCtrl a : alarmCtrl) {
				a.rawRemoveSmokeSensor((SmokeSensor) iObject);
			}
			daoLib.getSmokeSensorDAO().getEntityManager().remove(iObject);
		} else if (iObject instanceof SirenCtrl) {
			iObject = daoLib.getSirenCtrlDAO().getEntityManager().find(iObject.getClass(), iObject.getId());
			for (AlarmCtrl a : alarmCtrl) {
				a.removeSirenCtrl((SirenCtrl) iObject);
			}
			daoLib.getSirenCtrlDAO().getEntityManager().remove(iObject);
		}
		return Optional.empty();
	}

	/**
	 * Enables default AdapterFactory behavior by returning empty. By default
	 * classes required for Adapter creation will be created by the AdapterFactory.
	 *
	 * @param daoLib
	 *            daoLibrary
	 * @param port
	 *            The port name of the component-based system requiring a new
	 *            Adapter
	 * @param identifier
	 *            The unique name of the Adapter, that will be created
	 * @param group
	 *            The unique group to find the identifying class for the Adapter
	 * @param resource
	 *            database name
	 * @return empty
	 */
	@Override
	public Optional<IObject> handleAdapterCreation(DAOLib daoLib, String port, String identifier, String group,
			String resource) {
		return Optional.empty();
	}
}
