package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    /// los tokens deben tener el siguiente formato: (token, lexema), deberian tambien tener posicion pero no lo agregamos xd
    /// int main() {
    //     int a = 10;
    //     a = b + 5 * 2;
    //     return 1;
    // }


    public static void main(String[] args) {
        // Test 1: Función simple con asignaciones y operaciones
        // int main() { int a = 10; a = b + 5 * 2; return 1; }
        List<Entry<String, String>> test1 = createTokenList(
            "int", "int",
            "ID", "main",
            "(", "(",
            ")", ")",
            "{", "{",
            "int", "int",
            "ID", "a",
            "=", "=",
            "NUM", "10",
            ";", ";",
            "ID", "a",
            "=", "=",
            "ID", "b",
            "+", "+",
            "NUM", "5",
            "*", "*",
            "NUM", "2",
            ";", ";",
            "return", "return",
            "NUM", "1",
            ";", ";",
            "}", "}"
        );

        // Test 2: Función con condicionales if-else
        // void test() { if ( x > 5 && y == 10 ) { Show ( "Hello" ) ; } else { Show ( "World" ) ; } }
        List<Entry<String, String>> test2 = createTokenList(
            "void", "void",
            "ID", "test",
            "(", "(",
            ")", ")",
            "{", "{",
            "if", "if",
            "(", "(",
            "ID", "x",
            ">", ">",
            "NUM", "5",
            "&&", "&&",
            "ID", "y",
            "==", "==",
            "NUM", "10",
            ")", ")",
            "{", "{",
            "Show", "Show",
            "(", "(",
            "LITERAL", "Hello",
            ")", ")",
            ";", ";",
            "}", "}",
            "else", "else",
            "{", "{",
            "Show", "Show",
            "(", "(",
            "LITERAL", "World",
            ")", ")",
            ";", ";",
            "}", "}",
            "}", "}"
        );

        // Test 3: Función con for loop y métodos
        // void loop() { for ( i = 0 ; i < 10 ; i++ ) { Show ( i ) ; } return 0 ; }
        List<Entry<String, String>> test3 = createTokenList(
            "void", "void",
            "ID", "loop",
            "(", "(",
            ")", ")",
            "{", "{",
            "for", "for",
            "(", "(",
            "ID", "i",
            "=", "=",
            "NUM", "0",
            ";", ";",
            "ID", "i",
            "<", "<",
            "NUM", "10",
            ";", ";",
            "ID", "i",
            "++", "++",
            ")", ")",
            "{", "{",
            "Show", "Show",
            "(", "(",
            "ID", "i",
            ")", ")",
            ";", ";",
            "}", "}",
            "return", "return",
            "NUM", "0",
            ";", ";",
            "}", "}"
        );

        // Test 4: Función con arrays
        // int arrayTest() { int [ ] arr = 5 ; arr = arr [ 2 ] + 3 ; return 0 ; }
        List<Entry<String, String>> test4 = createTokenList(
            "int", "int",
            "ID", "arrayTest",
            "(", "(",
            ")", ")",
            "{", "{",
            "int", "int",
            "[", "[",
            "]", "]",
            "ID", "arr",
            "=", "=",
            "NUM", "5",
            ";", ";",
            "ID", "arr",
            "=", "=",
            "ID", "arr",
            "[", "[",
            "NUM", "2",
            "]", "]",
            "+", "+",
            "NUM", "3",
            ";", ";",
            "return", "return",
            "NUM", "0",
            ";", ";",
            "}", "}"
        );

        System.out.println("========== TEST 1: Asignaciones y Operaciones ==========");
        Parser parser1 = new Parser(test1);
        AstNode ast1 = parser1.Parse();
        if (ast1 != null) {
            System.out.println("AST Structure:");
            System.out.println(ast1);
        }
        System.out.println();

        System.out.println("========== TEST 2: Condicionales if-else ==========");
        Parser parser2 = new Parser(test2);
        AstNode ast2 = parser2.Parse();
        if (ast2 != null) {
            System.out.println("AST Structure:");
            System.out.println(ast2);
        }
        System.out.println();

        System.out.println("========== TEST 3: For loop y Métodos ==========");
        Parser parser3 = new Parser(test3);
        AstNode ast3 = parser3.Parse();
        if (ast3 != null) {
            System.out.println("AST Structure:");
            System.out.println(ast3);
        }
        System.out.println();

        System.out.println("========== TEST 4: Arrays ==========");
        Parser parser4 = new Parser(test4);
        AstNode ast4 = parser4.Parse();
        if (ast4 != null) {
            System.out.println("AST Structure:");
            System.out.println(ast4);
        }
    }

    private static List<Entry<String, String>> createTokenList(String... pairs) {
        List<Entry<String, String>> list = new ArrayList<>();
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide pairs of (tokenType, value)");
        }
        for (int i = 0; i < pairs.length; i += 2) {
            String tokenType = pairs[i];
            String value = pairs[i + 1];
            list.add(new SimpleEntry<>(tokenType, value));
        }
        return list;
    }
}
