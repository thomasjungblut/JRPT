package de.jungblut.jrpt.rules;

import de.jungblut.math.DoubleVector;

public interface SplitPolicy {

  /**
   * Choose a split dimension for the given vector and tree level.
   * 
   * @param v the vector.
   * @param level the tree level.
   * @return an index of the dimension between 0 and v.getDimension()
   *         (exclusive).
   */
  public int splitDimension(DoubleVector v, int level);

}
