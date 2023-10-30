package marshmalliow.core.json.assertions;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import marshmalliow.core.json.exceptions.JSONAssertionException;
import marshmalliow.core.json.objects.JSONContainer;

public class JSONAssertion {

	public static <E extends JSONContainer> void assertContentType(Class<E> container, Class<E> reference, String testedKey) {
		if (!container.equals(reference))
			throw new JSONAssertionException("Container type equality wasn't met", testedKey,
					AssertionType.JSON_CONTENT_TYPE, reference);
	}

	public static <E extends JSONContainer> void assertContentType(Class<E> container, Class<E> reference) {
		if (!container.equals(reference))
			throw new JSONAssertionException("Container type equality wasn't met",
					AssertionType.JSON_CONTENT_TYPE, reference);
	}
	
	public static void assertInstanceof(Object testedObj, Class<?> reference, String testedKey) {
		if (!reference.isInstance(testedObj))
			throw new JSONAssertionException("Object instance of condition wasn't met", testedKey,
					AssertionType.INSTANCE_OF, reference);
	}

	public static void assertInstanceof(Object testedObj, Class<?> reference) {
		if (!reference.isInstance(testedObj))
			throw new JSONAssertionException("Object instance of condition wasn't met", AssertionType.INSTANCE_OF,
					reference);
	}

	public static void assertTrue(boolean condition, String testedKey) {
		if (!condition)
			throw new JSONAssertionException("Boolean condition wasn't met", testedKey, AssertionType.TRUE);
	}

	public static void assertTrue(boolean condition) {
		if (!condition)
			throw new JSONAssertionException("Boolean condition wasn't met", AssertionType.TRUE);
	}

	public static void assertTrue(BooleanSupplier condition, String testedKey) {
		if (!condition.getAsBoolean())
			throw new JSONAssertionException("Boolean condition wasn't met", testedKey, AssertionType.TRUE);
	}

	public static void assertTrue(BooleanSupplier condition) {
		if (!condition.getAsBoolean())
			throw new JSONAssertionException("Boolean condition wasn't met", AssertionType.TRUE);
	}
	
	public static void assertFalse(boolean condition, String testedKey) {
		if (condition)
			throw new JSONAssertionException("Boolean condition wasn't met", testedKey, AssertionType.FALSE);
	}

	public static void assertFalse(boolean condition) {
		if (condition)
			throw new JSONAssertionException("Boolean condition wasn't met", AssertionType.FALSE);
	}

	public static void assertFalse(BooleanSupplier condition, String testedKey) {
		if (condition.getAsBoolean())
			throw new JSONAssertionException("Boolean condition wasn't met", testedKey, AssertionType.FALSE);
	}

	public static void assertFalse(BooleanSupplier condition) {
		if (condition.getAsBoolean())
			throw new JSONAssertionException("Boolean condition wasn't met", AssertionType.FALSE);
	}

	public static void assertEqual(Object testedObj, Object baseObj) {
		if (!testedObj.equals(baseObj))
			throw new JSONAssertionException("Equal condition wasn't met", AssertionType.EQUAL);
	}
	
	public static void assertEqual(Object testedObj, Object baseObj, String testedKey) {
		if (!testedObj.equals(baseObj))
			throw new JSONAssertionException("Equal condition wasn't met", testedKey, AssertionType.EQUAL);
	}

	public static void assertNotEqual(Object testedObj, Object baseObj) {
		if (testedObj.equals(baseObj))
			throw new JSONAssertionException("Not equal condition wasn't met", AssertionType.NOT_EQUAL);
	}
	
	public static void assertNotEqual(Object testedObj, Object baseObj, String testedKey) {
		if (testedObj.equals(baseObj))
			throw new JSONAssertionException("Not equal condition wasn't met", testedKey, AssertionType.NOT_EQUAL);
	}

	public static void assertNotNull(Object obj) {
		if (obj == null)
			throw new JSONAssertionException("Object not null condition wasn't met", AssertionType.NOT_NULL);
	}
	
	public static void assertNotNull(Object obj, String testedKey) {
		if (obj == null)
			throw new JSONAssertionException("Object not null condition wasn't met", testedKey, AssertionType.NOT_NULL);
	}
	
	public static void assertNotNull(Optional<Object> obj) {
		if (obj == null || obj.isEmpty())
			throw new JSONAssertionException("Object not null condition wasn't met", AssertionType.NOT_NULL);
	}
	
	public static void assertNotNull(Optional<Object> obj, String testedKey) {
		if (obj == null || obj.isEmpty())
			throw new JSONAssertionException("Object not null condition wasn't met", testedKey, AssertionType.NOT_NULL);
	}
	
}
