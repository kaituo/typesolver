package edu.umass.cs.typesolver;

public abstract class Expression {
	protected int obj;

	protected Expression(int o) {
		this.obj = o;
	}

	public int getObj() {
		return obj;
	}
}
