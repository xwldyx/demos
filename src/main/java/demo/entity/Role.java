package demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Role implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3226354365352480077L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String roleName;
	
	private String code;

	public long getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
