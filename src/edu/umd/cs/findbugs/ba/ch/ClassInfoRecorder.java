package edu.umd.cs.findbugs.ba.ch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.ClassInfo;
import edu.umd.cs.findbugs.ba.XClass;

public class ClassInfoRecorder {
	private static ClassInfoRecorder instance = null;
	Map<ClassDescriptor, XClass> cache;

	private ClassInfoRecorder() {
		this.cache = new HashMap<ClassDescriptor, XClass>();
	}

	public static ClassInfoRecorder getInstance() {
		if (instance == null) {
			instance = new ClassInfoRecorder();
		}
		return instance;
	}

	public XClass getOrCreateClassInfo(ClassDescriptor classDescriptor,
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
		XClass cinfo = classInfoBuilder.build();
		cache.put(classDescriptor, cinfo);
		return cinfo;
	}

	public XClass getXClass(ClassDescriptor classDescriptor) {
		return cache.get(classDescriptor);
	}

	public Collection<XClass> allClassInfo() {
		return cache.values();
	}
}
