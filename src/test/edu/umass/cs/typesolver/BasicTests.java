package test.edu.umass.cs.typesolver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.uta.cse.dsc.ast.reftype.TestUtility;

public class BasicTests {
	ClassInfoRecorder cinfo = ClassInfoRecorder.getInstance();
	DescriptorFactory dfactory = DescriptorFactory.instance();
	TestUtility tutility = TestUtility.getInstance();

	@Before
	public void initialize() {
//		Class class_object;
//		try {
//			class_object = Class.forName("java/lang/Object");
//			ClassDescriptor classDescriptor = dfactory.getClassDescriptor("java/lang/Object");
//			ClassDescriptor superclassDescriptor_Object = dfactory.getClassDescriptor("java/lang/Object");
//			ClassDescriptor[] interfaceDescriptorList_Object = new ClassDescriptor[] {};
//			int accessFlags_Object = class_object.getModifiers();
//			Class enclosingClass = class_object.getEnclosingClass();
//			ClassDescriptor immediateEnclosingClass = enclosingClass==null? null: dfactory.getClassDescriptor(enclosingClass);
//			cinfo.getOrCreateClassInfo(dfactory.getClassDescriptor("java/lang/Object"),
//					superclassDescriptor_Object, interfaceDescriptorList_Object, accessFlags_Object, immediateEnclosingClass);
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			tutility.getClass(Class.forName("java.lang.Object"));
			tutility.getClass(Class.forName("org.apache.bcel.generic.ConstantPoolGen"));
			tutility.getClass(Class.forName("java.util.List"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ConstantPoolGen cp,List<AnnotationEntryGen> vec
	 */
	@Test
	public void test1() {

	}



}
