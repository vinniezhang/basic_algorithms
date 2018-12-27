/* Vinnie Zhang
 * Basic Algorithms - Programming Assignment #4
 * Victor Schoup - Mon/Wed
 * Crossing Khazad-Dum - Dijkstra's Algorithm
 */

import java.io.*;
import java.util.*;

public class Solution {
		 
		static ArrayList<Integer> top_sort = new ArrayList<>();
	    static int pred_of_first_node;
	    static int[] predecessor;
		    
	    public static void main(String[] args) {
	       
	    	// reading input
	    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	        String input_string = null;
	        
	        // try catch block in case it doesn't work
	        try {
	            input_string = input.readLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        String[] data = input_string.split(" "); // split input by spaces to distinguish numbers
	        
	        int num_of_rooms = Integer.parseInt(data[0]) + 1; // first number will always be num of rooms
	        int num_of_passages = Integer.parseInt(data[1]); // second number will always be num of passages
	        
	        Node[] array_of_rooms = new Node[num_of_rooms]; // array of room objects as nodes
	        int[] array_of_passages = new int[num_of_rooms]; // array of edges as ints
	        predecessor = new int[num_of_rooms]; // array keeping track of predecessor rooms
	        
	        // filling in passage array with 0s --> empty/unvisited 
	        for (int i = 0; i < array_of_passages.length; i++) {
	            array_of_passages[i] = 0;
	        }
	        
	        // iterating through 
	        for (int i = 0; i < num_of_passages; i++) {
	            
	        	// verifying the input can be read
	        	try {
	                input_string = input.readLine();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        	
	            data = input_string.split(" "); // starting to read rooms/passageway inputs
	            int predecessor = Integer.parseInt(data[0]); // first room
	            int successor = Integer.parseInt(data[1]); // room led to from first room
	                        
	            Node successor_node = new Node(successor); // making successor int a node
	            successor_node.successor = array_of_rooms[predecessor]; // current successor becomes predecessor of next cycle
	            array_of_rooms[predecessor] = successor_node; // sets first index of array as successor node
	        }
	        
	        int loop = DFS(array_of_rooms, array_of_passages);
	        
	        if (loop == 1) {
	           
	        	System.out.println(1);
	            
	            String print = " " + pred_of_first_node;
	            int current = predecessor[pred_of_first_node];
	           
	            // printing out the rooms in the path since loop exists
	            while (current != pred_of_first_node) {
	                print = " " + current + print;
	                current = predecessor[current];
	            }
	            
	            System.out.println(print.substring(1));
	        }
	        
	        else {
	           
	        	System.out.println(0); // no such loop exists
	        }
	        
	    }
	    
	    static int DFS(Node[] arrayOfNodes, int[] passage) {
	       
	    	for (int i = 1; i < arrayOfNodes.length; i++) {
	           
	        	if (passage[i] == 0) { // room has not yet been visited
	                return RecDFS(i, arrayOfNodes, passage); // run recursive DFS
	            }
	        	
	        }
	        
	        return 0;
	    }
	    
	    static int RecDFS (int room_index, Node[] array_of_rooms, int passage[]) {
	       
	    	passage[room_index] = 1;
	        Node current = array_of_rooms[room_index];
	       
	        while (current != null) {
	            
	        	if (passage[current.value] == 0) { // unvisited
	                
	        		predecessor[current.value] = room_index;
	                top_sort.add(room_index);
	                int current_value = current.value;
	                current = current.successor;
	                
	                return RecDFS(current_value, array_of_rooms, passage);   
	            } 
	        	
	            else { // passage[current.value] == 1
	                
	            	top_sort.add(room_index);
	                predecessor[current.value] = room_index;
	                pred_of_first_node = room_index;
	                
	                return 1;
	            }
	        }
	        
	        return 0;
	    }

	}

	class Node { // node class
	    
		int value;
	    Node successor;
	    
	    Node(int value) {
	        this.value = value;
	    }

	}
