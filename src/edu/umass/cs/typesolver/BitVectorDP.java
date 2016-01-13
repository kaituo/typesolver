package edu.umass.cs.typesolver;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.ba.ch.TypeHierarchy;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;


public class BitVectorDP {
	private int nbits;

	private Stack<BitSet> s;
	private Map<Expression, BitSet> mem;
	private ClassInfoRecorder cirecorder;
	private BiMap<Integer, ClassDescriptor> id2cp;

	public BitVectorDP(int nonAbstractTypes, BiMap<Integer, ClassDescriptor> id2cp) {
		this.s = new Stack<BitSet>();
		mem = new HashMap<Expression, BitSet>();
		this.nbits = nonAbstractTypes;
		cirecorder = ClassInfoRecorder.getInstance();
		this.id2cp = id2cp;
	}

	public void push(Expression r) {
		BitSet B = makeB1();
		if(mem.containsKey(r)) {
			B = mem.get(r);
		} else {
			Expression neg = getComplement(r);
			if(mem.containsKey(neg)) {
				B = mem.get(neg);
				B.flip(0, nbits);
			} else {
				B = BVH(r);
			}
			mem.put(r, B);
		}
		B.and(s.peek());
		s.push(B);
	}

	public BitSet pop() {
		return s.pop();
	}

	public boolean isSat() {
		if(s.peek().isEmpty())
			return true;
		return false;
	}

	// will throw exception if there is no model, should use together with isSat()
	public ClassDescriptor getTypeInModel() {
		return id2cp.get(s.peek().nextSetBit(0));
	}

	private Expression getComplement(Expression r) {
		if(r instanceof TEqual || r instanceof TSubType)
			return new TNot(r.getObj(), r);
		else
			return ((TNot)r).getArgument();
	}

	private BitSet BVH(Expression r) {
		BitSet bs = makeB0();
		if(r instanceof TEqual || (r instanceof TNot && ((TNot)r).getArgument() instanceof TEqual)) {
			if(r instanceof TNot) {
				ClassDescriptor type = ((TEqual)(((TNot) r).getArgument())).getArgument();
				int t = id2cp.inverse().get(type);
				bs.set(t, false);
			}
			else {
				int t = id2cp.inverse().get(((TEqual)r).getArgument());
				bs.set(t);
			}
		} else if(r instanceof TSubType || (r instanceof TNot && ((TNot)r).getArgument() instanceof TSubType)) {

			if(r instanceof TNot) {
				ClassDescriptor type = ((TSubType)(((TNot) r).getArgument())).getArgument();
				int t = id2cp.inverse().get(type);
				for(Integer d: descendsOf(t)) {
					bs.set(d, false);
				}
			}
			else {
				int t = id2cp.inverse().get(((TSubType)r).getArgument());
				for(Integer d: descendsOf(t)) {
					bs.set(d);
				}
			}

		}
		return bs;
	}

	private List<Integer> descendsOf(int t) {
		ClassDescriptor dp = id2cp.get(t);
		List<Integer> res = new ArrayList<>();
		if(dp == null)
			throw new IllegalStateException("Unknown type!");
		try {
			Set<ClassDescriptor> subtypes = TypeHierarchy.getInstance().getSubtypes(dp);

			for(ClassDescriptor cdp: subtypes) {
				if(MainConfig.NON_ABSTRACT_OPTIMIZATION) {
					if(!(cirecorder.getXClass(cdp).isAbstract()))
						res.add(id2cp.inverse().get(cdp));
				} else {
					res.add(id2cp.inverse().get(cdp));
				}
			}
		} catch(ClassNotFoundException e) {
			throw new IllegalStateException("Should not happen, unknown type!");
		}
		return res;
	}

	private BitSet makeB1() {
		BitSet bs = new BitSet(nbits);
		bs.set(0, nbits);
		return bs;
	}

	private BitSet makeB0() {
		return new BitSet(nbits);
	}

	public void restart() {
		s.clear();
		s.push(makeB1());
	}
}
