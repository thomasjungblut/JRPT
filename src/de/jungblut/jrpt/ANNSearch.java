package de.jungblut.jrpt;

import java.util.List;

import de.jungblut.math.DoubleVector;

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
}
