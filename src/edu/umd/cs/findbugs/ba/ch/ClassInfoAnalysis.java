package edu.umd.cs.findbugs.ba.ch;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.ClassInfo;
import edu.umd.cs.findbugs.ba.XClass;

public class ClassInfoAnalysis {
	private static ClassInfoAnalysis instance = null;
	Map<ClassDescriptor, XClass> cache;

	private ClassInfoAnalysis() {
		this.cache = new HashMap<ClassDescriptor, XClass>();
	}

	public static ClassInfoAnalysis getInstance() {
		if (instance == null) {
			instance = new ClassInfoAnalysis();
		}
		return instance;
	}

	public XClass findOrCreateClassInfo(ClassDescriptor classDescriptor,
			ClassDescriptor superclassDescriptor,
			ClassDescriptor[] interfaceDescriptorList, int accessFlags,
			ClassDescriptor immediateEnclosingClass) {

		if (cache.containsKey(classDescriptor)) {
			return cache.get(classDescriptor);
		}

		ClassInfo.Builder classInfoBuilder = new ClassInfo.Builder();
		classInfoBuilder.setSuperclassDescriptor(superclassDescriptor);
		classInfoBuilder.setInterfaceDescriptorList(interfaceDescriptorList);
		classInfoBuilder.setAccessFlags(accessFlags);
		classInfoBuilder.setImmediateEnclosingClass(immediateEnclosingClass);
		return classInfoBuilder.build();
	}

	public XClass getXClass(ClassDescriptor classDescriptor) {
		return cache.get(classDescriptor);
	}
}
