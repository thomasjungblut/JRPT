package de.jungblut.jrpt;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;

public final class HyperRectangle {

  protected DoubleVector min;
  protected DoubleVector max;

  public HyperRectangle(DoubleVector min, DoubleVector max) {
    this.min = min;
    this.max = max;
  }

  public DoubleVector closestPoint(DoubleVector t) {
    DoubleVector p = new DenseDoubleVector(t.getDimension());
    for (int i = 0; i < t.getDimension(); ++i) {
      if (t.get(i) <= min.get(i)) {
        p.set(i, min.get(i));
      } else if (t.get(i) >= max.get(i)) {
        p.set(i, max.get(i));
      } else {
        p.set(i, t.get(i));
      }
    }
    return p;
  }

  public static HyperRectangle infiniteHyperRectangle(int dimension) {
    DoubleVector min = new DenseDoubleVector(dimension);
    DoubleVector max = new DenseDoubleVector(dimension);
    for (int i = 0; i < dimension; ++i) {
      min.set(i, Double.NEGATIVE_INFINITY);
      max.set(i, Double.POSITIVE_INFINITY);
    }

    return new HyperRectangle(min, max);
  }

  @Override
  public String toString() {
    return "min: " + min + " ; max: " + max;
  }
}
