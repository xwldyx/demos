package demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ResourceRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3073876687359476607L;

	@Id
	private long resourceId;
	
	private long roleId;

	public long getResourceId() {
		return resourceId;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	
}
