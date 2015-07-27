package blah.concurrency.second;

import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import sun.misc.Unsafe;

/**
 * A simple CAS abstraction using {@link Unsafe} class.
 */
@SuppressWarnings("restriction")
@RequiredArgsConstructor
public class FieldReference {

	static final Unsafe unsafe = getUnsafe();
	final Object target;
    final long fieldAddress;
    
    public static Unsafe getUnsafe() {
	   try {
           Field f = Unsafe.class.getDeclaredField("theUnsafe");
           f.setAccessible(true);
           return (Unsafe)f.get(null);
	   } catch (Exception e) { 
	       throw new Error(e);
	   }
	}

    public static FieldReference wrap( Object object, String fieldName ) {
    	long fieldAddress = getFieldAddress(object.getClass(), fieldName);
		return new FieldReference(object, fieldAddress);
	}

	public static long getFieldAddress( Class<?> clazz, String fieldName ) {
		try {
	        return unsafe.objectFieldOffset
	            ( clazz.getDeclaredField( fieldName ));
	      } catch (Exception ex) { throw new Error(ex); }
	}

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(target, fieldAddress, expect, update);
    }

    public final <T> boolean compareAndSet(T expect, T update) {
        return unsafe.compareAndSwapObject(target, fieldAddress, expect, update);
    }
}
