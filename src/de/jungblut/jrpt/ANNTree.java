package de.jungblut.jrpt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

import de.jungblut.jrpt.distance.EuclidianDistance;
import de.jungblut.jrpt.rules.SplitPolicy;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.DoubleVector.DoubleVectorElement;
import de.jungblut.math.tuple.Tuple;

/**
 * Generalized ANN tree structure that splits along a given split policy. This
 * is the base class for implementing KD and RP trees.
 *
 * @param <VALUE> the payload type.
 */
public class ANNTree<VALUE> implements ANN<VALUE> {

  public static final class TreeNode<VALUE> {
    final int splitDimension;
    // keyvector by the value in the split dimension
    final DoubleVector keyVector;
    final VALUE value;

    TreeNode<VALUE> left;
    TreeNode<VALUE> right;

    public TreeNode(int splitDimension, DoubleVector keyVector, VALUE val) {
      this.splitDimension = splitDimension;
      this.keyVector = keyVector;
      this.value = val;
    }

    public double splitValue() {
      return keyVector.get(splitDimension);
    }

    @Override
    public String toString() {
      return "Node [splitDimension=" + splitDimension + ", value=" + keyVector
          + "]";
    }
  }

  private final SplitPolicy<VALUE> splitRule;
  private final List<TreeNode<VALUE>> treeNodes = new ArrayList<>();

  private int vectorDimension;
  private TreeNode<VALUE> root;
  private int size;

  public ANNTree(SplitPolicy<VALUE> splitRule) {
    this.splitRule = Preconditions.checkNotNull(splitRule, "splitRule");
  }

  @Override
  public void addVectorStream(Stream<DoubleVector> vectors) {
    vectors.forEach((v) -> add(v, null));
  }

  @Override
  public void addStream(Stream<Tuple<DoubleVector, VALUE>> pairs) {
    pairs.forEach((v) -> add(v.getFirst(), v.getSecond()));
  }

  @Override
  public void add(DoubleVector vec, VALUE value) {
    Preconditions.checkNotNull(vec, "vector");
    validateVector(vec);

    // we always increment size at the beginning, given the guarantee that every
    // add creates a new node in the tree.
    size++;

    // shortcut for empty tree
    if (root == null) {
      root = new TreeNode<VALUE>(splitRule.splitDimension(vec, 0, treeNodes),
          vec, value);
      return;
    }

    TreeNode<VALUE> current = root;
    int level = 0;
    boolean right = false;
    // traverse the tree to the free spot that matches the dimension
    while (true) {
      right = current.splitValue() <= vec.get(current.splitDimension);
      TreeNode<VALUE> next = right ? current.right : current.left;
      if (next == null) {
        break;
      } else {
        current = next;
      }
      level++;
    }

    int splitDimension = splitRule.splitDimension(vec, level, treeNodes);
    Preconditions.checkElementIndex(splitDimension, vec.getDimension(),
        "split returned invalid index!");

    // do the "real" insert
    // note that current in this case is the parent
    TreeNode<VALUE> n = new TreeNode<VALUE>(splitDimension, vec, value);
    treeNodes.add(n);
    if (right) {
      current.right = n;
    } else {
      current.left = n;
    }
  }

  private void validateVector(DoubleVector vec) {
    Preconditions.checkArgument(vec.getDimension() != 0,
        "vector dimension can't be zero");
    if (vectorDimension == 0) {
      vectorDimension = vec.getDimension();
    } else {
      Preconditions.checkArgument(vectorDimension == vec.getDimension(),
          "Dimensional mismatch between vector and tree. Expected "
              + vectorDimension + " but given " + vec.getDimension() + "!");
    }
  }

  @Override
  public void balance() {

    Collections.sort(treeNodes, (o1, o2) -> Doubles.compare(
        o1.keyVector.get(o1.splitDimension),
        o2.keyVector.get(o2.splitDimension)));

    // do an inverse binary search to build up the tree from the root
    root = fix(treeNodes, 0, treeNodes.size() - 1);
  }

  /**
   * Fixup the tree recursively by divide and conquering the sorted array.
   */
  private TreeNode<VALUE> fix(List<TreeNode<VALUE>> nodes, int start, int end) {
    if (start > end) {
      return null;
    } else {
      int mid = (start + end) >>> 1;
      TreeNode<VALUE> midNode = nodes.get(mid);
      midNode.left = fix(nodes, start, mid - 1);
      midNode.right = fix(nodes, mid + 1, end);
      return midNode;
    }
  }

  @Override
  public List<VectorDistanceTuple<VALUE>> rangeQuery(DoubleVector lower,
      DoubleVector upper) {
    List<VectorDistanceTuple<VALUE>> list = Lists.newArrayList();
    List<TreeNode<VALUE>> rangeInternal = rangeInternal(lower, upper);
    for (TreeNode<VALUE> node : rangeInternal) {
      list.add(new VectorDistanceTuple<VALUE>(node.keyVector, node.value, 0));
    }
    return list;
  }

  private List<TreeNode<VALUE>> rangeInternal(DoubleVector lower,
      DoubleVector upper) {
    List<TreeNode<VALUE>> list = Lists.newArrayList();
    Deque<TreeNode<VALUE>> toVisit = new ArrayDeque<>();
    toVisit.add(root);
    while (!toVisit.isEmpty()) {
      TreeNode<VALUE> next = toVisit.pop();
      if (strictLower(upper, next.keyVector)
          && strictHigher(lower, next.keyVector)) {
        list.add(next);
      }

      if (next.left != null && checkSubtree(lower, upper, next.left)) {
        toVisit.add(next.left);
      }
      if (next.right != null && checkSubtree(lower, upper, next.right)) {
        toVisit.add(next.right);
      }
    }
    return list;
  }

  /**
   * checks if the given node is inside the range based on the split.
   */
  private boolean checkSubtree(DoubleVector lower, DoubleVector upper,
      TreeNode<VALUE> next) {
    if (next != null) {
      boolean greater = lower.get(next.splitDimension) >= next.keyVector
          .get(next.splitDimension);
      boolean lower2 = upper.get(next.splitDimension) >= next.keyVector
          .get(next.splitDimension);
      return greater || lower2;
    }
    return false;
  }

  @Override
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, int k) {
    return getNearestNeighbours(vec, k, Double.MAX_VALUE);
  }

  @Override
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, double radius) {
    return getNearestNeighbours(vec, Integer.MAX_VALUE, radius);
  }

  @Override
  public List<VectorDistanceTuple<VALUE>> getNearestNeighbours(
      DoubleVector vec, int k, double radius) {
    LimitedPriorityQueue<VectorDistanceTuple<VALUE>> queue = new LimitedPriorityQueue<>(
        k);
    HyperRectangle hr = HyperRectangle.infiniteHyperRectangle(vec
        .getDimension());
    getNearestNeighbourInternal(root, vec, hr, radius, k, radius, queue);
    return queue.toList();
  }

  /**
   * Euclidian distance based recursive algorithm for nearest neighbour queries
   * based on Andrew W. Moore.
   */
  private void getNearestNeighbourInternal(TreeNode<VALUE> current,
      DoubleVector target, HyperRectangle hyperRectangle,
      double maxDistSquared, int k, final double radius,
      LimitedPriorityQueue<VectorDistanceTuple<VALUE>> queue) {
    if (current == null) {
      return;
    }
    int s = current.splitDimension;
    DoubleVector pivot = current.keyVector;
    double distancePivotToTarget = EuclidianDistance.get().measureDistance(
        pivot, target);

    HyperRectangle leftHyperRectangle = hyperRectangle;
    HyperRectangle rightHyperRectangle = new HyperRectangle(
        hyperRectangle.min.deepCopy(), hyperRectangle.max.deepCopy());
    leftHyperRectangle.max.set(s, pivot.get(s));
    rightHyperRectangle.min.set(s, pivot.get(s));
    boolean left = target.get(s) > pivot.get(s);
    TreeNode<VALUE> nearestNode;
    HyperRectangle nearestHyperRectangle;
    TreeNode<VALUE> furtherstNode;
    HyperRectangle furtherstHyperRectangle;
    if (left) {
      nearestNode = current.left;
      nearestHyperRectangle = leftHyperRectangle;
      furtherstNode = current.right;
      furtherstHyperRectangle = rightHyperRectangle;
    } else {
      nearestNode = current.right;
      nearestHyperRectangle = rightHyperRectangle;
      furtherstNode = current.left;
      furtherstHyperRectangle = leftHyperRectangle;
    }
    getNearestNeighbourInternal(nearestNode, target, nearestHyperRectangle,
        maxDistSquared, k, radius, queue);

    double distanceSquared = queue.isFull() ? queue.getMaximumPriority()
        : Double.MAX_VALUE;
    maxDistSquared = Math.min(maxDistSquared, distanceSquared);
    DoubleVector closest = furtherstHyperRectangle.closestPoint(target);
    double closestDistance = EuclidianDistance.get().measureDistance(closest,
        target);
    // check subtrees even if they aren't in your maxDist but within our radius
    if (closestDistance < maxDistSquared || closestDistance < radius) {
      if (distancePivotToTarget < distanceSquared) {
        distanceSquared = distancePivotToTarget > 0d ? distancePivotToTarget
            : distanceSquared;
        // check if we are within our defined radius
        if (distancePivotToTarget <= radius) {
          queue.add(new VectorDistanceTuple<>(current.keyVector, current.value,
              distancePivotToTarget), distancePivotToTarget);
        }
        maxDistSquared = queue.isFull() ? queue.getMaximumPriority()
            : Double.MAX_VALUE;
        maxDistSquared = Math.min(maxDistSquared, distanceSquared);
      }
      // now inspect the furthest away node as well
      getNearestNeighbourInternal(furtherstNode, target,
          furtherstHyperRectangle, maxDistSquared, k, radius, queue);
    }
  }

  @Override
  public Iterator<DoubleVector> iterator() {
    return new VectorBFSIterator();
  }

  // iterator for the implementation detail of tree nodes for test cases and
  // additional asserts
  Iterator<TreeNode<VALUE>> iterateNodes() {
    return new BreadthFirstIterator();
  }

  /**
   * @return the size of the kd-tree.
   */
  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    prettyPrintIternal(root, sb, 0);
    return sb.toString();
  }

  private StringBuilder prettyPrintIternal(TreeNode<VALUE> node,
      StringBuilder sb, int depth) {
    if (node != null) {
      sb.append("\n").append(Strings.repeat("\t", depth));
      sb.append(node.keyVector + " " + node.splitDimension);
      prettyPrintIternal(node.left, sb, depth + 1);
      prettyPrintIternal(node.right, sb, depth + 1);
    }
    return sb;
  }

  static boolean strictHigher(DoubleVector lower, DoubleVector current) {
    Iterator<DoubleVectorElement> iterateNonZero = lower.iterateNonZero();
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      if (current.get(next.getIndex()) < next.getValue())
        return false;
    }
    return true;
  }

  static boolean strictLower(DoubleVector upper, DoubleVector current) {
    Iterator<DoubleVectorElement> iterateNonZero = upper.iterateNonZero();
    while (iterateNonZero.hasNext()) {
      DoubleVectorElement next = iterateNonZero.next();
      if (current.get(next.getIndex()) > next.getValue())
        return false;
    }
    return true;
  }

  private final class BreadthFirstIterator extends
      AbstractIterator<TreeNode<VALUE>> {

    private final Deque<TreeNode<VALUE>> toVisit = new ArrayDeque<>();
    TreeNode<VALUE> current;

    public BreadthFirstIterator() {
      toVisit.add(root);
    }

    @Override
    protected TreeNode<VALUE> computeNext() {
      current = toVisit.poll();
      if (current != null) {
        if (current.left != null) {
          toVisit.add(current.left);
        }
        if (current.right != null) {
          toVisit.add(current.right);
        }
        return current;
      }
      return endOfData();
    }
  }

  private final class VectorBFSIterator extends AbstractIterator<DoubleVector> {

    private BreadthFirstIterator inOrderIterator;

    public VectorBFSIterator() {
      inOrderIterator = new BreadthFirstIterator();
    }

    @Override
    protected DoubleVector computeNext() {
      TreeNode<VALUE> next = inOrderIterator.computeNext();
      return next != null ? next.keyVector : endOfData();
    }

  }

}
