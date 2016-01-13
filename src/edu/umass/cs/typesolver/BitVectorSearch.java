package edu.umass.cs.typesolver;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.ch.ClassInfoRecorder;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class BitVectorSearch {
	private BiMap<Integer, ClassDescriptor> id2cp;

	/**
	 *
	 * @param constraints: CNF form
	 * @param nObjs
	 * @return
	 */
	public List<ClassDescriptor> solve(List<Expression> constraints, int nObjs) {
		List<ClassDescriptor> res = new ArrayList<>();
		if(constraints==null || constraints.size()<1)
			return res;
		id2cp = HashBiMap.create();

		int index = 0;
		int nonAbstractTypes = 0;

		// we only want non-abstract types in the bit vector
		for(XClass cinfo : ClassInfoRecorder.getInstance().allClassInfo()) {
			id2cp.put(index++, cinfo.getClassDescriptor());
			if(MainConfig.NON_ABSTRACT_OPTIMIZATION) {
				if(!(cinfo.isAbstract())) {
					nonAbstractTypes++;
				}
			}
		}

		if(!(MainConfig.NON_ABSTRACT_OPTIMIZATION))
			nonAbstractTypes = index;

		BitVectorDP[] dps = new BitVectorDP[nObjs];
		for(int i=0; i<nObjs; i++) {
			dps[i] = new BitVectorDP(nonAbstractTypes, id2cp);
		}
		helper(dps, constraints, 0);
		for(int i=0; i<nObjs; i++) {
			res.add(dps[i].getTypeInModel());
		}
		return res;
	}

	/**
	 * @param dps
	 * @param constraints: We require constraints is of Conjunctive Normal
	 *                     Form (CNF)
	 * @param i
	 * @param j
	 * @return
	 */
	boolean helper(BitVectorDP[] dps, List<Expression> constraints, int i) {
		if(i==constraints.size()) {
			return true;
		}

		if(constraints.get(i) instanceof TOr) {
			TOr disjunction = (TOr)(constraints.get(i));
			for(int j=0; j<disjunction.getNumArgs(); j++) {
				Expression literal = disjunction.getArg(j);
				int objIndex = literal.getObj();
				dps[objIndex].push(literal);
				if(dps[objIndex].isSat()) {
					if(helper(dps, constraints, i+1))
						return true;
				}
				dps[objIndex].pop();
			}
		} else {
			Expression clause = constraints.get(i);
			int objIndex = clause.getObj();
			dps[objIndex].push(clause);
			if(dps[objIndex].isSat())
				return helper(dps, constraints, i+1);
		}
		return false;
	}



}
