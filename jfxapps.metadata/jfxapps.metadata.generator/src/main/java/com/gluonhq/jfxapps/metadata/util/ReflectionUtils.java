package com.gluonhq.jfxapps.metadata.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {

	private static final Map<Class<?>, Object> primitiveDefaultValue = new HashMap<>();
    static {
    	primitiveDefaultValue.put(Boolean.TYPE, false);
    	primitiveDefaultValue.put(Byte.TYPE, (byte)0);
    	primitiveDefaultValue.put(Character.TYPE, '\u0000');
    	primitiveDefaultValue.put(Short.TYPE, (short)0);
    	primitiveDefaultValue.put(Integer.TYPE, 0);
    	primitiveDefaultValue.put(Long.TYPE, 0L);
    	primitiveDefaultValue.put(Double.TYPE, 0.0);
    	primitiveDefaultValue.put(Float.TYPE, 0.0f);
    	primitiveDefaultValue.put(Void.TYPE, null);
    }

    private static final Map<Class<?>, String> primitiveDeclarationFormat = new HashMap<>();
    static {
    	primitiveDeclarationFormat.put(Boolean.TYPE, "%s");
    	primitiveDeclarationFormat.put(Byte.TYPE, "%s");
    	primitiveDeclarationFormat.put(Character.TYPE, "'%s'");
    	primitiveDeclarationFormat.put(Short.TYPE, "%s");
    	primitiveDeclarationFormat.put(Integer.TYPE, "%s");
    	primitiveDeclarationFormat.put(Long.TYPE, "%sL");
    	primitiveDeclarationFormat.put(Double.TYPE, "%sd");
    	primitiveDeclarationFormat.put(Float.TYPE, "%sf");
    	primitiveDeclarationFormat.put(String.class, "\"%s\"");
    	primitiveDeclarationFormat.put(Void.TYPE, null);
    }

    public static Object getPrimitiveDefaultValue(Class<?> cls) {
    	return primitiveDefaultValue.get(cls);
    }

    public static String getPrimitiveDeclaration(Class<?> cls, Object value) {
    	return String.format(primitiveDeclarationFormat.get(cls), value);
    }

	public static String findStaticMemberByValue(Class<?> holder, Object value) {
		for (Field field:holder.getDeclaredFields()) {
			try {
				if (Modifier.isStatic(field.getModifiers()) && field.get(null).equals(value)) {
					return holder.getName() + "." + field.getName();
				}
			} catch (Exception e) {}
		}
		return holder.getEnclosingClass() == null ? null : findStaticMemberByValue(holder.getEnclosingClass(), value);
	}

	public static String findStaticMemberByType(Class<?> holder, Class<?> type) {
		for (Field field:holder.getDeclaredFields()) {
			try {
				if (Modifier.isStatic(field.getModifiers()) && field.get(null) != null && field.get(null).getClass().equals(type)) {
					return holder.getName() + "." + field.getName();
				}
			} catch (Exception e) {}
		}
		return holder.getEnclosingClass() == null ? null : findStaticMemberByType(holder.getEnclosingClass(), type);
	}

	public static Object findStaticMemberValueByType(Class<?> holder, Class<?> type) {
		for (Field field:holder.getDeclaredFields()) {
			try {
				if (Modifier.isStatic(field.getModifiers()) && field.get(null) != null && field.get(null).getClass().equals(type)) {
					return field.get(null);
				}
			} catch (Exception e) {}
		}
		return holder.getEnclosingClass() == null ? null : findStaticMemberValueByType(holder.getEnclosingClass(), type);
	}

	public static String findStaticGetMethodByValue(Class<?> holder, Object value) {
		for (Method method:holder.getDeclaredMethods()) {
			try {
				if (Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0 && method.invoke(null).equals(value)) {
					return holder.getName() + "." + method.getName() + "()";
				}
			} catch (Exception e) {}
		}
		return holder.getEnclosingClass() == null ? null : findStaticGetMethodByValue(holder.getEnclosingClass(), value);
	}


	public static Object[] convertToObjectArray(Object array) {
	    Class<?> ofArray = array.getClass().getComponentType();
	    if (ofArray.isPrimitive()) {
	        List<Object> ar = new ArrayList<>();
	        int length = Array.getLength(array);
	        for (int i = 0; i < length; i++) {
	            ar.add(Array.get(array, i));
	        }
	        return ar.toArray();
	    }
	    else {
	        return (Object[]) array;
	    }
	}

	public static List<Class<?>> findGenericTypes(Class<?> owner, String fieldName) throws NoSuchFieldException, SecurityException {

		List<Class<?>> result = new ArrayList<>();
        Field field = findField(owner, fieldName);
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType)type;
            Type[] arr = pType.getActualTypeArguments();

            for (Type tp: arr) {
            	if (ParameterizedType.class.isAssignableFrom(tp.getClass())) {
            		ParameterizedType prmType = ParameterizedType.class.cast(tp);
            		result.add((Class<?>)prmType.getRawType());
            	} else {
            		result.add((Class<?>)tp);
            	}
            }
            return result;
        }
        return null;
	}

	public static Field findField(Class<?> owner, String fieldName) {

		Class<?> current = owner;

		while (current != null) {
			try {
				Field field = current.getDeclaredField(fieldName);
				if (field != null) {
					return field;
				}
			} catch (Exception e) {}

			current = current.getSuperclass();
		}
        return null;
	}
}
