package edu.umd.cs.findbugs.ba.ch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.util.MapCache;

public class TypeHierarchy {
	private final InheritanceGraph graph;

	private final Map<ClassDescriptor, ClassVertex> classDescriptorToVertexMap;

	private final Set<XClass> xclassSet;

	private final Map<ClassDescriptor, Set<ClassDescriptor>> subtypeSetMap;

	private static TypeHierarchy instance = null;

	public static TypeHierarchy getInstance() {
		if (instance == null) {
			instance = new TypeHierarchy();
		}
		return instance;
	}

	/**
	 * Constructor.
	 */
	private TypeHierarchy() {
		this.graph = new InheritanceGraph();
		this.classDescriptorToVertexMap = new HashMap<ClassDescriptor, ClassVertex>();
		this.xclassSet = new HashSet<XClass>();
		this.subtypeSetMap = new MapCache<ClassDescriptor, Set<ClassDescriptor>>(
				500);

	}

	/**
	 * Get known subtypes of given class. The set returned <em>DOES</em> include
	 * the class itself.
	 *
	 * @param classDescriptor
	 *            ClassDescriptor naming a class
	 * @return Set of ClassDescriptors which are the known subtypes of the class
	 * @throws ClassNotFoundException
	 */
	public Set<ClassDescriptor> getSubtypes(ClassDescriptor classDescriptor)
			throws ClassNotFoundException {
		Set<ClassDescriptor> result = subtypeSetMap.get(classDescriptor);
		if (result == null) {
			result = computeKnownSubtypes(classDescriptor);
			subtypeSetMap.put(classDescriptor, result);
		}
		return result;
	}

	/**
	 * Compute set of known subtypes of class named by given ClassDescriptor.
	 *
	 * @param classDescriptor
	 *            a ClassDescriptor
	 * @throws ClassNotFoundException
	 */
	private Set<ClassDescriptor> computeKnownSubtypes(
			ClassDescriptor classDescriptor) throws ClassNotFoundException {
		LinkedList<ClassVertex> workList = new LinkedList<ClassVertex>();

		ClassVertex startVertex = resolveClassVertex(classDescriptor);
		workList.addLast(startVertex);

		Set<ClassDescriptor> result = new HashSet<ClassDescriptor>();

		while (!workList.isEmpty()) {
			ClassVertex current = workList.removeFirst();

			if (result.contains(current.getClassDescriptor())) {
				// Already added this class
				continue;
			}

			// Add class to the result
			result.add(current.getClassDescriptor());

			// Add all known subtype vertices to the work list
			Iterator<InheritanceEdge> i = graph.incomingEdgeIterator(current);
			while (i.hasNext()) {
				InheritanceEdge edge = i.next();
				workList.addLast(edge.getSource());
			}
		}

		return new HashSet<ClassDescriptor>(result);
	}

	/**
	 * Resolve a class named by given ClassDescriptor and return its resolved
	 * ClassVertex.
	 *
	 * @param classDescriptor
	 *            a ClassDescriptor
	 * @return resolved ClassVertex representing the class in the
	 *         InheritanceGraph
	 * @throws ClassNotFoundException
	 *             if the class named by the ClassDescriptor does not exist
	 */
	private ClassVertex resolveClassVertex(ClassDescriptor classDescriptor)
			throws ClassNotFoundException {
		ClassVertex typeVertex = optionallyResolveClassVertex(classDescriptor);

		if (!typeVertex.isResolved()) {
			ClassDescriptor.throwClassNotFoundException(classDescriptor);
		}

		assert typeVertex.isResolved();
		return typeVertex;
	}

	private ClassVertex optionallyResolveClassVertex(
			ClassDescriptor classDescriptor) {
		ClassVertex typeVertex = classDescriptorToVertexMap
				.get(classDescriptor);
		if (typeVertex == null) {
			// We have never tried to resolve this ClassVertex before.
			// Try to find the XClass for this class.
			XClass xclass = ClassInfoRecorder.getInstance().getXClass(
					classDescriptor);
			if (xclass == null) {
				throw new IllegalStateException("We don't have info for "
						+ classDescriptor);
			} else {
				// Add the class and all its superclasses/superinterfaces to the
				// inheritance graph.
				// This will result in a resolved ClassVertex.
				typeVertex = addClassAndGetClassVertex(xclass);
			}
		}
		return typeVertex;
	}

	/**
	 * Add an XClass and all of its supertypes to the InheritanceGraph.
	 *
	 * @param xclass
	 *            an XClass
	 * @return the ClassVertex representing the class in the InheritanceGraph
	 */
	private ClassVertex addClassAndGetClassVertex(XClass xclass) {
		if (xclass == null) {
			throw new IllegalStateException();
		}

		LinkedList<XClass> workList = new LinkedList<XClass>();
		workList.add(xclass);

		while (!workList.isEmpty()) {
			XClass work = workList.removeFirst();
			ClassVertex vertex = classDescriptorToVertexMap.get(work
					.getClassDescriptor());
			if (vertex != null && vertex.isFinished()) {
				// This class has already been processed.
				continue;
			}

			if (vertex == null) {
				vertex = ClassVertex.createResolvedClassVertex(
						work.getClassDescriptor(), work);
				addVertexToGraph(work.getClassDescriptor(), vertex);
			}

			addSupertypeEdges(vertex, workList);

			vertex.setFinished(true);
		}

		return classDescriptorToVertexMap.get(xclass.getClassDescriptor());
	}

	private void addVertexToGraph(ClassDescriptor classDescriptor,
			ClassVertex vertex) {
		assert classDescriptorToVertexMap.get(classDescriptor) == null;

		graph.addVertex(vertex);
		classDescriptorToVertexMap.put(classDescriptor, vertex);

		if (vertex.isResolved()) {
			xclassSet.add(vertex.getXClass());
		}

		// if (vertex.isInterface()) {
		// // There is no need to add additional worklist nodes because
		// // java/lang/Object has no supertypes.
		// addInheritanceEdge(vertex,
		// DescriptorFactory.instance().getClassDescriptor("java/lang/Object"),
		// false, null);
		// }
	}

	/**
	 * Add supertype edges to the InheritanceGraph for given ClassVertex. If any
	 * direct supertypes have not been processed, add them to the worklist.
	 *
	 * @param vertex
	 *            a ClassVertex whose supertype edges need to be added
	 * @param workList
	 *            work list of ClassVertexes that need to have their supertype
	 *            edges added
	 */
	private void addSupertypeEdges(ClassVertex vertex,
			LinkedList<XClass> workList) {
		XClass xclass = vertex.getXClass();

		// Direct superclass
		ClassDescriptor superclassDescriptor = xclass.getSuperclassDescriptor();
		if (superclassDescriptor != null) {
			addInheritanceEdge(vertex, superclassDescriptor, false, workList);
		}

		// Directly implemented interfaces
		for (ClassDescriptor ifaceDesc : xclass.getInterfaceDescriptorList()) {
			addInheritanceEdge(vertex, ifaceDesc, true, workList);
		}
	}

	/**
	 * Add supertype edge to the InheritanceGraph.
	 *
	 * @param vertex
	 *            source ClassVertex (subtype)
	 * @param superclassDescriptor
	 *            ClassDescriptor of a direct supertype
	 * @param isInterfaceEdge
	 *            true if supertype is (as far as we know) an interface
	 * @param workList
	 *            work list of ClassVertexes that need to have their supertype
	 *            edges added (null if no further work will be generated)
	 */
	private void addInheritanceEdge(ClassVertex vertex,
			ClassDescriptor superclassDescriptor, boolean isInterfaceEdge,
			@CheckForNull LinkedList<XClass> workList) {
		if (superclassDescriptor == null) {
			return;
		}

		ClassVertex superclassVertex = classDescriptorToVertexMap
				.get(superclassDescriptor);
		if (superclassVertex == null) {
			// Haven't encountered this class previously.

			XClass superclassXClass = ClassInfoRecorder.getInstance()
					.getXClass(superclassDescriptor);
			if (superclassXClass == null) {
				// Inheritance graph will be incomplete.
				// Add a dummy node to inheritance graph and report missing
				// class.
				throw new IllegalStateException("We don't have info for "
						+ superclassDescriptor);
			} else {
				// Haven't seen this class before.
				superclassVertex = ClassVertex.createResolvedClassVertex(
						superclassDescriptor, superclassXClass);
				addVertexToGraph(superclassDescriptor, superclassVertex);

				if (workList != null) {
					// We'll want to recursively process the superclass.
					workList.addLast(superclassXClass);
				}
			}
		}
		assert superclassVertex != null;

		if (graph.lookupEdge(vertex, superclassVertex) == null) {

			graph.createEdge(vertex, superclassVertex);
		}
	}

}
