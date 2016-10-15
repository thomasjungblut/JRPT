package de.jungblut.jrpt.rules;

import java.util.List;

import de.jungblut.jrpt.ANNTree.TreeNode;
import de.jungblut.math.DoubleVector;

public interface SplitPolicy<VALUE> {

  /**
   * Choose a split dimension for the given vector and tree level.
   * 
   * @param v the vector.
   * @param level the tree level.
   * @param treeNodes the tree nodes that were added so far.
   * @return an index of the dimension between 0 and v.getDimension()
   *         (exclusive).
   */
  public int splitDimension(DoubleVector v, int level,
      List<TreeNode<VALUE>> treeNodes);

}
