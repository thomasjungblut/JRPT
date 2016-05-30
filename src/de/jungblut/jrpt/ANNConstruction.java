package de.jungblut.jrpt;

import java.util.stream.Stream;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.tuple.Tuple;

/**
 * Construction interface for ANN (approximate nearest neighbours).
 * 
 * @param <VALUE> the value type of the payload.
 */
public interface ANNConstruction<VALUE> {

  /**
   * Adds a new vector to the tree with the given payload. The payload may be
   * null, the vector is not allowed to be non-null.
   * 
   * @param v the non-null vector.
   * @param payload the maybe null payload for this vector.
   */
  public void add(DoubleVector v, VALUE payload);

  /**
   * Adds the given vector stream into the tree.
   */
  public void addVectorStream(Stream<DoubleVector> vectors);

  /**
   * Adds the given stream of vector/payload pairs into the tree.
   */
  public void addStream(Stream<Tuple<DoubleVector, VALUE>> pairs);

}
