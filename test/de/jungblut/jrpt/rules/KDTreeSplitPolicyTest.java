package de.jungblut.jrpt.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jungblut.jrpt.rules.KDTreeSplitPolicy;
import de.jungblut.jrpt.rules.SplitPolicy;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.math.sparse.SparseDoubleVector;

public class KDTreeSplitPolicyTest {

  private SplitPolicy splitPolicy = new KDTreeSplitPolicy();

  @Test
  public void testMedian() throws Exception {
    assertEquals(1, splitPolicy.splitDimension(new DenseDoubleVector(
        new double[] { 2, 3 }), 0));
    assertEquals(0, splitPolicy.splitDimension(new DenseDoubleVector(
        new double[] { 9, 6 }), 0));
    assertEquals(2, splitPolicy.splitDimension(new DenseDoubleVector(
        new double[] { 9, 6, 8 }), 0));
    assertEquals(1, splitPolicy.splitDimension(new DenseDoubleVector(
        new double[] { 9, 8, 7 }), 0));
    assertEquals(0, splitPolicy.splitDimension(new DenseDoubleVector(
        new double[] { 8, 9, 6 }), 0));

    assertEquals(
        1,
        splitPolicy.splitDimension(new DenseDoubleVector(new double[] { 8, 9,
            6, 19, 25, 2, 3, 4 }), 8));
  }

  @Test
  public void testMedianSparse() throws Exception {
    assertEquals(1, splitPolicy.splitDimension(new SparseDoubleVector(
        new double[] { 2, 3 }), 0));
    assertEquals(0, splitPolicy.splitDimension(new SparseDoubleVector(
        new double[] { 9, 6 }), 0));
    assertEquals(
        2,
        splitPolicy.splitDimension(new SparseDoubleVector(new double[] { 9, 6,
            8 }), 0));
    assertEquals(
        1,
        splitPolicy.splitDimension(new SparseDoubleVector(new double[] { 9, 8,
            7 }), 0));
    assertEquals(
        0,
        splitPolicy.splitDimension(new SparseDoubleVector(new double[] { 8, 9,
            6 }), 0));

    assertEquals(
        7,
        splitPolicy.splitDimension(new SparseDoubleVector(new double[] { 8, 9,
            6, 19, 25, 2, 3, 4 }), 0));
  }

}
