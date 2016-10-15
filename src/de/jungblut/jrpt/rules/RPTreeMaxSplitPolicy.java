package de.jungblut.jrpt.rules;

import java.util.List;
import java.util.Random;

import de.jungblut.jrpt.ANNTree.TreeNode;
import de.jungblut.jrpt.distance.EuclidianDistance;
import de.jungblut.math.DoubleVector;

/**
 * Based on Section 2.3 of the paper
 * "Random projection trees and low dimensional manifolds" by Sanjoy Dasgupta
 * and Yoav Freund.
 * 
 * http://cseweb.ucsd.edu/~dasgupta/papers/rptree-stoc.pdf
 * 
 * Since the split policy (and our tree building algorithm) are stochastic, we
 * need heuristic around the "limited knowledge" that we have in our tree so
 * far.
 * 
 * 
 * @author thomas.jungblut
 *
 */
public final class RPTreeMaxSplitPolicy<VALUE> implements SplitPolicy<VALUE> {

  // TODO expose via constructor
  private final double sampleSizePercent = 0.2;
  private final EuclidianDistance dist = new EuclidianDistance();
  private final Random rand = new Random();

  @Override
  public int splitDimension(DoubleVector v, int level,
      List<TreeNode<VALUE>> treeNodes) {

    int randomDirection = rand.nextInt(v.getDimension());
    
    

    return randomDirection;
  }
}
