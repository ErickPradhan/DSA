/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import Model.ExpenseModel;
import java.util.ArrayList;

public class ExpenseSortController {
    //Selection Sort:
    public static void SelectionSortByName(ArrayList<ExpenseModel> list) 
    {
        int size = list.size();

        for (int step = 0; step < size - 1; step++) 
        {
            // Find the index of the minimum element in the remaining unsorted array
            int min_idx = step;

            for (int i = step + 1; i < size; i++) 
            {
                // Compare names alphabetically using compareTo()
                // compareTo() returns a negative number if the first string comes before the second
                if (list.get(i).getName().compareToIgnoreCase(list.get(min_idx).getName()) < 0) {
                    min_idx = i; // Update the index to the new minimum
                }
            }
            // Swap the found minimum element with the element at the current step position
            ExpenseModel temp = list.get(step);
            list.set(step, list.get(min_idx));
            list.set(min_idx, temp);
        }
    }
    //Insertion Sort:
    public static void InsertionSortByAmount(ArrayList<ExpenseModel> list){
    int size = list.size();

        // Start from the second element (index 1) as the first element is trivially sorted
        for (int i = 1; i < size; i++) {
            // Pick the element to be inserted
            ExpenseModel key = list.get(i);
            int j = i - 1;

            // Move elements of list[0..i-1], that are greater than key.getAge(), 
            // to one position ahead of their current position
            while (j >= 0 && list.get(j).getAmount() > key.getAmount()) {
                // Shift the current element to the right
                list.set(j + 1, list.get(j));
                j = j - 1;
            }

            // Place the key (the current element from the outer loop) in its correct position
            list.set(j + 1, key);
        }
    }
    //Bubble Sort:
    public static void BubbleSortById(ArrayList<ExpenseModel> list)
    {
        int n = list.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {

                if (list.get(j).getId() > list.get(j + 1).getId()) {
                    ExpenseModel temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }
}
