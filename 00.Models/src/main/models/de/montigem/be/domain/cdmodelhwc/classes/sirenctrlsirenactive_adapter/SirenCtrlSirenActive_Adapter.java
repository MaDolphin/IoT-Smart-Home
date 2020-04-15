/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.classes.sirenctrlsirenactive_adapter;

import de.montigem.be.domain.rte.interfaces.IObject;
import org.hibernate.envers.Audited;

import de.montigem.be.error.JsonException;
import de.montigem.be.marshalling.JsonMarshal;
import javax.persistence.*;
import static org.apache.webbeans.util.Asserts.assertNotNull;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.SirenCtrl;

/**
 * Entity for writing and reading the attribute SirenCtrl.sirenActive of the
 * associated entity.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class SirenCtrlSirenActive_Adapter extends SirenCtrlSirenActive_AdapterTOP {

	/** class uses auto identification */
	public static final boolean AUTOID = false;

	/** class name and attribute name that is accessed of the associated entity */
	public static final String attribute = "SirenCtrl.sirenActive";

	/** Empty constructor. Do not use, but exists for compatibility reasons. */
	public SirenCtrlSirenActive_Adapter() {
	}

	/**
	 * Constructor for assigning unique identifier, group and associated attribute
	 * entity.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 * @sirenCtrl entity that is used for attribute access
	 */
	public SirenCtrlSirenActive_Adapter(String identifier, String group, SirenCtrl sirenCtrl) {
		this(identifier, group);
		assertNotNull(sirenCtrl);
		setSirenCtrl(sirenCtrl);
	}

	/**
	 * Constructor for assigning unique identifier and group.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 */
	public SirenCtrlSirenActive_Adapter(String identifier, String group) {
		assertNotNull(identifier);
		assertNotNull(group);
		this.identifier = identifier;
		this.groupName = group;
		super.attributeName = SirenCtrlSirenActive_Adapter.attribute;
	}

	@Override
	public String getGroupName() {
		return getAlarmCtrl().getSerial();
	}

	@Override
	public boolean setJson(String json) {
		if (!getAllowWrite()) {
			return false;
		}
		try {
			setData(JsonMarshal.getInstance().unmarshal(json, Data.class).data);
			return true;
		} catch (JsonException e) {
			return false;
		}
	}

	@Override
	public String getJson() {
		if (!getAllowRead()) {
			return null;
		}
		return JsonMarshal.getInstance().marshal(new Data(getData()));
	}

	/**
	 * Gets sirenActive from the associated sirenCtrl.
	 *
	 * @return d(param.attr)» value
	 */
	public boolean getData() {
		return getSirenCtrl().isSirenActive();
	}

	/**
	 * Sets sirenActive in associated sirenCtrl.
	 *
	 * @data value to set for d(param.attr)»
	 */
	public void setData(boolean data) {
		getSirenCtrl().setSirenActive(data);
	}

	@Override
	public IObject getAttributeClass() {
		return getSirenCtrl();
	}

	@Override
	public void removeAssociations() {
		setSirenCtrl(null);
		setAlarmCtrl(null);
	}

	/**
	 * Object for easy JSON Object creation and value retrieval.
	 */
	static class Data {
		public boolean data;

		public Data(boolean data) {
			this.data = data;
		}
	}
}
