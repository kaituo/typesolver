package edu.uta.cse.dsc.ast.util;

import java.lang.reflect.Modifier;

public class TypeUtility
{
	public static String getSignature(java.lang.reflect.Method meth)
	{
		StringBuilder sb = new StringBuilder("(");
		Class<?>[] params = meth.getParameterTypes(); // avoid clone
		for (int j = 0; j < params.length; j++)
		{
			sb.append(getType(params[j], true));
		}
		sb.append(")");
		sb.append(getType(meth.getReturnType(), true));
		return sb.toString();
	}

	public static String getType(java.lang.Class<?> cl, boolean slashed)
	{
		if (cl == null) { throw new IllegalArgumentException(
				"Class must not be null"); }
		/*
		 * That's an amazingly easy case, because getName() returns the signature.
		 * That's what we would have liked anyway.
		 */
		if (cl.isArray())
		{
			if(slashed)
				return toSlashedClassName(cl.getName());
			return cl.getName();
		}
		else if (cl.isPrimitive())
		{
			if (cl == Integer.TYPE)
			{
				return "I";
			}
			else if (cl == Void.TYPE)
			{
				return "V";
			}
			else if (cl == Double.TYPE)
			{
				return "D";
			}
			else if (cl == Float.TYPE)
			{
				return "F";
			}
			else if (cl == Boolean.TYPE)
			{
				return "Z";
			}
			else if (cl == Byte.TYPE)
			{
				return "B";
			}
			else if (cl == Short.TYPE)
			{
				return "S";
			}
			else if (cl == Long.TYPE)
			{
				return "J";
			}
			else if (cl == Character.TYPE)
			{
				return "C";
			}
			else
			{
				throw new IllegalStateException("Ooops, what primitive type is " + cl);
			}
		}
		else
		{ // "Real" class
			if(slashed)
				return "L" + toSlashedClassName(cl.getName()) + ";";
			else
				return "L" + cl.getName() + ";";
		}
	}

	public static String toSlashedClassName(String className)
	{
		if (className.indexOf('.') >= 0) { return className.replace('.', '/'); }
		return className;
	}

	public static boolean isAbstract(Class<?> cl) {
		Class<?> type = cl;
		if(cl.isArray())
			type = getInnerMostComponentType(cl);
		int mod = type.getModifiers();
		return Modifier.isAbstract(mod);
	}

	public static Class<?> getInnerMostComponentType(Class<?> cl) {
		if(!cl.isArray())
			return cl;
		return getInnerMostComponentType(cl.getComponentType());
	}

	public static int getDimensions(Class<?> cl) {
		return cl.getName().lastIndexOf('[')+1;
	}

	public static String getBracketsForArray(Class<?> cl) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<getDimensions(cl); i++) {
			sb.append('[');
		}
		return sb.toString();
	}
}
