package edu.uta.cse.dsc.ast.reftype;

import static edu.uta.cse.dsc.ast.reftype.TestUtility.check;
import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import gnu.trove.THashSet;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Type (class or interface) we have seen in the current exploration, except the
 * null type.
 *
 * @author csallner@uta.edu (Christoph Csallner)
 */
public abstract class LiteralNonNullReferenceType extends
		LiteralSeenReferenceType {
	protected final Class<?> dynamicType;

	/**
	 * This class directly implements these interfaces. This interface directly
	 * extends these Interfaces.
	 */
	final Set<LiteralInterfaceType> directSuperInterfaces = new THashSet<LiteralInterfaceType>();

	/**
	 * id of the next class/array/interface we encounter.
	 *
	 * <P>
	 * Makes us allocate classes at the low end of our natural number space.
	 * <ul>
	 * <li>
	 * 0 = null type -- see LiteralNullReferenceTyepe</li>
	 * <li>
	 * 1 = {@link java.lang.Object}</li>
	 * <li>
	 * 2 = {@link java.lang.String}</li>
	 * <li>
	 * ...</li>
	 * <li>
	 * n = {@link java.util.Vector}</li>
	 * </ul>
	 * .
	 *
	 * FIXME(csallner): reduce visibility of this field!
	 */
	public static int nextClassId = 1;

	/**
	 * id we assigned to this type.
	 */
	final int classId;

	ClassInfoRecorder cinfo = ClassInfoRecorder.getInstance();
	DescriptorFactory dfactory = DescriptorFactory.instance();
	TestUtility tutility = TestUtility.getInstance();

	/**
	 * Constructor
	 *
	 * Register ourselves in the Z3ToJava mapping
	 *
	 * @param superClass
	 *            initialized but otherwise ignored.
	 */
	LiteralNonNullReferenceType(Class<?> dynamicType, Class<?> superClass) {
		if (dynamicType == null)
			check(false, "There is no \"null type\".");

		if (superClass != null)
			TestUtility.getInstance().getClass(superClass); // initialize super
															// first

		/* Direct super-interfaces of this class. */
		Class<?>[] superIfaces = dynamicType.getInterfaces();
		for (Class<?> superIface : superIfaces) {
			LiteralInterfaceType superNode = TestUtility.getInstance()
					.getInterface(superIface);
			directSuperInterfaces.add(superNode);
		}

		this.classId = nextClassId;
		nextClassId += 1;

		this.dynamicType = dynamicType;
	}

	@Override
	public int getGlobalId() {
		return classId;
	}

	@Override
	public Class<?> getType() {
		return dynamicType;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(dynamicType.getModifiers());
	}

	@Override
	public boolean isArray() {
		return dynamicType.isArray();
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(dynamicType.getModifiers());
	}

	@Override
	public boolean isInterface() {
		return dynamicType.isInterface();
	}

	/**
	 * @see http
	 *      ://java.sun.com/docs/books/jvms/second_edition/html/Concepts.doc.
	 *      html#18914
	 */
	@Override
	public boolean isPublic() {
		return Modifier.isPublic(dynamicType.getModifiers());
	}

	protected String constructSimpleRep() {
		return dynamicType.getSimpleName();
	}
}