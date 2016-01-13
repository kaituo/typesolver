package edu.uta.cse.dsc.ast.reftype;

import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.uta.cse.dsc.util.DscAssertionViolationException;
import gnu.trove.THashMap;
import gnu.trove.TIntObjectHashMap;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class TestUtility {
	/**
	 * 1 = java.lang.Object 4 = java.lang.Enum ...
	 *
	 * Raw jvm class/array --> refType
	 */
	private final Map<Type, LiteralClassType> classes = new THashMap<Type, LiteralClassType>();

	/**
	 * 2 = java.lang.Comparable 3 = java.io.Serializable ...
	 *
	 * Raw jvm interface --> refType
	 */
	private final Map<Class<?>, LiteralInterfaceType> interfaces = new THashMap<Class<?>, LiteralInterfaceType>();

	/**
	 * int --> Type
	 *
	 * <p>
	 * Maps to Type, not Class, as we represent the null_type with a Type. We do
	 * that because we could not find a Class that rerpesents the null_type.
	 */
	private final TIntObjectHashMap<Type> id2type = new TIntObjectHashMap<Type>();

	private static TestUtility instance = null;

	ClassInfoRecorder cinfo = ClassInfoRecorder.getInstance();
	DescriptorFactory dfactory = DescriptorFactory.instance();

	private TestUtility() {
	}

	public static TestUtility getInstance() {
		if (instance == null) {
			instance = new TestUtility();
		}
		return instance;
	}


	private LiteralClassType addClass(Class<?> claz) {
		if (claz == null)
			check(false);

		LiteralClassType res;
		if (claz.isInterface())
			check(false, claz + " is an interface.");

		if (claz.isArray())
			res = new LiteralArrayClassType(claz);
		else
			res = new LiteralClassType(claz);

		classes.put(claz, res);
		id2type.put(res.getGlobalId(), res.getType());

		res.registerSuperTypes();

		return res;
	}

	/**
	 * Checks if b holds. Call this method to check assertions like pre- and
	 * post-conditions.
	 *
	 * @throws IllegalStateException
	 *             iff (b==false)
	 */
	public static void check(final boolean b) {
		check(b, "");
	}

	public static void check(final boolean b, Throwable t) {
		if (b == false) {
			IllegalStateException ise = new IllegalStateException(t);
			throw ise;
		}
	}

	/**
	 * @param msg
	 *            for exception, in case b==false
	 */
	public static void check(final boolean b, String msg) {
		if (b == false)
			throw new DscAssertionViolationException(msg);
	}

	/**
	 * Log this Interface and all Interfaces it extends.
	 */
	private LiteralInterfaceType addInterface(Class<?> claz) {
		if (!claz.isInterface())
			check(false, claz + " is not an interface.");

		final LiteralInterfaceType res = new LiteralInterfaceType(claz);
		interfaces.put(claz, res);
		id2type.put(res.getGlobalId(), res.getType());

		res.registerSuperTypes();

		return res;
	}

	public LiteralInterfaceType getInterface(Class<?> claz) {
		if (claz == null)
			check(false, "get null_type instead");

		LiteralInterfaceType node = interfaces.get(claz);
		if (node == null)
			node = addInterface(claz);
		return node;
	}

	public LiteralClassType getClass(Class<?> claz) {
		if (claz == null)
			check(false, "get null_type instead");

		LiteralClassType typeLiteral = classes.get(claz);
		if (typeLiteral == null)
			typeLiteral = addClass(claz);
		return typeLiteral;
	}

	public ClassDescriptor[] interfaceToClassDescriptor(Set<LiteralInterfaceType> superInterfaces) {
		int size = superInterfaces.size();
		ClassDescriptor[] superinterfaceArray = new ClassDescriptor[size];
		int i = 0;
		for(LiteralInterfaceType type: superInterfaces) {
			superinterfaceArray[i++] = dfactory.getClassDescriptor(type.dynamicType);
		}
		return superinterfaceArray;
	}

	public ClassDescriptor[] arrayToClassDescriptor(Set<LiteralArrayClassType> superInterfaces) {
		int size = superInterfaces.size();
		ClassDescriptor[] superinterfaceArray = new ClassDescriptor[size];
		int i = 0;
		for(LiteralArrayClassType type: superInterfaces) {
			superinterfaceArray[i++] = dfactory.getClassDescriptor(type.dynamicType);
		}
		return superinterfaceArray;
	}
}
