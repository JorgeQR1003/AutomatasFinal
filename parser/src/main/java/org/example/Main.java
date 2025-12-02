package org.example;

import java.util.List;
import java.util.Map.Entry;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        String tokensFilePath = "../tokens.json"; 
        
        System.out.println("========== TEST FROM tokens.json ==========");
        List<Entry<String, String>> jsonTokens = Scanner.readTokens(tokensFilePath);
        
        if (jsonTokens.isEmpty()) {
             // Fallback try without .. if running from root
             jsonTokens = Scanner.readTokens("tokens.json");
        }

        if (!jsonTokens.isEmpty()) {
            Parser parser = new Parser(jsonTokens);
            AstNode ast = parser.Parse();
            if (ast != null) {
                // System.out.println(ast);
                System.out.println("Running Semantic Analysis on tokens.json...");
                SemanticAnalyzer analyzer = new SemanticAnalyzer();
                analyzer.analyze(ast);
                System.out.println("Done.");
            }
        } else {
            System.out.println("Could not load tokens from tokens.json");
        }
    }
}
