JRPT
====

This library contains containers for approximate nearest neighbour searches in n-dimensional spaces.
It features a KD-Tree (moved from my common library) and a Random Projection Tree, "RPT" which gives this library its name.

On top of the RPT, there is a refinement layer that computes multiple RPTs and combines the result for higher accuracy.    

TODO: 
 -implement the RPT
 -documentation
 -benchmarks (accuracy vs. construction / queue time)  