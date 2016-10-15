package de.jungblut.jrpt;

import de.jungblut.jrpt.rules.KDTreeSplitPolicy;

/**
 * Implementation of a kd-tree that handles dense vectors as well as sparse
 * vectors. It offers O(log n) best case lookup time, but can degrade to O(n) if
 * the tree isn't balanced well. It is mostly optimized for special cases like
 * two or three dimensional data.
 */
public final class KDTree<VALUE> extends ANNTree<VALUE> {

  public KDTree() {
    super(new KDTreeSplitPolicy<VALUE>());
  }

}
