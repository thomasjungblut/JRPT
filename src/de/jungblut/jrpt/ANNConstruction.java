package de.jungblut.jrpt;

import java.util.stream.Stream;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.tuple.Tuple;

public interface ANNConstruction<VALUE> {

  /**
   * Constructs the ANN searcher from pure vectors.
   */
  public void constructFromVectors(Stream<DoubleVector> vectors);

  /**
   * Constructs the ANN searcher from pairs of vector and metadata (payload).
   */
  public void constructWithPayload(Stream<Tuple<DoubleVector, VALUE>> pairs);

}
