package model;

import java.util.Arrays;

public class Doctor extends AbstractUser {
	private double var = 3.14159265358979;
	protected int ID;

	public Doctor() {
		this.ID = 1;
		this.name = "Doctor";
	}

	public Doctor(int id, String name) {
		this.ID = id;
		this.name = name;
	}

	public Doctor(int id, String name, char[] someNumber) {
		this.ID = id;
		this.name = name;
		for (char ch : someNumber) {
			System.out.print(ch + ", ");
		}
		System.out.println();
		//System.out.println("Some Number: " + someNumber);
	}

	public double getVar() {
		return var;
	}

	@Override
	public String toString() {
		return "User: Dr. " + name + ", ID: " + ID;
	}
}
