JRPT
====

This library contains containers for approximate nearest neighbour searches in n-dimensional spaces.
It features a KD-Tree (moved from my common library) and a Random Projection Tree, "RPT" which gives this library its name.

On top of the RPT, there is a refinement layer that computes multiple RPTs and combines the result for higher accuracy.    

TODO: 
 -implement the RPT
 -documentation
 -benchmarks (accuracy vs. construction / queue time)  
 
 
 
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