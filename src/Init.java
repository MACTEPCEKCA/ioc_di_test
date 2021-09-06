import model.*;


public class Init {
	public static void main(String[] args) throws Throwable {
		AppContext context = new AppContext();

		User user = context.getBean("beanName", User.class);
		Doctor user2 = context.getBean("beanName", Doctor.class);

		System.out.println();

		System.out.println(user);
		System.out.println();

		System.out.println("The same object: " + (user == user2));
		System.out.println();

		System.out.println("Var = " + user.getVar());
	}
}




