package de.jungblut.jrpt;

import java.util.List;

import de.jungblut.math.DoubleVector;

/**
 * Interface for searching a ANN (approx. nearest neighbour) datastructure
 * structure.
 * 
 * @param <VALUE> the value type of the payload.
 */
public interface ANNSearch<VALUE> {

  /**
   * @return the k nearest neighbors to the given vector.
   */
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, int k);

  /**
   * @return nearest neighbors to the given vector within the given radius.
   */
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, double radius);

  /**
   * @return the k nearest neighbors to the given vector within the given
   *         radius.
   */
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, int k, double radius);

  /**
   * @return the vectors and payload within the range of the lower and upper
   *         bounded vectors.
   */
  public List<VectorDistanceTuple<VALUE>> rangeQuery(DoubleVector lower,
      DoubleVector upper);

}
