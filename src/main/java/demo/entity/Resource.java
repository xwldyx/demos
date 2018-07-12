package demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4304569429057003655L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String urlPattern;
	
	private String code;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getId() {
		return id;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public String getCode() {
		return code;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
