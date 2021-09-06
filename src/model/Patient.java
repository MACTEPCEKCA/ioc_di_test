package model;

public class Patient extends AbstractUser {
	public Patient() {
		this.ID = 1;
		this.name = "Patient";
	}

	public Patient(int id, String name) {
		this.ID = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "User: P. " + name + ", ID: " + ID;
	}
}
