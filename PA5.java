/* Vinnie Zhang
 * Basic Algorithms - Programming Assignment #5
 * Victor Schoup - Mon/Wed
 * Mutating the NBA - Dynamic Programming
 */

import java.io.*;
import java.util.*;

public class Solution {

    public static void main(String[] args) {
        
        // reading from system
        Scanner input = new Scanner(System.in);
        
        // variable/data structure declarations
        HashMap<String, Integer> d = new HashMap<>(); // hm storing specific genes in relation to score
        char dna[] = {'_', 'G', 'C', 'A', 'T'}; // possible genes
        int gap_score = -1; // constant variable
        
        String player_a = input.nextLine(); // dna of first player
        String player_b = input.nextLine(); // dna of second player
        String new_a = ""; // new dna of first player
        String new_b = ""; // new dna of second player
        
        // subproblem graph of player a and b dna sequence lengths
        int[][] matrix = new int[player_a.length() + 1][player_b.length() + 1]; 
        
        // filling in first row of hashmap with genes, and -1
        for (int i = 0; i < 5; i++) {
            d.put("" + dna[0] + dna[i], gap_score); 
            d.put("" + dna[i] + dna[0], gap_score);
        }
        
        
        for (int i = 1; i < 5; i++) {  // columns
            
            for (int k = 1; k < 5; k++) { // rows
               
                if (dna[i] == dna[k]) { // if the genes in both players match, store +2
                    d.put("" + dna[i] + dna[k], 2);
                } else { // if they don't match store, -2
                    d.put("" + dna[i] + dna[k], -2);
                }
                
            }  
        }
        
        for (int i = 0; i < player_a.length() + 1; i++) { // adjusting score for player a
            matrix[i][0] = gap_score*i;
        }
        
        for (int i = 0; i < player_b.length() + 1; i++) { // adjusting score for player b
            matrix[0][i] = gap_score*i;
        }
     
        for (int i = 1; i < player_a.length() + 1; i++) {
           
            for (int k = 1; k < player_b.length() + 1; k++) {
                
                int temp = Math.max(matrix[i-1][k-1] + d.get("" + player_a.charAt(i-1) + player_b.charAt(k-1)), matrix[i][k-1] + d.get("" + dna[0] + player_b.charAt(k-1)));
                matrix[i][k] = Math.max(temp, matrix[i-1][k] + d.get("" + player_a.charAt(i-1) + dna[0]));
            }
        }
        
        int x = player_a.length(); // player a dna sequence length
        int y = player_b.length(); // player b dna sequence length
        
        while (x > 0 && y > 0) { // while not empty --> either of them      
            
            if (matrix[x][y] - d.get("" + player_a.charAt(x-1) + player_b.charAt(y-1)) == matrix[x-1][y-1]) { // same gene in both locations of matrix
                new_a = player_a.charAt(x-1) + new_a; // add to a
                new_b = player_b.charAt(y-1) + new_b; // add to b
                x--;
                y--;
            } 
            
            else if (matrix[x][y] - gap_score == matrix[x][y-1]) { // same gene in both locations --> y difference
                new_a = "-" + new_a;
                new_b = player_b.charAt(y-1) + new_b;
                y--;
            }
            
            else if (matrix[x][y] - gap_score == matrix[x-1][y]) { //same gene in both locations --> x difference
                new_a = player_a.charAt(x-1) + new_a;
                new_b = "-" + new_b;
                x--;
            }
        }
        
        if (y > 0) { // gene column not emptied
            
            while (y > 0) {
                new_b = player_b.charAt(y-1) + new_b;
                new_a = "-" + new_a;
                y--;
            }
        }
        
        else if (x > 0) { // gene row not emptied
           
            while (x > 0) {
                new_a = player_a.charAt(x-1) + new_a;
                new_b = "-" + new_b;
                x--;
            }
        }
        
        // print statements
        System.out.println(matrix[player_a.length()][player_b.length()]); // optimal score of final alignment
        System.out.println(new_a); // final player 1 alignment
        System.out.println(new_b); // final player 2 alignment
        
        input.close();
    }
    
}
