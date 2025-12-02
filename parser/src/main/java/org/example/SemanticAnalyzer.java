package org.example;

public class SemanticAnalyzer {
    private SymbolTable symbolTable;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
    }

    public void analyze(AstNode root) {
        // Check for type mismatches
        // Check for undefined variables
        // Check for redeclaration of variables
        // Check for scope mismatches
        // Check for use of undeclared variables
        // Check for use of undefined variables
        // Check for use of undeclared variables

    }

    // What I want this to do is check if whole program is valid, else return mdweowemdoiwmdowem
    public void checkProgram(AstNode root) {
        // Checks functions because is Program's direct child.
        for (AstNode child : root.getChildren()) {
            checkFunction(child);
        }
    }

    public void checkFunction(AstNode function) {
        for(AstNode child : function.getChildren()) {
            if (child.getLabel().equals("Type")) {
                addTypeFunction(child);
            } else if (child.getLabel().equals("Identifier")) {
                addIdentifierSymbolTable(child, addTypeFunction(child.getChildren().get(0)));
            } else if (child.getLabel().equals("Parameters")) {
                addParametersFunction(child);
            } else if (child.getLabel().equals("Body")) {
                addInstListFunction(child);
            }
        }
    }

    public String addTypeFunction(AstNode type) {
        return type.getValue();
    }

    public boolean addIdentifierSymbolTable(AstNode identifier, String type) {
        if (this.symbolTable.checkScope(identifier.getValue())) {
            System.out.println("Error: Identifier " + identifier.getValue() + " already declared");
            return false;
        }
        this.symbolTable.addSymbol(type, identifier.getValue());
        return true;
    }

    public void addParametersFunction(AstNode parameters) {}

    // Checks if instList is valid, else return mdweowemdoiwmdowem
    public void addInstListFunction(AstNode instList) {
        

}
