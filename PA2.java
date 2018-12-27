/* Vinnie Zhang
 * Basic Algorithms - Assignment #2
 * Victor Schoup - Mon/Wed
 * Hijacking the Fees - updated twoThreeTree implementation
 */

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.Scanner;

public class Solution {

    public static void main(String[] args) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        
        // reads input file and create an instance of a twoThree tree   
        Scanner input = new Scanner(System.in);
        TwoThreeTree tree = new TwoThreeTree();
        
        // read first line, which is always the total # of queries
        int numOfQueries = input.nextInt();    
        
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        
        // iterates through the all lines of input
        for (int i = 0; i < numOfQueries; i++) {

            int type_of_query = input.nextInt(); // determines what to do with the info on each line
            
            String planet1, planet2; // planets specified
            String lower, upper; // upper and lower bounds of planets when comparing their lexicographic order
            int fee; // entrance fee of planet
            
            planet1 = input.next(); // first planet always
            
            if (type_of_query == 1) { // need to add planet and fee value to the tree
                fee = input.nextInt();
                insert(planet1, fee, tree); // adding the planet/fee node into the tree
            } else if (type_of_query == 2) { // need to add new fee to all planets within the given range (inclusive)
                planet2 = input.next();
                fee = input.nextInt();
                
                 // determining the lexicographical order of the given planets
                if (planet1.compareTo(planet2) <= 0) {
                    lower = planet1;
                    upper = planet2;
                } else {
                    lower = planet2;
                    upper = planet1;
                }
                
                doUpdate("", lower, upper, fee, tree.root, tree.height, output); // updating current fees to planets
                
            } else if (type_of_query == 3) { // need to return the value of the planet specified
                fee = search(planet1, tree); // searching for the planet within the tree to find the fee to print
                System.out.println(fee);
            }

         }  
        
        output.flush(); // closing the BufferedWriter
        
    }
    
    static int search(String planet1, TwoThreeTree tree) {
        
        // searching for the planet fee corresponding to the input planet
        return doSearch(planet1, 0, tree.root, tree.height);
    }
    
    static int doSearch(String planet, int update_fee, Node p, int h) {
        
        // recursive search
        update_fee += p.update_fee; // updating the fee by adding the additional fee to the current fee
        
        if (h > 0 ) { // tree has root with children nodes
            
            InternalNode q = (InternalNode) p; // downcast
            
            if (planet.compareTo(q.child0.guide) <= 0) { // planet in guide's subtree (in child0)
                return doSearch(planet, update_fee, q.child0, h-1); // root is now child0 node (root of subtree)
            } else if (planet.compareTo(q.child1.guide) <= 0) { // planet in guide's subtree (in child1)
                return doSearch(planet, update_fee, q.child1, h-1); // root is now child1 node (root of subtree)
            } else if (q.child2 != null && planet.compareTo(q.child2.guide) <= 0) { 
                // ^ there is a third child and the planet node falls within its subtree
                return doSearch(planet, update_fee, q.child2, h-1); // root is now child2 node (root of subtree)
            } else {
                return -1; // planet doesn't fall within any of the children node's subtrees
            }
        } else { // tree's height is 0 or less (one node or less)
            
            LeafNode leaf = (LeafNode) p; // downcast
            
            if (planet.compareTo(leaf.guide) == 0) { // if the planet being added is already in the tree
                return leaf.value + update_fee; // increment the current fee with the new fee
            } else {
                return -1; // otherwise the planet isn't in the tree
            }
        }
    }        
    
    static void doUpdate(String lower_guide, String lower, String upper, int fee, Node p, int h, BufferedWriter output) {
        
        // recursive method
        if (h == 0) { // base case (at leaf node)
            
            LeafNode leaf = (LeafNode) p;
            
            // if within range
            if (lower.compareTo(leaf.guide) <= 0 && upper.compareTo(leaf.guide) >= 0) {
                leaf.update_fee += fee; // updating fee with by the additional fee
            }        
            
            return;
        }
        
        // recursive case --> shovel through the tree nodes
        InternalNode q = (InternalNode) p;
        
        if (lower.compareTo(lower_guide) <= 0 && upper.compareTo(q.guide) >= 0) { // inside range?
            q.update_fee += fee; // updating fee
            return;
        } else if (upper.compareTo(lower_guide) > 0 && lower.compareTo(q.guide) <= 0) {
            
            doUpdate(lower_guide, lower, upper, fee, q.child0, h-1, output);
            doUpdate(q.child0.guide, lower, upper, fee, q.child1, h-1, output);
            
            if (q.child2 != null) { // there is a third child
                doUpdate(q.child1.guide, lower, upper, fee, q.child2, h-1, output);
            }
        }    
    }

    static void insert(String key, int value, TwoThreeTree tree) {
       // insert a key value pair into tree (overwrite existing value
       // if key is already present)

          int h = tree.height;

          if (h == -1) { // if there are no current nodes in the tree
              LeafNode newLeaf = new LeafNode();
              newLeaf.guide = key;
              newLeaf.value = value;
              tree.root = newLeaf; 
              tree.height = 0;
          }
          else { // there's at least one node in the tree
              
             WorkSpace ws = doInsert(key, value, tree.root, h);

             if (ws != null && ws.newNode != null) {
             // create a new root

                InternalNode newRoot = new InternalNode();
                if (ws.offset == 0) {
                   newRoot.child0 = ws.newNode; 
                   newRoot.child1 = tree.root;
                }
                else {
                   newRoot.child0 = tree.root; 
                   newRoot.child1 = ws.newNode;
                }
                resetGuide(newRoot);
                tree.root = newRoot;
                tree.height = h+1;
            }
        }
    }

    static WorkSpace doInsert(String key, int value, Node p, int h) {
    
        // auxiliary recursive routine for insert

        if (h == 0) {
        
            // we're at the leaf level, so compare and 
            // either update value or insert new leaf

            LeafNode leaf = (LeafNode) p; //downcast
            int cmp = key.compareTo(leaf.guide);

            if (cmp == 0) { // if the planet being added is the same as the node we are at
                leaf.value = value - leaf.update_fee; // not sure why we do this
                return null;
            }

            // create new leaf node and insert into tree
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key; 
            newLeaf.value = value;

            int offset = (cmp < 0) ? 0 : 1;
            // offset == 0 => newLeaf inserted as left sibling
            // offset == 1 => newLeaf inserted as right sibling

            WorkSpace ws = new WorkSpace();
            ws.newNode = newLeaf;
            ws.offset = offset;
            ws.scratch = new Node[4];

            return ws;
        }
        
        else {
            InternalNode q = (InternalNode) p; // downcast
            int pos;
            WorkSpace ws;
            
        value -= q.update_fee; // still unsure why we are deducting values down tree

        if (key.compareTo(q.child0.guide) <= 0) {
            pos = 0; 
            ws = doInsert(key, value, q.child0, h-1);
        }
        else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
            pos = 1;
            ws = doInsert(key, value, q.child1, h-1);
        }
        else {
            pos = 2; 
            ws = doInsert(key, value, q.child2, h-1);
        }

        if (ws != null) {
            if (ws.newNode != null) {
               // make ws.newNode child # pos + ws.offset of q

               int sz = copyOutChildren(q, ws.scratch);
               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
               if (sz == 2) {
                  ws.newNode = null;
                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
               }
               else {
                  ws.newNode = new InternalNode();
                  ws.offset = 1;
                   ws.newNode.update_fee = q.update_fee; // copying update_fee when creating a new internal node
                  resetChildren(q, ws.scratch, 0, 2);
                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
               }
            } else if (ws.guideChanged) {
                 ws.guideChanged = resetGuide(q);
            }
        }
       
        return ws;
        
        }
   }


   static int copyOutChildren(InternalNode q, Node[] x) {
   // copy children of q into x, and return # of children

      int sz = 2;
      x[0] = q.child0; x[1] = q.child1;
      if (q.child2 != null) {
         x[2] = q.child2; 
         sz = 3;
      }
      return sz;
   }

   static void insertNode(Node[] x, Node p, int sz, int pos) {
   // insert p in x[0..sz) at position pos,
   // moving existing extries to the right

      for (int i = sz; i > pos; i--)
         x[i] = x[i-1];

      x[pos] = p;
   }

   static boolean resetGuide(InternalNode q) {
   // reset q.guide, and return true if it changes.

      String oldGuide = q.guide;
      if (q.child2 != null)
         q.guide = q.child2.guide;
      else
         q.guide = q.child1.guide;

      return q.guide != oldGuide;
   }


   static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
   // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
   // also resets guide, and returns the result of that

      q.child0 = x[pos]; 
      q.child1 = x[pos+1];

      if (sz == 3) 
         q.child2 = x[pos+2];
      else
         q.child2 = null;

      return resetGuide(q);
   }
}

    class Node {
       String guide;
       // guide points to max key in subtree rooted at node
       int update_fee = 0; // updates values storead at each node to perform range update
    }

    class InternalNode extends Node {
       Node child0, child1, child2;
       // child0 and child1 are always non-null
       // child2 is null iff node has only 2 children
    }

    class LeafNode extends Node {
       // guide points to the key

       int value;
    }

    class TwoThreeTree {
       Node root;
       int height;

       TwoThreeTree() {
          root = null;
          height = -1;
       }
    }

    class WorkSpace {
    // this class is used to hold return values for the recursive doInsert
    // routine

       Node newNode;
       int offset;
       boolean guideChanged;
       Node[] scratch;
    
}