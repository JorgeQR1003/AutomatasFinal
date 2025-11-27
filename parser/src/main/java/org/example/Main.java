package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Test 1: Función simple con asignaciones y operaciones
        // int Main() { int ID = NUM ; ID = ID + NUM * NUM ; return NUM ; }
        String[] test1 = {
            "int", "ID", "(", ")", "{",
            "int", "ID", "=", "NUM", ";",
            "ID", "=", "ID", "+", "NUM", "*", "NUM", ";",
            "return", "NUM", ";",
            "}"
        };

        // Test 2: Función con condicionales if-else
        // void Main() { if ( ID > NUM && ID == NUM ) { Show ( LITERAL ) ; } else { Show ( LITERAL ) ; } }
        String[] test2 = {
            "void", "ID", "(", ")", "{",
            "if", "(", "ID", ">", "NUM", "&&", "ID", "==", "NUM", ")", "{",
            "Show", "(", "LITERAL", ")", ";",
            "}", "else", "{",
            "Show", "(", "LITERAL", ")", ";",
            "}",
            "}"
        };

        // Test 3: Función con for loop y métodos
        // void Main() { for ( ID = NUM ; ID < NUM ; ID++ ) { Show ( ID ) ; } return NUM ; }
        String[] test3 = {
            "void", "ID", "(", ")", "{",
            "for", "(", "ID", "=", "NUM", ";", "ID", "<", "NUM", ";", "ID", "++", ")", "{",
            "Show", "(", "ID", ")", ";",
            "}",
            "return", "NUM", ";",
            "}"
        };

        // Test 4: Función con arrays (según gramática: Val' -> [ num ] para acceso)
        // int Main() { int [ ] ID = NUM ; ID = ID [ NUM ] + NUM ; return NUM ; }
        String[] test4 = {
            "int", "ID", "(", ")", "{",
            "int", "[", "]", "ID", "=", "NUM", ";",
            "ID", "=", "ID", "[", "NUM", "]", "+", "NUM", ";",
            "return", "NUM", ";",
            "}"
        };

        System.out.println("========== TEST 1: Asignaciones y Operaciones ==========");
        Parser parser1 = new Parser(test1);
        parser1.Parse();
        System.out.println();

        System.out.println("========== TEST 2: Condicionales if-else ==========");
        Parser parser2 = new Parser(test2);
        parser2.Parse();
        System.out.println();

        System.out.println("========== TEST 3: For loop y Métodos ==========");
        Parser parser3 = new Parser(test3);
        parser3.Parse();
        System.out.println();

        System.out.println("========== TEST 4: Arrays ==========");
        Parser parser4 = new Parser(test4);
        parser4.Parse();
    }
}



            