/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package max.vanach.lesson_1;

import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

/**
 *
 * @author max
 */ 
public class Lesson_1 {
    
    // procedure and function is a Methods
    // this is a local methods
    
    // procedure
    static void setSay()
    {
        System.out.println("saing: I'm procedure !!!");
    }

    // function
    static String getSay()
    {
        return "saing: I'm function !!!";
    }
   
    // software engine
    public static void main(String[] args) throws FileNotFoundException {
        //System.out.println("Hello World!");

        GuessingGame game = new GuessingGame();

        game.run();


    }
}
