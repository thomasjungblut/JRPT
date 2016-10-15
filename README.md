JRPT
====

This library contains containers for approximate nearest neighbour searches in n-dimensional spaces.
It features a KD-Tree and a Random Projection Tree, "RPT" which gives this library its name.

All trees can be built in a stochastic fashion, meaning you can feed it one element at a time and query as you go. 

Features / TODO
--------
- [x] KD Tree
- [ ] Random Projection Tree (in progress)
- [ ] refinement layer that computes multiple trees and combines the result for higher accuracy
- [ ] MinHashing
- [ ] Benchmarks of accuracy vs. construction time 

Sample Usage
===================

KD Tree
-------

The most common functionality of using a kdtree is to get the k-nearest neighbours of a vector like this:

```java

KDTree<String> tree = new KDTree<>();
tree.add(new DenseDoubleVector(new double[] { 1, 2, 3 }), "1 2 3");
tree.add(new DenseDoubleVector(new double[] { 20, 30, 40 }), "20 30 40");
tree.add(new DenseDoubleVector(new double[] { 4, 5, 6 }), "4 5 6");

List<VectorDistanceTuple<String>> nearestNeighbours = 
   tree.getNearestNeighbours(new DenseDoubleVector(new double[] { 2, 3, 4 }), 2);

// yields
// [4.0, 5.0, 6.0] - 4 5 6 -> 3.4641016151377544, 
// [1.0, 2.0, 3.0] - 1 2 3 -> 1.7320508075688772
```

You can balance the tree after a bulk insert from a stream to improve the lookup time:

```java

KDTree<String> tree = new KDTree<>();
tree.addVectorStream(() -> ...);
tree.balance();

// do the lookups

```

 
License
-------

Since I am Apache committer, I consider everything inside of this repository
licensed by Apache 2.0 license, although I haven't put the usual header into the source files.

If something is not licensed via Apache 2.0, there is a reference or an additional licence header included in the specific source file.

Maven
-----

If you use maven, you can get the latest release using the following dependency:

```
 <dependency>
     <groupId>de.jungblut.jrpt</groupId>
     <artifactId>jrpt</artifactId>
     <version>0.1</version>
 </dependency>
```

Build
-----

You will need Java 8 to build this library.

You can simply build with:

> mvn clean package install

The created jars contains debuggable code + sources + javadocs.

If you want to skip testcases you can use:

> mvn clean package install -DskipTests

If you want to skip the signing process you can do:

> mvn clean package install -Dgpg.skip=true