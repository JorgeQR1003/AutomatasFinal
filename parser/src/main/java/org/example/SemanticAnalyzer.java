package org.example;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class SemanticAnalyzer {
    private SymbolTable symbolTable;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
    }

    public void analyze(AstNode root) {
        checkProgram(root);
    }

    public void checkProgram(AstNode root) {
        symbolTable.enterScope(); // Global scope
        addBuiltins(); // Restore built-ins
        
        for (AstNode child : root.getChildren()) {
            if ("Function".equals(child.getLabel())) {
                checkFunction(child);
            }
        }
        
        symbolTable.exitScope();
    }
    
    private void addBuiltins() {
        // Void methods
        symbolTable.addSymbol("Show", "void");
        symbolTable.addSymbol("Graph", "void");
        
        // Math methods (double)
        String[] mathMethods = {"Genf", "Deriv", "DerivX", "Integ", "IntegX", "Slope", "Root", "RootX", 
                              "Sen", "Cos", "Tan", "Sec", "Csc", "Cot", "Limit", "Dist"};
        for (String m : mathMethods) {
            symbolTable.addSymbol(m, "double");
        }
        
        // String/Other
        symbolTable.addSymbol("Input", "string");
        symbolTable.addSymbol("Concat", "string");
        symbolTable.addSymbol("Tab", "void"); 
    }

    public void checkFunction(AstNode function) {
        List<AstNode> children = function.getChildren();
        if (children.size() < 4) return;

        AstNode typeNode = children.get(0);
        AstNode idNode = children.get(1);
        AstNode paramsNode = children.get(2);
        AstNode bodyNode = children.get(3);

        String returnType = typeNode.getValue();
        String functionName = idNode.getValue();

        symbolTable.addSymbol(functionName, returnType);

        symbolTable.enterScope(); 

        addParametersFunction(paramsNode);
        addInstListFunction(bodyNode);

        symbolTable.exitScope();
    }

    public void addParametersFunction(AstNode parameters) {
        for (AstNode param : parameters.getChildren()) {
            if (param.getChildren().size() >= 2) {
                String type = param.getChildren().get(0).getValue();
                String name = param.getChildren().get(1).getValue();
                symbolTable.addSymbol(name, type);
            }
        }
    }

    public void addInstListFunction(AstNode instList) {
        for (AstNode inst : instList.getChildren()) {
            checkInstruction(inst);
        }
    }

    private void checkInstruction(AstNode inst) {
        String label = inst.getLabel();
        
        switch (label) {
            case "Declaration":
                checkDeclaration(inst);
                break;
            case "Assignment":
                checkAssignment(inst);
                break;
            case "If":
                checkIf(inst);
                break;
            case "For":
                checkFor(inst);
                break;
            case "MethodCall":
                checkMethodCall(inst);
                break;
            case "Return":
                // Keeping simple return check if needed or empty
                break;
            default:
                break;
        }
    }

    private void checkDeclaration(AstNode decl) {
        String type = decl.getChildren().get(0).getValue();
        String name = decl.getChildren().get(1).getValue();
        AstNode expr = decl.getChildren().get(2);

        String exprType = getExpressionType(expr);
        if (!isCompatible(type, exprType)) {
            System.out.println("Error: Type mismatch in declaration of '" + name + "'. Expected " + type + ", got " + exprType);
        }
        
        symbolTable.addSymbol(name, type);
    }

    private void checkAssignment(AstNode assign) {
        AstNode idNode = assign.getChildren().get(0);
        String name = idNode.getValue();
        AstNode expr = assign.getChildren().get(1);

        SimpleEntry<String, String> symbol = symbolTable.findSymbol(name);
        if (symbol == null) {
            System.out.println("Error: Variable '" + name + "' not declared.");
            return;
        }

        String varType = symbol.getValue();
        String exprType = getExpressionType(expr);
        
        if (!isCompatible(varType, exprType)) {
            System.out.println("Error: Type mismatch in assignment to '" + name + "'. Expected " + varType + ", got " + exprType);
        }
    }

    private void checkIf(AstNode ifNode) {
        AstNode condition = ifNode.getChildren().get(0);
        String condType = getExpressionType(condition);
        if (!"bool".equals(condType)) {
             System.out.println("Error: If condition must be boolean. Got " + condType);
        }

        symbolTable.enterScope();
        addInstListFunction(ifNode.getChildren().get(1));
        symbolTable.exitScope();

        if (ifNode.getChildren().size() > 2) {
            AstNode elseNode = ifNode.getChildren().get(2);
            symbolTable.enterScope();
            if ("If".equals(elseNode.getLabel())) {
                checkIf(elseNode);
            } else {
                addInstListFunction(elseNode);
            }
            symbolTable.exitScope();
        }
    }

    private void checkFor(AstNode forNode) {
        symbolTable.enterScope();
        
        AstNode init = forNode.getChildren().get(0);
        if ("Assignment".equals(init.getLabel()) || "Declaration".equals(init.getLabel())) {
             checkInstruction(init);
        }

        AstNode condition = forNode.getChildren().get(1);
        String condType = getExpressionType(condition);
        if (!"bool".equals(condType)) {
            System.out.println("Error: For condition must be boolean. Got " + condType);
        }

        AstNode update = forNode.getChildren().get(2);
        if ("Assignment".equals(update.getLabel())) {
            checkAssignment(update);
        } else if ("Update".equals(update.getLabel())) {
             String name = update.getChildren().get(0).getValue();
             if (symbolTable.findSymbol(name) == null) {
                 System.out.println("Error: Variable '" + name + "' not declared in for loop update.");
             }
        }

        AstNode body = forNode.getChildren().get(3);
        addInstListFunction(body);

        symbolTable.exitScope();
    }

    private void checkMethodCall(AstNode methodCall) {
        String name = methodCall.getValue();
        
        if (symbolTable.findSymbol(name) == null) {
             System.out.println("Error: Undefined function '" + name + "'");
        }
    }

    private String getExpressionType(AstNode node) {
        String label = node.getLabel();
        
        if ("Identifier".equals(label)) {
            String name = node.getValue();
            SimpleEntry<String, String> symbol = symbolTable.findSymbol(name);
            if (symbol != null) {
                return symbol.getValue();
            }
            System.out.println("Error: Undefined variable '" + name + "'");
            return "unknown";
        } else if ("Value".equals(label)) {
            String val = node.getValue();
            if (val.matches("-?\\d+")) return "int";
            if (val.matches("-?\\d*\\.\\d+")) return "double";
            if (val.startsWith("\"")) return "string";
            if (val.startsWith("'")) return "char";
            if ("true".equals(val) || "false".equals(val)) return "bool";
            return "unknown";
        } else if ("BinaryOp".equals(label)) {
            String op = node.getValue();
            String left = getExpressionType(node.getChildren().get(0));
            String right = getExpressionType(node.getChildren().get(1));
            
            if ("unknown".equals(left) || "unknown".equals(right)) return "unknown";
            
            if (isArithmetic(op)) {
                if ("double".equals(left) && "double".equals(right)) return "double";
                if ("int".equals(left) && "int".equals(right)) return "int";
                return "error"; 
            } else if (isComparison(op)) {
                if (isCompatible(left, right) || isCompatible(right, left)) return "bool";
                return "error";
            }
        } else if ("LogicOp".equals(label)) {
             return "bool";
        } else if ("MethodCall".equals(label)) {
             String name = node.getValue();
             SimpleEntry<String, String> symbol = symbolTable.findSymbol(name);
             if (symbol != null) return symbol.getValue();
             return "unknown";
        } else if ("ArrayAccess".equals(label)) {
             AstNode idNode = node.getChildren().get(0);
             String arrayType = getExpressionType(idNode);
             if (arrayType.endsWith("[]")) {
                 return arrayType.substring(0, arrayType.length() - 2);
             }
             return "unknown";
        }

        return "unknown";
    }

    private boolean isCompatible(String expected, String actual) {
        if ("unknown".equals(expected) || "unknown".equals(actual)) return true; 
        if (expected.equals(actual)) return true;
        return false;
    }

    private boolean isArithmetic(String op) {
        return "+".equals(op) || "-".equals(op) || "*".equals(op) || "/".equals(op) || "%".equals(op) || "**".equals(op);
    }

    private boolean isComparison(String op) {
        return "==".equals(op) || "!=".equals(op) || "<".equals(op) || ">".equals(op) || "<=".equals(op) || ">=".equals(op);
    }
}
