package custom.classes;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class Page implements Serializable{
	String name,role;
	Class pageClass;
	
	public Page(String name, String role, Class pageClass) {
		this.name = name;
		this.role = role;
		this.pageClass = pageClass;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public Class getPageClass() {
		return pageClass;
	}
	
	
}
