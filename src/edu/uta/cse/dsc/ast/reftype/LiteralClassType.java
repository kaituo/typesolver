package edu.uta.cse.dsc.ast.reftype;

import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import gnu.trove.THashSet;

import java.util.Set;

/**
 * A non-interface, non-null class.
 *
 * @author csallner@uta.edu (Christoph Csallner)
 */
public class LiteralClassType extends LiteralNonNullReferenceType {

	//private LiteralClassType directSuperClass;

	/**
	 * Classes of which this class is a subtype of (directly, reflexivly and
	 * transitively).
	 */
	protected final Set<LiteralClassType> allSuperclasses = new THashSet<LiteralClassType>();

	/**
	 * Constructor
	 */
	LiteralClassType(Class<?> type) {
		super(type, type.getSuperclass());
//		/* Direct super-interfaces of this class. */
//		Class<?> superClass = type.getSuperclass();
//		if (superClass == null)
//			directSuperClass = null;
//		else {
//			directSuperClass = TestUtility.getInstance().getClass(superClass);
//		}
	}

	/**
	 * @return all classes of which this class is a subtype of (directly,
	 *         reflexivly and transitively).
	 */
	protected Set<LiteralClassType> getAllSuperClasses() {
		return allSuperclasses;
	}

	@Override
	protected void registerSuperTypes() {
		ClassInfoRecorder.getInstance().getOrCreateClassInfo(
				dfactory.getClassDescriptor(dynamicType),
				dfactory.getClassDescriptor(dynamicType.getSuperclass()),
				tutility.interfaceToClassDescriptor(directSuperInterfaces),
				dynamicType.getModifiers(),
				dfactory.getClassDescriptor(dynamicType.getEnclosingClass()));

	}
}
