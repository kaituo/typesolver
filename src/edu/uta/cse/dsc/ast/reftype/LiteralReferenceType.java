package edu.uta.cse.dsc.ast.reftype;

import java.lang.reflect.Type;

/**
 * Type (class, mock-class, interface, array, null).
 *
 * @author csallner@uta.edu (Christoph Csallner)
 */
public abstract class LiteralReferenceType
implements ReferenceType
{

  /**
   * Register all (including direct, reflexive, and transitive)
   * super-types of this type.
   */
  protected abstract void registerSuperTypes();

  public abstract Type getType();

  /**
   * @return global identifier of this type.
   * I.e., there are no two different types that have the same global id.
   */
  public abstract int getGlobalId();

  public abstract boolean isAbstract();

  public abstract boolean isArray();

  public abstract boolean isFinal();

  public abstract boolean isInterface();

  /**
   * @see http://java.sun.com/docs/books/jvms/second_edition/html/Concepts.doc.html#18914
   */
  public abstract boolean isPublic();
}