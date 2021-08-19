package com.sample;


/*
 * This java source file is set with Code Smell to be fixed through Automation.
 SONAR Rule S1118 and SONAR Rule S106 will be highlighted as issues in Sonar Server.
 
 To understand how to fix this through Automation, visit the below article written specifically
 to address and fix the code mainatainablility metric.
 
 https://medium.com/geekculture/quest-syntax-tree-and-remedy-code-smell-using-japa-visitor-patterns-f023a89842cf
 
 */
class SampleApp {

    //A private constructer will be added through automation for this utility class to hide the implicit public constructor
	//If a public constructor is written, the modifier will be replaced with private. This demonstrates sonar Rule S1118.
    public static String tester(String a) {
        String str = a;
        System.out.println("This line will be replaced with Logger - sonar Rule S106");
        return str;
    }

    //java.util.logging.* import declaration will be added through automation.
    public static int tester1() {
        int val = 80;
         System.out.println("This line will be replaced with Logger - sonar Rule S106");
        return val;
    }

   
}
