package de.jungblut.jrpt;

import java.util.Iterator;

import de.jungblut.math.DoubleVector;

/**
 * Approximate nearest neighbour interface, this combines the two interfaces for
 * construction and search into one and adds some utility methods that are
 * usually seen in container classes (size, iterator).
 * 
 * The tree gives the following guarantees:
 * <ul>
 * <li>Every addition to the tree creates a new node</li>
 * <li>Payloads (value) to a vector can be null</li>
 * <li>Lookups are approximate, so not guaranteed to exhaustively find all nodes
 * matching the search criterion</li>
 * <li>Vectors added must have the same dimension across all inserts and lookups
 * </li>
 * </ul>
 * 
 * @param <T> the payload type for the data structure.
 */
public interface ANN<T> extends ANNConstruction<T>, ANNSearch<T>,
    Iterable<DoubleVector> {

  /**
   * @return the size of the tree.
   */
  public int size();

  /**
   * Balances this tree by sorting along the split dimensions and rebuilding the
   * entire tree.
   */
  public void balance();

  /**
   * Iterates over the vectors in the tree.
   */
  @Override
  public Iterator<DoubleVector> iterator();

}
