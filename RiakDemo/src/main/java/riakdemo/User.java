package riakdemo;

public class User {
	public String companyName;
	public String name;
	public Integer phone;
	public String street;
	
	private static final String USER_FORMAT = "- %-10s  %-16s %-10s %-10s\n";
	
	public User() {
	}
	
	public User(String companyName, String name, Integer phone, String street) {
		this.companyName = companyName;
		this.name = name;
		this.phone = phone;
		this.street = street;
	}
	
	@Override
	public String toString() {
		return String.format(USER_FORMAT, companyName, name, phone, street);
	}
}
