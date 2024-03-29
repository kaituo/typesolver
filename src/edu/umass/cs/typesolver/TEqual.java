package edu.umass.cs.typesolver;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class TEqual extends Expression {

	private ClassDescriptor argument;

	private TEqual(int Obj, ClassDescriptor argument) {
		super(Obj);
		this.argument = argument;
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
		TEqual other = (TEqual) obj;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		return true;
	}


}
