package model;

public interface User {
	int getID();
	String getName();
	default double getVar() {
		return 0;
	}
}
