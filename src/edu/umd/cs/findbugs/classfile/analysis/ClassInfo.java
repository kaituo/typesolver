/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006, University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.classfile.analysis;

import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

/**
 * ClassInfo represents important metadata about a loaded class, such as its
 * superclass, access flags, codebase entry, etc.
 *
 * @author David Hovemeyer
 */
public class ClassInfo extends ClassNameAndSuperclassInfo implements XClass {

	/**
	 *
	 */
	private static final long serialVersionUID = 7165979002258711147L;
	private final ClassDescriptor immediateEnclosingClass;

	public static class Builder extends ClassNameAndSuperclassInfo.Builder {

		private ClassDescriptor immediateEnclosingClass;

		@Override
		public ClassInfo build() {
			return new ClassInfo(classDescriptor, superclassDescriptor,
					interfaceDescriptorList, accessFlags,
					immediateEnclosingClass);
		}

		public ClassDescriptor getClassDescriptor() {
			return classDescriptor;
		}

		public void setImmediateEnclosingClass(
				ClassDescriptor immediateEnclosingClass) {
			this.immediateEnclosingClass = immediateEnclosingClass;
		}

	}

	/**
	 *
	 * @param classDescriptor
	 *            ClassDescriptor representing the class name
	 * @param superclassDescriptor
	 *            ClassDescriptor representing the superclass name
	 * @param interfaceDescriptorList
	 *            ClassDescriptors representing implemented interface names
	 * @param codeBaseEntry
	 *            codebase entry class was loaded from
	 * @param accessFlags
	 *            class's access flags
	 * @param referencedClassDescriptorList
	 *            ClassDescriptors of all classes/interfaces referenced by the
	 *            class
	 * @param fieldDescriptorList
	 *            FieldDescriptors of fields defined in the class
	 * @param methodInfoList
	 *            MethodDescriptors of methods defined in the class
	 */
	private ClassInfo(ClassDescriptor classDescriptor,
			ClassDescriptor superclassDescriptor,
			ClassDescriptor[] interfaceDescriptorList, int accessFlags,
			ClassDescriptor immediateEnclosingClass) {
		super(classDescriptor, superclassDescriptor, interfaceDescriptorList,
				accessFlags);
		this.immediateEnclosingClass = immediateEnclosingClass;
	}

	@Override
	public ClassDescriptor getImmediateEnclosingClass() {
		return immediateEnclosingClass;
	}

	@Override
	public String getPackageName() {
		String dottedClassName = getClassDescriptor().toDottedClassName();
		int lastDot = dottedClassName.lastIndexOf('.');
		if (lastDot < 0) {
			return "";
		} else {
			return dottedClassName.substring(0, lastDot);
		}
	}

	public String getSlashedPackageName() {
		String slashedClassName = getClassDescriptor().getClassName();
		int lastSlash = slashedClassName.lastIndexOf('/');
		if (lastSlash < 0) {
			return "";
		} else {
			return slashedClassName.substring(0, lastSlash);
		}
	}

}
