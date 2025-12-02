package org.example;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Stack;

/// SymbolTable handles the symbol table for the semantic analysis
/// It is a stack of scopes, each scope is a HashMap of identifiers and their types
/// If an identifier is found in the stack, it means it was previusly declared and can be used
/// Else, death.
/// Is not neccesary to include scope as a parameter because the stack is already keeping track of the current scope.
public class SymbolTable {

    // Stack of scopes, each scope is a HashMap of identifiers and their types
    private Stack<HashMap<String, String>> scopeStack;
    
    public SymbolTable() {
        this.scopeStack = new Stack<HashMap<String, String>>();
    }

    // enterScope pushes a new scope onto the stack, making it the most recent scope in the stack.
    public void enterScope() {
        this.scopeStack.push(new HashMap<String, String>());
    }

    // findSymbol returns the symbol and its type if it exists, otherwise returns null
    public SimpleEntry<String, String> findSymbol(String symbol) {
        for (HashMap<String, String> scope : scopeStack) {
            if (scope.containsKey(symbol)) {
                return new SimpleEntry<String, String>(symbol, scope.get(symbol));
            }
        }
        return null;
    }

    // addSymbol adds a symbol to the current scope
    public void addSymbol(String symbol, String type) {
        this.scopeStack.peek().put(symbol, type);
    }

    // checkScope checks if a symbol exists in the current scope, if not returns false
    public boolean checkScope(String symbol) {
        return this.scopeStack.peek().containsKey(symbol);
    }

    // exitScope pops the current scope off the stack
    public void exitScope() {
        this.scopeStack.pop();
    }
}