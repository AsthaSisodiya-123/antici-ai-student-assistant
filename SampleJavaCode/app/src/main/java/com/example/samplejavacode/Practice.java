package com.example.samplejavacode;
import java.util.HashMap;
import java.util.Scanner;

public class Practice {



        public static void main(String[] args) {
            // 1. Create a HashMap (DSA Concept) to store Student Names and their Marks
            // Key: Student Name (String), Value: Marks (Integer)
            HashMap<String, Integer> studentRecords = new HashMap<>();

            // 2. Insert data into the HashMap (O(1) time complexity)
            studentRecords.put("Rahul", 85);
            studentRecords.put("Sneha", 92);
            studentRecords.put("Amit", 78);

            // 3. Set up a Scanner to take input from the keyboard
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter student name to find marks (Rahul, Sneha, Amit): ");

            // This line waits for you to type a name in the terminal and press Enter
            String inputName = scanner.next();

            // 4. Use the DSA structure to instantly look up the value
            if (studentRecords.containsKey(inputName)) {
                int marks = studentRecords.get(inputName);
                System.out.println("OUTPUT: " + inputName + " scored " + marks + " marks.");
            } else {
                System.out.println("OUTPUT: Student record not found!");
            }

            scanner.close();
        }
    }
