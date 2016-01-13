package edu.uta.cse.dsc.ast.reftype;

import static edu.uta.cse.dsc.ast.reftype.TestUtility.check;
import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

/**
 * @author csallner@uta.edu (Christoph Csallner)
 */
final public class LiteralInterfaceType extends LiteralNonNullReferenceType
{

	ClassDescriptor objectDes = dfactory.getClassDescriptor("java/lang/Object");

	/**
	 * Constructor
	 */
	LiteralInterfaceType(Class<?> type)
	{
		super(type, null);
		if (!type.isInterface())
			check(false, type + " is not an interface.");
	}

	@Override
	public void registerSuperTypes()
	{
		ClassInfoRecorder.getInstance().getOrCreateClassInfo(dfactory.getClassDescriptor(dynamicType),
				objectDes, tutility.interfaceToClassDescriptor(directSuperInterfaces), dynamicType.getModifiers(),
				dfactory.getClassDescriptor(dynamicType.getEnclosingClass()));
	}
}
