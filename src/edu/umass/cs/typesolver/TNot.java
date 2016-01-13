package edu.umass.cs.typesolver;

public class TNot extends Expression {
	private Expression argument;

	public TNot(int Obj, Expression arg) {
		super(Obj);
		argument = arg;
	}

	public Expression getArgument() {
		return argument;
	}


}
