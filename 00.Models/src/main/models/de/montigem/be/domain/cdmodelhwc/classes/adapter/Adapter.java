/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.classes.adapter;

import de.montigem.be.domain.rte.interfaces.IObject;

import org.hibernate.envers.Audited;
import javax.persistence.*;

/**
 * Entity for writing and reading an attribute of the associated entity.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
abstract public class Adapter extends AdapterTOP {

	/**
	 * Unique Adapter identifier, but not the id.
	 */
	@Column(unique = true)
	protected String identifier;

	/**
	 * Name to identify the associated attribute class.
	 */
	protected String groupName;

	/**
	 * Name of the attribute accessed by the Adapter Name is consisting of the
	 * simple attribute class name + "." + simple attribute name.
	 */
	protected String attributeName;

	/**
	 * Overwriting the attribute value is allowed.
	 */
	protected boolean allowWrite = true;

	/**
	 * Reading the attribute value is allowed.
	 */
	protected boolean allowRead = true;

	/**
	 * Gets the correct group name. The name does not necessarily match the
	 * groupName attribute!
	 *
	 * @return name of the attribute identifying group.
	 */
	abstract public String getGroupName();

	/**
	 * Sets given value as the attribute value if allowWrite is true.
	 * 
	 * @json value to set, given as JSON object "data" field.
	 * @return If value is successfully set, true is returned. Otherwise false.
	 */
	abstract public boolean setJson(String json);

	/**
	 * Gets the attribute value if allowRead is true.
	 * 
	 * @return If value is successfully retrieved, a JSON object with the value in
	 *         the "data" field is returned. Otherwise null is returned.
	 */
	abstract public String getJson();

	/**
	 * Removes connection to associated classes. See attribute annotation for
	 * behavior.
	 */
	abstract public void removeAssociations();

	/**
	 * Gets associated attribute entity.
	 * 
	 * @return associated attribute entity. Could be null.
	 */
	abstract public IObject getAttributeClass();

	/**
	 * Gets groupName.
	 * 
	 * @return this.groupName.
	 */
	public String getGroup() {
		return this.groupName;
	}

	/**
	 * Gets identifier.
	 * 
	 * @return this.identifier.
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Sets this.identifier.
	 * 
	 * @s Adapter identifier, but separate from the id.
	 */
	public void setIdentifier(String s) {
		this.identifier = s;
	}

	/**
	 * Gets attributeName.
	 * 
	 * @return this.attributeName.
	 */
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * Updates this.groupName with getGroupName().
	 */
	public void updateGroup() {
		this.groupName = getGroupName();
	}

	/**
	 * Gets this.allowWrite.
	 * 
	 * @return this.allowWrite.
	 */
	public boolean getAllowWrite() {
		return this.allowWrite;
	}

	/**
	 * Sets this.allowWrite.
	 * 
	 * @b allow write.
	 */
	public void setAllowWrite(boolean b) {
		this.allowWrite = b;
	}

	/**
	 * Gets this.allowRead.
	 * 
	 * @return this.allowRead.
	 */
	public boolean getAllowRead() {
		return this.allowRead;
	}

	/**
	 * Sets this.allowRead.
	 * 
	 * @b allow read.
	 */
	public void setAllowRead(boolean b) {
		this.allowRead = b;
	}

	// adding abstract public void setGroup(String groupName) might be necessary;

	/**
	 * Creates AdapterProxy for this Adapter.
	 * 
	 * @return new AdapterProxy.
	 */
	public AdapterProxy toProxy() {
		return new AdapterProxy(this);
	}

}
