/* Vinnie Zhang
 * Basic Algorithms - Programming Assignment #6
 * Victor Schoup - Mon/Wed
 * Smelling the Cosmos - Karatsuba's Algorithm
 */

import java.io.*;
import java.util.regex.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class Solution {
    
    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);  // reading in the input       
       
        int n = input.nextInt() + 1; // degree of the two given polynomials
        int[] first = new int[n]; // array of coefficients of first polynomial
        int[] second = new int[n]; // array of coefficients of second polynomial
        
        for (int i = 0; i < n; i++) {
            first[i] = input.nextInt(); // storing input into array of polynomial 1's coefficients
        }
        
        for (int i = 0; i < n; i++) {
            second[i] = input.nextInt(); // storing input into array of polynomial 2's coefficients
        }
        
        int[] result = Solution.karatsuba(first, second, n); // array of final_output coefficients (after calling karatsuba method)
        
        for(int i = 0; i < result.length; i++) { // printing out the final_output results, formatted with spaces between each coefficient
            System.out.print(result[i] + " ");
        }
    }
    
    // naive (quadratic time) algorithm that will be used when n is small
    static int[] naive(int[] first, int[] second, int n) {
        
        int[] final_output = new int[2*n -1];
        
        for (int i = 0; i < n; i++) {
            
            for (int j = 0; j < n; j++) {
                final_output[i+j] = final_output[i+j] + first[i] * second[j];
            }
        }
        
        return final_output;
    }
    
    // main method that calculates the final coefficients
    static int[] karatsuba(int[] first, int[] second, int n){
        
        // instantiating arrays to store high and low coefficients for both the first and second polynomial
        int[] first_high = new int[n/2];
        int[] first_low = new int[n/2];
        int[] second_high = new int[n/2];        
        int[] second_low = new int[n/2];
        
        int[] final_output = new int[2*n -1]; // array of final_output coefficients
        
        if (n <= 8) { // if the degree is small, call naive algorithm method to calculate
            return naive(first, second, n);
        }
        
        // instantiating the first and second low arrays with the values in the original arrays
        for (int i = 0; i < n/2; i++) {
            first_low[i] = first[i];
            second_low[i] = second[i];
        }
        
        // instantiating the first and second high arrays starting halfway through the array, with the values in the original arrays
        for (int i = n/2; i < n; i++) {
            first_high[i-n/2] = first[i];
            second_high[i-n/2] = second[i];
        }
        
        // declaration and instantiation of two new arrays
        int[] t1 = new int[n/2];
        int[] t2 = new int[n/2];
        
        for (int i = 0; i < n/2; i++) { // filling in the two arrays
            t1[i] = first_low[i] + first_high[i]; // t1 stores the sum of the first lows and highs
            t2[i] = second_low[i] + second_high[i]; // t2 stores the sum of the second lows and highs
        }
            
        // calculate those values for final output
        int[] final_mid = karatsuba(t1, t2, n/2);
        int[] final_low = karatsuba(first_low, second_low, n/2);
        int[] final_high = karatsuba(first_high, second_high, n/2);
        
        // filling all of final-output array with final low values (only temporary)
        for (int i = 0; i < n-1; i++) {
            final_output[i] = final_low[i];
        }
        
        final_output[n-1] = 0; // last index in the final array will have no coefficient
        
        // what does n+i position represents in the final_output array
        for (int i = 0; i < n-1; i++) {
            final_output[n+i] = final_high[i];
        }
        
        // calculating final outputs using the other final arrays --> only adjusting the values starting from midpoint of array
        for (int i = 0; i < n-1; i++) {
            final_output[n/2 + i] += final_mid[i] - (final_low[i] + final_high[i]);
        }
        
        return final_output; // final return statement
    }
}