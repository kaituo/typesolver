package edu.umass.cs.typesolver;

public class TNot implements Relation {
	private Relation argument;

	public TNot(Relation arg) {
		argument = arg;
	}

	public Relation getArgument() {
		return argument;
	}

}
