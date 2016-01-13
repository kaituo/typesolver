package edu.umass.cs.typesolver;

public class TOr extends Expression {
	private Expression[] args;


	public TOr(int Obj, Expression[] args) {
		super(Obj);
		this.args = args;
	}

	public int getNumArgs() {
		return args.length;
	}

	public Expression getArg(int i) {
		return args[i];
	}
}
