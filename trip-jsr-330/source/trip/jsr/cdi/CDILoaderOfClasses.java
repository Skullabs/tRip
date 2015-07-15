package trip.jsr.cdi;

@SuppressWarnings("unchecked")
abstract class CDILoaderOfClasses {

	static <T> Class<? extends T> loadClass( String className ){
		try {
			return (Class<? extends T>)Class.forName(className);
		} catch (final ClassNotFoundException e) {
			return null;
		}
	}

	static boolean isClassPresent( String className ){
		return loadClass(className) != null;
	}

	static <T> T newInstanceOf( String className, Class<T> supertype ){
		try {
			return (T)loadClass(className).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
}
