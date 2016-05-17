package de.jungblut.jrpt;

import com.google.common.base.Preconditions;

import de.jungblut.math.DoubleVector;

/**
 * Tuple for holding element information from an ANN. Comparable is implemented
 * on the distance, enabling a descending sort mainly designed for use in a
 * {@link LimitedPriorityQueue}.
 * 
 * @author thomas.jungblut
 *
 * @param <VALUE>
 */
public final class VectorDistanceTuple<VALUE> implements
    Comparable<VectorDistanceTuple<VALUE>> {

  private final DoubleVector keyVector;
  private final VALUE value;
  private final double dist;

  public VectorDistanceTuple(DoubleVector keyVector, VALUE value, double dist) {
    this.keyVector = Preconditions.checkNotNull(keyVector, "keyVector");
    this.value = value;
    this.dist = dist;
  }

  public double getDistance() {
    return dist;
  }

  public DoubleVector getVector() {
    return keyVector;
  }

  public VALUE getValue() {
    return value;
  }

  @Override
  public int compareTo(VectorDistanceTuple<VALUE> o) {
    return Double.compare(o.dist, dist);
  }

  @Override
  public String toString() {
    return keyVector + " - " + value + " -> " + dist;
  }
}
