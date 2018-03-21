package test;

import javax.swing.table.object.Column;

/**
 * 
 * Employee object
 * 
 * @author Antony Hixson
 *
 */
public class Employee {

	static int nextId = 1554;

	@Column(name = "Name", placeholder = "John Doe", required = true, tooltip = "Employee's name")
	String name;

	@Column(name = "ID", unique = true, editable = false, required = true, tooltip = "Employee's unique identifier", index = 5)
	int id;

	@Column(name = "Active", tooltip = "Employee is working at the company")
	boolean active;

	@Column(name = "Joined date", min = "0", placeholder = "timestamp", required = true, tooltip = "Date of joining the company")
	long joined;

	@Column(name = "Joined date", min = "0", placeholder = "timestamp", value = "18", tooltip = "Age of the employee (optional)")
	int age;

	@Column(name = "Job")
	Occupation occupation;

	public Employee() {
		super();
		id = ++nextId;
	}
	
	public Employee(String name, boolean active, long joined, int age, Occupation occupation) {
		this();
		this.name = name;
		this.active = active;
		this.joined = joined;
		this.age = age;
		this.occupation = occupation;
	}

	@Override
	public String toString() {
		return String.format("{ id=%s, name=%s, active=%s, joined=%s, age=%s, occupation=%s }", id, name, active, joined, age, occupation);
	}

	@Column(name = "Print", placeholder="Print", tooltip = "Print to console")
	public void print() {
		System.out.println(toString());
	}

	/**
	 * Job roles
	 */
	enum Occupation {
		
		IT_SUPPORT, IT_DEVELOPER, IT_PROJECT_MANAGER, DIRECTOR
		
	}
}
