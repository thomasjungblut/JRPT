package de.jungblut.jrpt.rules;

import java.util.Iterator;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;

public class KDTreeSplitPolicy implements SplitPolicy {

  @Override
  public int splitDimension(DoubleVector v, int level) {
    return median(v, level);
  }

  /**
   * @return the index of the median of the vector.
   */
  static int median(DoubleVector v, int insertLevel) {
    if (v.getDimension() == 1) {
      return 0;
    }
    if (!v.isSparse()) {
      // speedup for two and three dimensional spaces
      if (v.getDimension() == 2) {
        return medianTwoDimensions(v, 0, 1);
      } else if (v.getDimension() == 3) {
        return medianThreeDimensions(v, 0, 1, 2);
      } else {
        // fall back to modulo on larger vectors
        return (insertLevel + 1) % v.getDimension();
      }
    } else {
      // sparse implementation, basically it finds median on the not zero
      // entries and returns the index.
      final int vectorLength = v.getLength();
      final Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
      if (vectorLength == 2) {
        return medianTwoDimensions(v, iterateNonZero.next().getIndex(),
            iterateNonZero.next().getIndex());
      } else if (vectorLength == 3) {
        return medianThreeDimensions(v, iterateNonZero.next().getIndex(),
            iterateNonZero.next().getIndex(), iterateNonZero.next().getIndex());
      } else {
        return iterateNonZero.next().getIndex();
      }
    }
  }

  private static int medianThreeDimensions(DoubleVector v, int i, int j, int k) {
    boolean greater = v.get(i) > v.get(j);
    int largeIndex = greater ? i : j;
    int smallIndex = !greater ? i : j;

    if (v.get(k) > v.get(largeIndex)) {
      return largeIndex;
    } else {
      if (v.get(smallIndex) > v.get(k)) {
        return smallIndex;
      } else {
        return k;
      }
    }
  }

  private static int medianTwoDimensions(DoubleVector v, int i, int j) {
    return v.get(i) > v.get(j) ? i : j;
  }

}
