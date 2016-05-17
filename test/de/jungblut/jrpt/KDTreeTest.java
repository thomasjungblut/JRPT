package de.jungblut.jrpt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.jungblut.jrpt.distance.EuclidianDistance;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.math.sparse.SparseDoubleVector;

public class KDTreeTest {

  @Test
  public void testBalanceByFix() throws Exception {
    // this yields to a pretty inbalanced tree (degenerated)
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 1 }),
        new DenseDoubleVector(new double[] { 2 }),
        new DenseDoubleVector(new double[] { 3 }),
        new DenseDoubleVector(new double[] { 4 }),
        new DenseDoubleVector(new double[] { 5 }),
        new DenseDoubleVector(new double[] { 6 }),
        new DenseDoubleVector(new double[] { 7 }), };
    DoubleVector[] bfsResult = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 4 }),
        new DenseDoubleVector(new double[] { 2 }),
        new DenseDoubleVector(new double[] { 6 }),
        new DenseDoubleVector(new double[] { 1 }),
        new DenseDoubleVector(new double[] { 3 }),
        new DenseDoubleVector(new double[] { 5 }),
        new DenseDoubleVector(new double[] { 7 }), };

    tree.constructFromVectors(Arrays.asList(array).stream());

    System.out.println(tree);
    tree.balanceBySort();
    System.out.println(tree);
    int index = 0;
    for (DoubleVector v : tree) {
      assertEquals(bfsResult[index++], v);
    }

  }

  @Test
  public void testKNearestNeighboursRadiusSearch() throws Exception {
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 2, 3 }),
        new DenseDoubleVector(new double[] { 5, 4 }),
        new DenseDoubleVector(new double[] { 9, 6 }),
        new DenseDoubleVector(new double[] { 4, 7 }),
        new DenseDoubleVector(new double[] { 8, 1 }),
        new DenseDoubleVector(new double[] { 7, 2 }), };

    tree.constructFromVectors(Arrays.asList(array).stream());

    double maxDist = new EuclidianDistance()
        .measureDistance(array[0], array[1]);
    List<VectorDistanceTuple<Object>> nearestNeighbours = tree
        .getNearestNeighbours(new DenseDoubleVector(new double[] { 5, 4 }),
            maxDist);
    Collections.sort(nearestNeighbours);
    Collections.reverse(nearestNeighbours);
    assertEquals(4, nearestNeighbours.size());
    assertTrue(array[1] == nearestNeighbours.get(0).getVector());
    assertTrue(nearestNeighbours.get(0).getDistance() <= maxDist);
    assertTrue(array[5] == nearestNeighbours.get(1).getVector());
    assertTrue(nearestNeighbours.get(1).getDistance() <= maxDist);
    assertTrue(array[3] == nearestNeighbours.get(2).getVector());
    assertTrue(nearestNeighbours.get(2).getDistance() <= maxDist);
    assertTrue(array[0] == nearestNeighbours.get(3).getVector());
    assertTrue(nearestNeighbours.get(3).getDistance() <= maxDist);

  }

  @Test
  public void testInsert() throws Exception {
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 2, 3 }),
        new DenseDoubleVector(new double[] { 5, 4 }),
        new DenseDoubleVector(new double[] { 9, 6 }),
        new DenseDoubleVector(new double[] { 4, 7 }),
        new DenseDoubleVector(new double[] { 8, 1 }),
        new DenseDoubleVector(new double[] { 7, 2 }), };

    tree.constructFromVectors(Arrays.asList(array).stream());

    DoubleVector[] result = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 2, 3 }),
        new DenseDoubleVector(new double[] { 8, 1 }),
        new DenseDoubleVector(new double[] { 5, 4 }),
        new DenseDoubleVector(new double[] { 7, 2 }),
        new DenseDoubleVector(new double[] { 4, 7 }),
        new DenseDoubleVector(new double[] { 9, 6 }) };

    int index = 0;
    Iterator<DoubleVector> iterator = tree.iterator();
    while (iterator.hasNext()) {
      DoubleVector next = iterator.next();
      assertEquals(result[index++], next);
    }
    assertEquals(result.length, index);
  }

  @Test
  public void testKNearestNeighbours() throws Exception {
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 2, 3 }),
        new DenseDoubleVector(new double[] { 5, 4 }),
        new DenseDoubleVector(new double[] { 9, 6 }),
        new DenseDoubleVector(new double[] { 4, 7 }),
        new DenseDoubleVector(new double[] { 8, 1 }),
        new DenseDoubleVector(new double[] { 7, 2 }), };

    tree.constructFromVectors(Arrays.asList(array).stream());

    List<VectorDistanceTuple<Object>> nearestNeighbours = tree
        .getNearestNeighbours(new DenseDoubleVector(new double[] { 0, 0 }), 1);
    assertEquals(1, nearestNeighbours.size());
    assertTrue(array[0] == nearestNeighbours.get(0).getVector());
  }

  @Test
  public void testMedian() throws Exception {
    assertEquals(1,
        KDTree.median(new DenseDoubleVector(new double[] { 2, 3 }), 0));
    assertEquals(0,
        KDTree.median(new DenseDoubleVector(new double[] { 9, 6 }), 0));
    assertEquals(2,
        KDTree.median(new DenseDoubleVector(new double[] { 9, 6, 8 }), 0));
    assertEquals(1,
        KDTree.median(new DenseDoubleVector(new double[] { 9, 8, 7 }), 0));
    assertEquals(0,
        KDTree.median(new DenseDoubleVector(new double[] { 8, 9, 6 }), 0));

    assertEquals(
        1,
        KDTree.median(new DenseDoubleVector(new double[] { 8, 9, 6, 19, 25, 2,
            3, 4 }), 8));
  }

  @Test
  public void testRangeQuery() throws Exception {
    DoubleVector[] array = new DoubleVector[] {
        new DenseDoubleVector(new double[] { 2 }),
        new DenseDoubleVector(new double[] { 3 }),
        new DenseDoubleVector(new double[] { 4 }),
        new DenseDoubleVector(new double[] { 5 }),
        new DenseDoubleVector(new double[] { 6 }),
        new DenseDoubleVector(new double[] { 8 }), };

    KDTree<Object> tree = new KDTree<>();

    tree.constructFromVectors(Arrays.asList(array).stream());

    List<DoubleVector> rangeQuery = tree.rangeQuery(new DenseDoubleVector(
        new double[] { 4 }), new DenseDoubleVector(new double[] { 8 }));
    assertEquals(4, rangeQuery.size());
    for (int i = 2; i < array.length; i++)
      assertEquals(array[i], rangeQuery.get(i - 2));

    array = new DoubleVector[] { new DenseDoubleVector(new double[] { 2 }),
        new DenseDoubleVector(new double[] { 8 }),
        new DenseDoubleVector(new double[] { 4 }),
        new DenseDoubleVector(new double[] { 3 }),
        new DenseDoubleVector(new double[] { 6 }),
        new DenseDoubleVector(new double[] { 5 }) };

    tree = new KDTree<>();
    tree.constructFromVectors(Arrays.asList(array).stream());

    rangeQuery = tree.rangeQuery(new DenseDoubleVector(new double[] { 4 }),
        new DenseDoubleVector(new double[] { 8 }));
    assertEquals(4, rangeQuery.size());
  }

  @Test
  public void testStrictHigherLower() throws Exception {
    DenseDoubleVector lower = new DenseDoubleVector(new double[] { 2 });
    DenseDoubleVector current = new DenseDoubleVector(new double[] { 5 });
    DenseDoubleVector upper = new DenseDoubleVector(new double[] { 10 });

    assertTrue(KDTree.strictHigher(lower, current));
    assertTrue(KDTree.strictHigher(current, upper));
    assertTrue(KDTree.strictLower(upper, current));
    assertTrue(KDTree.strictLower(current, lower));

  }

  /*
   * sparse test vectors
   */

  @Test
  public void testInsertSparse() throws Exception {
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new SparseDoubleVector(new double[] { 2, 3 }),
        new SparseDoubleVector(new double[] { 5, 4 }),
        new SparseDoubleVector(new double[] { 9, 6 }),
        new SparseDoubleVector(new double[] { 4, 7 }),
        new SparseDoubleVector(new double[] { 8, 1 }),
        new SparseDoubleVector(new double[] { 7, 2 }), };

    DoubleVector[] result = new DoubleVector[] {
        new SparseDoubleVector(new double[] { 2, 3 }),
        new SparseDoubleVector(new double[] { 8, 1 }),
        new SparseDoubleVector(new double[] { 5, 4 }),
        new SparseDoubleVector(new double[] { 7, 2 }),
        new SparseDoubleVector(new double[] { 4, 7 }),
        new SparseDoubleVector(new double[] { 9, 6 }) };

    tree.constructFromVectors(Arrays.asList(array).stream());

    int index = 0;
    Iterator<DoubleVector> iterator = tree.iterator();
    while (iterator.hasNext()) {
      DoubleVector next = iterator.next();
      assertEquals(result[index++], next);
    }
    assertEquals(array.length, index);
  }

  @Test
  public void testKNearestNeighboursSparse() throws Exception {
    KDTree<Object> tree = new KDTree<>();
    DoubleVector[] array = new DoubleVector[] {
        new SparseDoubleVector(new double[] { 2, 3 }),
        new SparseDoubleVector(new double[] { 5, 4 }),
        new SparseDoubleVector(new double[] { 9, 6 }),
        new SparseDoubleVector(new double[] { 4, 7 }),
        new SparseDoubleVector(new double[] { 8, 1 }),
        new SparseDoubleVector(new double[] { 7, 2 }), };

    tree.constructFromVectors(Arrays.asList(array).stream());

    List<VectorDistanceTuple<Object>> nearestNeighbours = tree
        .getNearestNeighbours(new SparseDoubleVector(new double[] { 0, 0 }), 1);
    assertEquals(1, nearestNeighbours.size());
    assertTrue(array[0] == nearestNeighbours.get(0).getVector());
  }

  @Test
  public void testMedianSparse() throws Exception {
    assertEquals(1,
        KDTree.median(new SparseDoubleVector(new double[] { 2, 3 }), 0));
    assertEquals(0,
        KDTree.median(new SparseDoubleVector(new double[] { 9, 6 }), 0));
    assertEquals(2,
        KDTree.median(new SparseDoubleVector(new double[] { 9, 6, 8 }), 0));
    assertEquals(1,
        KDTree.median(new SparseDoubleVector(new double[] { 9, 8, 7 }), 0));
    assertEquals(0,
        KDTree.median(new SparseDoubleVector(new double[] { 8, 9, 6 }), 0));

    assertEquals(
        7,
        KDTree.median(new SparseDoubleVector(new double[] { 8, 9, 6, 19, 25, 2,
            3, 4 }), 0));
  }

}
