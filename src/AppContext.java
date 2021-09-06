import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

public class AppContext {
	private Properties props = new Properties();
	private HashMap<String, Object> singletons = new HashMap<>();

	public AppContext() throws IOException {
		props.load(this.getClass().getResourceAsStream("di.properties"));
		//props.load(ClassLoader.getSystemResourceAsStream("di.properties"));
	}

	public <T> T getBean(String beanName, Class<T> clazz) throws Exception {
		Class<?> newObjClass = Class.forName(props.getProperty(beanName));
		Constructor<?>[] constructors = newObjClass.getConstructors();
		String scope = props.getProperty(beanName + ".scope");
		boolean isSingleton = (scope != null && scope.toLowerCase(Locale.ROOT).equals("singleton"));

		//	Singleton check (return if found in hash map)
		if (isSingleton && singletons.get(beanName) != null) {
			return (T) singletons.get(beanName);
		}

		//	Parsing class constructors
		//	Here we also check for default constructor with no arguments
		//			which will be used in case of zero args in properties
		Constructor<?> foundConstructor = null;
		Class<?>[] foundParameterTypes = null;

		boolean noArgsFound = !props.containsKey(beanName + ".arg0");

		for (Constructor<?> constructor : constructors) {
			Parameter[] params = constructor.getParameters();

			boolean found = params.length > 0 || noArgsFound;
			if (noArgsFound && params.length > 0) {
				continue;
			}

			for (Parameter param : params) {
				String paramName = param.getName();
				if (!props.containsKey(beanName + "." + paramName)) {
					found = false;
					break;
				}
			}

			if (found) {
				foundConstructor = constructor;
				foundParameterTypes = foundConstructor.getParameterTypes();
				break;
			}
		}

		//	If no constructor was found, throw ConstructorNotFoundException
		if (foundConstructor == null) {
			throw new ConstructorNotFoundException();
		}

		//	Converting constructor arguments from properties to found constructor parameter types
		Object[] args = new Object[foundParameterTypes.length];
		for (int i = 0; i < foundParameterTypes.length; i++) {
			String propArg = props.getProperty(beanName + ".arg" + i);
			args[i] = parseStringArgument(propArg, foundParameterTypes[i]);
		}

		//	Singleton check (add if no presence was found in hash map)
		T obj = (T) foundConstructor.newInstance(args);
		if (isSingleton) {
			singletons.put(beanName, obj);
		}

		//	Setting fields manually by bypassing "private" modifier with setAccessible method
		setUpFields(beanName, newObjClass, obj);

		return obj;
	}

	private <T> T parseStringArgument(String arg, Class<T> clazz) {
		if (clazz == String.class) {
			return (T) arg;
		} else if (clazz == CharSequence.class) {
			return (T) arg.subSequence(0, arg.length());
		} else if (clazz == char[].class) {
			return (T) arg.toCharArray();
		} else if (clazz == Integer.class || clazz == int.class) {
			return (T) (Integer) Integer.parseInt(arg);
		} else if (clazz == Double.class || clazz == double.class) {
			return (T) (Double) Double.parseDouble(arg);
		} else if (clazz == Float.class || clazz == float.class) {
			return (T) (Float) Float.parseFloat(arg);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			return (T) (Boolean) Boolean.parseBoolean(arg);
		} else {
			return (T) arg;
		}
	}

	private void setUpFields(String beanName, Class<?> newObjClass, Object obj) {
		for (Object propKey : props.keySet()) {
			if (propKey.equals(beanName)
					|| ((String) propKey).contains(beanName + ".arg")
					|| ((String) propKey).contains(beanName + ".scope")) {
				continue;
			}

			String varName = ((String) propKey).substring(beanName.length() + 1);

			try {
				Field fieldVar = newObjClass.getDeclaredField(varName);
				fieldVar.setAccessible(true);
				fieldVar.set(obj, parseStringArgument(props.getProperty((String) propKey), fieldVar.getType()));
			} catch (Exception exp) { // NoSuchFieldException || IllegalAccessException
				continue;
			}
		}
	}
}