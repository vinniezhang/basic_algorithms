/* Vinnie Zhang
 * Basic Algorithms - Assignment #1
 * Victor Schoup - Mon/Wed
 * Ford's Dilemma - twoThreeTree implementation
 */

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.Scanner;

public class Solution { // class twoThree is the same as class Solution

    public static void main(String[] args) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        
        TwoThreeTree tree = new TwoThreeTree();
        
        // reads input file and inserts nodes into an instance of the twoThree tree
            
        Scanner input = new Scanner(System.in);

        // read first line, which is always the total # of planets in database
        int numOfPlanets = input.nextInt();    

        // iterates through the entire database
        for (int i = 0; i < numOfPlanets; i++) {

            //String line = input.nextLine();
            String planet = input.next();
            int fee = input.nextInt();

            insert(planet, fee, tree); // inserts nodes into tree

         }    
        
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);

        // number of comparisons given after the database list ends; first line
        int numOfComparisons = input.nextInt(); 

        // iterates through the number of comparisons needed (planet pairs)
        for (int j = 0; j < numOfComparisons; j++) {

            String planet1 = input.next();
            String planet2 = input.next();       

            // initializing the smaller and bigger planet variables
            String smaller;
            String bigger;

            // planet names may not be lexicographic; this will alphabetize
            if (planet1.compareTo(planet2) < 0) {
                smaller = planet1;
                bigger = planet2;
            } else {
                smaller = planet2;
                bigger = planet1;
            }

            // printing the range of planets that fall between the given two
            printRange(smaller, bigger, tree.root, tree.height, output);
        
        }
        
        output.flush(); // closing the BufferedWriter
          
    }
    
    // method that prints out the range of planets lexicographically, that fall within the given two planets
    static void printRange(String small, String big, Node p, int height, BufferedWriter output) throws Exception {
        
        // auxiliary recursive routine for traversing the delimited 2-3 tree
        if (height == 0) {
            
            // base case
            LeafNode leaf = (LeafNode) p; // downcast
            
            if (big.compareTo(leaf.guide) >= 0) {
                output.write(leaf.guide + " " + Integer.toString(leaf.value) + "\n");
            }
        } 

        else {
            
            // recursive case
            InternalNode q = (InternalNode) p; 
            
            if (small.compareTo(q.child0.guide) <= 0)
                printRange(small, big, q.child0, height - 1, output);
            
            if (small.compareTo(q.child1.guide) <= 0)
                printRange(small, big, q.child1, height - 1, output);
            
            if (q.child2 != null && small.compareTo(q.child2.guide) <= 0)
                printRange(small, big, q.child2, height - 1, output);
        }
    }


    static void insert(String key, int value, TwoThreeTree tree) {
       // insert a key value pair into tree (overwrite existing value
       // if key is already present)

          int h = tree.height;

          if (h == -1) {
              LeafNode newLeaf = new LeafNode();
              newLeaf.guide = key;
              newLeaf.value = value;
              tree.root = newLeaf; 
              tree.height = 0;
          }
          else {
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

            if (cmp == 0) {
                leaf.value = value; 
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
                  resetChildren(q, ws.scratch, 0, 2);
                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
               }
            }
        else if (ws.guideChanged) {
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
