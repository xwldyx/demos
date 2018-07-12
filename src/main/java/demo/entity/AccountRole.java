package demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccountRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 39508681536917034L;

	@Id
	private long accId;
	
	private long roleId;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getAccId() {
		return accId;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setAccId(long accId) {
		this.accId = accId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

}
