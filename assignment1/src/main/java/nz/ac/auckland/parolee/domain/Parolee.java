package nz.ac.auckland.parolee.domain;

import org.joda.time.DateTime;

/**
 * Class to represent a Parolee.
 * 
 * For this first Web service, a Parolee is simply represented by a unique id, 
 * a name, gender and date of birth.
 * 
 * @author Ian Warren
 *
 */
public class Parolee {
	private int _id;
	private String _lastname;
	private String _firstname;
	private Gender _gender;
	private DateTime _dateOfBirth;
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
	}
	
	public String getLastname() {
		return _lastname;
	}
	
	public void setLastname(String lastname) {
		_lastname = lastname;
	}
	
	public String getFirstname() {
		return _firstname;
	}
	
	public void setFirstname(String firstname) {
		_firstname = firstname;
	}
	
	public Gender getGender() {
		return _gender;
	}
	
	public void setGender(Gender gender) {
		_gender = gender;
	}
	
	public DateTime getDateOfBirth() {
		return _dateOfBirth;
	}
	
	public void setDateOfBirth(DateTime dateOfBirth) {
		_dateOfBirth = dateOfBirth;
	}
}