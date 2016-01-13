package edu.uta.cse.dsc.ast.reftype;

import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.uta.cse.dsc.ast.util.TypeUtility;
import gnu.trove.THashSet;

import java.lang.reflect.Modifier;
import java.util.Set;

import static edu.uta.cse.dsc.ast.reftype.TestUtility.check;

/**
 * Represents a given array type, e.g., int[]
 *
 * @author csallner@uta.edu (Christoph Csallner)
 */
final public class LiteralArrayClassType extends LiteralClassType
{
	private Class<?> componentType;

	private LiteralArrayClassType directSuperClassArray;

	private final Set<LiteralArrayClassType> superInterfacesArray = new THashSet<LiteralArrayClassType>();

	/**
	 * Constructor
	 */
	LiteralArrayClassType(Class<?> type)
	{
		super(type);

		if (type == null)
			check(false, "There is no \"null type\".");

		if (!type.isArray())
			check(false, type + " is not an array.");

		// This could be a multi dimensional array
		componentType = TypeUtility.getInnerMostComponentType(type);
		String brackets = TypeUtility.getBracketsForArray(type);

		/* Direct super-interfaces of this class. */
		Class<?>[] superIfaces = componentType.getInterfaces();
		for (Class<?> superIface : superIfaces)
		{
			LiteralArrayClassType superNode;
			try
			{
				superNode = (LiteralArrayClassType) (TestUtility.getInstance()
						.getClass(Class.forName(brackets
								+ TypeUtility.getType(superIface, false))));
				superInterfacesArray.add(superNode);
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/* Direct super-interfaces of this class. */
		Class<?> superClass = componentType.getSuperclass();
		if (superClass == null)
			directSuperClassArray = null;
		else
		{
			try
			{
				directSuperClassArray = (LiteralArrayClassType) (TestUtility.getInstance()
						.getClass(Class.forName(brackets
						+ TypeUtility.getType(superClass, false))));
				// superInterfacesArray.add(directSuperClass4Array);

			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @return all classes of which this class is a subtype of (directly,
	 *         reflexivly and transitively).
	 */
	protected Set<LiteralArrayClassType> getAllSuperClasses4Array()
	{
		return superInterfacesArray;
	}

	@Override
	protected void registerSuperTypes()
	{
		ClassInfoRecorder.getInstance().getOrCreateClassInfo(
				dfactory.getClassDescriptor(dynamicType),
				dfactory.getClassDescriptor(directSuperClassArray.getClass()),
				tutility.arrayToClassDescriptor(superInterfacesArray),
				dynamicType.getModifiers(),
				dfactory.getClassDescriptor(dynamicType.getEnclosingClass()));

	}

	@Override
	public boolean isAbstract()
	{
		Class<?> type = TypeUtility.getInnerMostComponentType(dynamicType);
		return Modifier.isAbstract(type.getModifiers());
	}
}
