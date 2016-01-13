package edu.umass.cs.typesolver;

public class TAnd extends Expression {
	private Expression left, right;


	public TAnd(int Obj, Expression left, Expression right) {
		super(Obj);
		this.left = left;
		this.right = right;
	}



}
