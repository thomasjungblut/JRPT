package de.jungblut.jrpt.rules;

import de.jungblut.math.DoubleVector;

public interface SplitPolicy {

  public int splitDimension(DoubleVector v, int level);

}
