package edu.umass.cs.typesolver;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class TSubType implements Relation {
	private ClassDescriptor argument;

	private TSubType(ClassDescriptor arg) {
		argument = arg;
	}

	public ClassDescriptor getArgument() {
		return argument;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TSubType other = (TSubType) obj;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		return true;
	}


}
