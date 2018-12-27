/* Vinnie Zhang
 * Basic Algorithms - Programming Assignment #3
 * Victor Schoup - Mon/Wed
 * Making the SPARTAN - hash-heap
 */

import java.io.*;
import java.util.*;


class Candidate { // candidate class
	
	String name;
	int heap_pos;
	long score;

	Candidate(){} // default constructor
	
	Candidate(String name, long score, int heap_pos){ // candidate constructor with set candidate name, score, and index within the heap
		this.name = name;
		this.score = score;
		this.heap_pos = heap_pos; // the position within the array implementation of the heap
	}
	
}

class buildHeap{ // heap class
	
	private ArrayList<Candidate> array_heap;
	
	// method that creates a heap (from data of an ArrayList) and heapifies it, to ensure correct heap properties
	buildHeap(ArrayList<Candidate> c_heap){
		array_heap = c_heap;
		minHeapify(array_heap);
	}
	
	public int getSize() { // getter for heap size
		return array_heap.size();
	}
	
	// method to move candidate down the array implementation of the heap, to maintain heap property of every parent node being less than its children
	public static void bubbleDown(ArrayList<Candidate> heap_array, int array_index) { // to ensure that there is no indexOutOfBounds
		
		// instantiate variables
		int left_child_index, right_child_index, min_index;
		Candidate tmp;
		
		// setting left and right child indices
		left_child_index = getLeft(array_index);
		right_child_index = getRight(array_index);
		
		if (right_child_index >= heap_array.size()) { // right child will not appear in the arraylist
			
			if (left_child_index >= heap_array.size()) { // left child will not appear in the arraylist
				return; 
			} else {
				min_index = left_child_index; // left child will appear in the arraylist
			}
			
		} else { // right child will appear in the arraylist
			
			if (heap_array.get(left_child_index).score <= heap_array.get(right_child_index).score) { // left is less than right score
				min_index = left_child_index; // thus, left is min score --> setting the min
			} else {
				min_index = right_child_index; // right is the min score --> setting the min
			}
		}
		
		if (heap_array.get(array_index).score > heap_array.get(min_index).score) { // if the current index we're at is greater than the score at the minimum index
			
			int temp_index;
			
			// maintaining the heap_pos indices of the two scores we're swapping
			temp_index = heap_array.get(min_index).heap_pos;
			heap_array.get(min_index).heap_pos = heap_array.get(array_index).heap_pos;
			heap_array.get(array_index).heap_pos = temp_index;
			
			// swapping the two candidates using a temporary variable
			tmp = heap_array.get(min_index);
			heap_array.set(min_index, heap_array.get(array_index));
			heap_array.set(array_index, tmp);
			
			bubbleDown(heap_array, min_index); // bubbling down now to maintain the heap property
		}
		
	}
		
	// getter methods for left and right children
	public static int getLeft(int index) {
		return (index*2) + 1;	 
	}
	
	public static int getRight(int index) {
		return (index*2) + 2;	
	}
	
	// minHeapify method --> reordering the array elements to satisfy the minHeap property
	public static void minHeapify(ArrayList<Candidate> heap_array) { 
		
		for (int n = heap_array.size()/2; n >= 0; n--) {
			bubbleDown(heap_array, n);
		}
	}
			
	// method to delete the minimum value of the heap --> this is where you call bubbleDown
	public static void deleteMin(ArrayList<Candidate> heap_array){
		
		int heapSize = heap_array.size();
		heap_array.set(0, heap_array.get(heapSize - 1)); // setting the root of the heap to the value of the last value in the arrayList to get rid of the root value
		
		heap_array.get(0).heap_pos = 0; // maintaining the index of the root after a deletion
		
		heap_array.remove(heapSize-1); // removing the last value of the heap that we just put into the first position of the arrayList
		heapSize--; // decrementing heap size since we just deleted
		
		if (heapSize > 0) {
			bubbleDown(heap_array, 0); // maintaining the heap property
		}
	}
	
}

public class Solution extends Candidate{

	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		int num_of_candidates = input.nextInt(); // first line always give number of candidates
		int query_type;
		String name, name_to_find;
		long score, num_to_add, standard;	
		
		// creating hashmap to store candidate's name and score
		HashMap<String, Candidate> spartan_hm = new HashMap<String, Candidate>();
		
		// create an array implementation of heap, set array length to num of candidates there are
		ArrayList<Candidate> heap_array = new ArrayList<Candidate>();
		
		// looping through the candidates, and adding info to both hashmap and heap
		for (int i = 0; i < num_of_candidates; i++) {
			
			// obtaining the name and score values from the input
			name = input.next();
			score = input.nextLong();
			
			Candidate spartan = new Candidate(name, score, i); // creating new candidate object
			
			spartan_hm.put(name, spartan); // storing candidate object info in hashmap
			
			heap_array.add(spartan); // adding each object to the heap array --> all of them will be named the same		
			
		} 
		
		buildHeap bh = new buildHeap(heap_array); // creating a heap with nodes in the correct order
		
		int num_of_queries = input.nextInt();
		
		// looping through the queries and distinguishing what to do with each query type
		for (int j = 0; j < num_of_queries; j++) {
			
			query_type = input.nextInt(); 
			
			if (query_type == 1) { // must add the candidate object to heap_array, but in correct position
				
				name_to_find = input.next();
				num_to_add = input.nextLong();
				
				// updating the current score of spartan in hashmap with improvement score
				Candidate c = spartan_hm.get(name_to_find); // point by reference variable
				c.score += num_to_add;
				
				buildHeap.bubbleDown(heap_array, c.heap_pos); // bubbling down the heap at this current heap index, to make sure the indices are accurate after adding
			}
			
			else if (query_type == 2) { // must return the final number of qualified spartans
				
				standard = input.nextLong(); // standard to base comparison off of
				
				// must make the comparisons between standard and spartan scores and deletes the candidate from heap if the candidate score doesn't qualify
				while (heap_array.get(0).score < standard) { 
					buildHeap.deleteMin(heap_array);
				}		
		
				System.out.println(heap_array.size()); // prints the final heap array size
				
			}
		}
	}
}
