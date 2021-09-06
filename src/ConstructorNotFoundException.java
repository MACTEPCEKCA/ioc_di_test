class ConstructorNotFoundException extends Exception {
	public ConstructorNotFoundException() {
		super("No valid constructor was found for the arguments mentioned in properties");
	}
}