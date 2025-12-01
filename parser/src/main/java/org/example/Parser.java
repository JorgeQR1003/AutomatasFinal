package org.example;


import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

public class Parser {
    private final List<Entry<String, String>> tokens;
    private String currentToken;
    private int currentIndex;
    
    private String[] dataTypes = {"string", "void", "int", "bool", "char", "double"};
    private String[] firstAssign = {"string", "void", "int", "bool", "char", "double", "ID"};
    private String[] methodNames = {"Show", "Input", "Genf", "Deriv", "DerivX", "Integ", "IntegX", "Graph", "Slope", "Tab", "Root", "RootX", "Sen", "Cos", "Tan", "Sec", "Csc", "Cot", "Limit", "Concat", "Dist"};
    private String[] logOp = {"&&", "||", "!=", "==", "<=", ">=", "<", ">"};
    private String[] op = {"+", "-", "*", "/", "%", "**"};
    private String[] vals = {"NUM", "LITERAL", "INF", "PI"};
    private String[] opIncr = {"++", "--"};

    public Parser(List<Entry<String, String>> tokens) {
        this.tokens = tokens;
        this.tokens.add(new SimpleEntry<String, String>("eof", "eof"));
        this.currentIndex = 0;
        this.currentToken = this.tokens.get(currentIndex).getKey();
    }

    public AstNode Parse() {
        System.out.println("Starting parse...");
        AstNode root = Program();
        if (root != null && currentToken.equals("eof")) {
            System.out.println("Program parsed successfully");
            return root;
        } else {
            System.out.println("Error: Program failed to parse. Unexpected token: " + currentToken);
            return null;
        }
    }

    /// Program -> Function Program | ε
    public AstNode Program() {
        AstNode programNode = new AstNode("Program");
        
        while (Contains(currentToken, dataTypes)) {
            AstNode funcNode = Function();
            if (funcNode == null) {
                return null;
            }
            programNode.addChild(funcNode);
        }
        
        return programNode;
    }

    /// Function -> Type ident ( FParam ) { InstList }
    public AstNode Function() {
        AstNode funcNode = new AstNode("Function");
        
        // Type
        AstNode typeNode = Type();
        if (typeNode == null) return null;
        funcNode.addChild(typeNode);
        
        // ID
        if (!Match("ID")) {
            System.out.println("Error in Function: Expected ID");
            return null;
        }
        String funcName = tokens.get(currentIndex).getValue(); // Previous token was the ID
        funcNode.addChild(new AstNode("Identifier", funcName));
        
        consumeToken(); 
        
        if (!MatchAndConsume("(")) return null;
        
        AstNode paramsNode = new AstNode("Parameters");
        if (!FParam(paramsNode)) return null;
        funcNode.addChild(paramsNode);
        
        if (!MatchAndConsume(")")) return null;
        if (!MatchAndConsume("{")) return null;
        
        AstNode bodyNode = InstList();
        if (bodyNode == null) return null;
        funcNode.addChild(bodyNode);
        
        if (!MatchAndConsume("}")) return null;
        
        return funcNode;
    }

    /// Type -> TypeVal Type'
    public AstNode Type() {
        String baseType = currentToken;
        if (!TypeVal()) return null;
        
        String suffix = TypePr(); // Returns "[]" or ""
        if (suffix == null) return null;
        
        return new AstNode("Type", baseType + suffix);
    }

    /// Type' -> ε | [ ]
    public String TypePr() {
        if (CheckAndConsume("[")) {
            if (MatchAndConsume("]")) {
                return "[]";
            }
            return null;
        }
        return "";
    }

    /// TypeVal -> void | string | int | double | char | bool
    public boolean TypeVal() {
        if (Contains(currentToken, dataTypes)) {
            consumeToken();
            return true;
        }
        return false;
    }

    /// InstList -> Inst InstList | ε
    public AstNode InstList() {
        AstNode listNode = new AstNode("InstList");
        
        while (Contains(currentToken, firstAssign) || Contains(currentToken, methodNames) || 
               isMatch("if") || isMatch("for") || isMatch("switch") || isMatch("return")) {
            AstNode inst = Inst();
            if (inst == null) return null;
            listNode.addChild(inst);
        }
        return listNode;
    }

    /// Inst -> Assign ; | Cond | For | Switch | Method ; | return Val ;
    public AstNode Inst() {
        if (isMatch("return")) {
            consumeToken();
            AstNode returnNode = new AstNode("Return");
            AstNode val = Val();
            if (val == null) return null;
            returnNode.addChild(val);
            if (!MatchAndConsume(";")) return null;
            return returnNode;
        }
        
        if (Contains(currentToken, firstAssign)) {
            AstNode assignNode = Assign();
            if (assignNode == null) return null;
            if (!MatchAndConsume(";")) return null;
            return assignNode;
        }
        
        if (isMatch("if")) return Cond();
        if (isMatch("for")) return For();
        if (isMatch("switch")) return Switch();
        
        if (Contains(currentToken, methodNames)) {
            AstNode methodNode = Method();
            if (methodNode == null) return null;
            if (!MatchAndConsume(";")) return null;
            return methodNode;
        }
        
        System.out.println("Unexpected token in Inst: " + currentToken);
        return null;
    }

    /// Assign -> Type ident = Expr | ident = Expr
    public AstNode Assign() {
        // Check if it's a declaration (starts with Type) or assignment (starts with ID)
        if (Contains(currentToken, dataTypes)) {
            // Type ident = Expr
            AstNode typeNode = Type(); // Consumes type
            if (typeNode == null) return null;
            
            String id = tokens.get(currentIndex).getValue();
            if (!MatchAndConsume("ID")) return null;
            
            if (!MatchAndConsume("=")) return null;
            
            AstNode expr = Expr();
            if (expr == null) return null;
            
            AstNode assignNode = new AstNode("Declaration");
            assignNode.addChild(typeNode);
            assignNode.addChild(new AstNode("Identifier", id));
            assignNode.addChild(expr);
            return assignNode;
        } else {
            // ident = Expr
            String id = tokens.get(currentIndex).getValue();
            if (!MatchAndConsume("ID")) return null;
            
            if (!MatchAndConsume("=")) return null;
            
            AstNode expr = Expr();
            if (expr == null) return null;
            
            AstNode assignNode = new AstNode("Assignment");
            assignNode.addChild(new AstNode("Identifier", id));
            assignNode.addChild(expr);
            return assignNode;
        }
    }

    /// Cond -> if ( CondExp ) { InstList } Cond'
    public AstNode Cond() {
        if (!MatchAndConsume("if")) return null;
        if (!MatchAndConsume("(")) return null;
        
        AstNode condNode = new AstNode("If");
        AstNode condition = CondExp();
        if (condition == null) return null;
        condNode.addChild(condition);
        
        if (!MatchAndConsume(")")) return null;
        if (!MatchAndConsume("{")) return null;
        
        AstNode thenBlock = InstList();
        if (thenBlock == null) return null;
        condNode.addChild(thenBlock);
        
        if (!MatchAndConsume("}")) return null;
        
        AstNode elsePart = CondPr();
        if (elsePart != null) {
            condNode.addChild(elsePart);
        }
        
        return condNode;
    }

    /// Cond' -> ε | else Cond''
    public AstNode CondPr() {
        if (CheckAndConsume("else")) {
            return CondPrPr();
        }
        return null; // epsilon
    }

    /// Cond'' -> { InstList } | Cond
    public AstNode CondPrPr() {
        if (CheckAndConsume("{")) {
            AstNode elseBlock = InstList();
            if (elseBlock == null) return null;
            
            if (!MatchAndConsume("}")) return null;
            return elseBlock;
        }
        return Cond(); // else if ...
    }

    /// For -> for ( Assign ; CondExp ; Incr ) { InstList }
    public AstNode For() {
        if (!MatchAndConsume("for")) return null;
        if (!MatchAndConsume("(")) return null;
        
        AstNode forNode = new AstNode("For");
        
        AstNode init = Assign();
        if (init == null) return null;
        forNode.addChild(init);
        
        if (!MatchAndConsume(";")) return null;
        
        AstNode cond = CondExp();
        if (cond == null) return null;
        forNode.addChild(cond);
        
        if (!MatchAndConsume(";")) return null;
        
        AstNode incr = Incr();
        if (incr == null) return null;
        forNode.addChild(incr);
        
        if (!MatchAndConsume(")")) return null;
        if (!MatchAndConsume("{")) return null;
        
        AstNode body = InstList();
        if (body == null) return null;
        forNode.addChild(body);
        
        if (!MatchAndConsume("}")) return null;
        
        return forNode;
    }
    
    /// Incr -> ident Incr'
    public AstNode Incr() {
        String id = tokens.get(currentIndex).getValue();
        if (!MatchAndConsume("ID")) return null;
        
        // IncrPr returns the operation part
        return IncrPr(id);
    }
    
    /// Incr' -> = ident Op Val | OpIncr
    public AstNode IncrPr(String id) {
        if (CheckAndConsume("=")) {
            // Assignment increment: i = i + 1
            AstNode assignNode = new AstNode("Assignment");
            assignNode.addChild(new AstNode("Identifier", id));
            
            // Parse RHS: ident Op Val
            String rhsId = tokens.get(currentIndex).getValue();
            if (!MatchAndConsume("ID")) return null;
            
            String op = currentToken;
            if (!Op()) return null; // Consumes op
            
            AstNode val = Val();
            if (val == null) return null;
            
            AstNode exprNode = new AstNode("BinaryOp", op);
            exprNode.addChild(new AstNode("Identifier", rhsId));
            exprNode.addChild(val);
            
            assignNode.addChild(exprNode);
            return assignNode;
        } else {
            // OpIncr: i++
            String op = currentToken;
            if (!OpIncr()) return null; // Consumes ++/--
            
            AstNode updateNode = new AstNode("Update", op);
            updateNode.addChild(new AstNode("Identifier", id));
            return updateNode;
        }
    }
    
    /// Switch -> switch ( ident ) { CaseList default > InstList }
    public AstNode Switch() {
        if (!MatchAndConsume("switch")) return null;
        if (!MatchAndConsume("(")) return null;
        
        String id = tokens.get(currentIndex).getValue();
        if (!MatchAndConsume("ID")) return null;
        
        if (!MatchAndConsume(")")) return null;
        if (!MatchAndConsume("{")) return null;
        
        AstNode switchNode = new AstNode("Switch");
        switchNode.addChild(new AstNode("Identifier", id));
        
        AstNode cases = new AstNode("Cases");
        if (!CaseList(cases)) return null;
        switchNode.addChild(cases);
        
        if (!MatchAndConsume("default")) return null;
        if (!MatchAndConsume(">")) return null;
        
        AstNode defaultNode = new AstNode("Default");
        AstNode defaultInst = InstList();
        if (defaultInst == null) return null;
        defaultNode.addChild(defaultInst);
        switchNode.addChild(defaultNode);
        
        if (!MatchAndConsume("}")) return null;
        
        return switchNode;
    }
    
    // CaseList helper
    public boolean CaseList(AstNode parent) {
        AstNode caseNode = Case();
        if (caseNode == null) return false;
        parent.addChild(caseNode);
        
        return CaseListPr(parent);
    }
    
    public boolean CaseListPr(AstNode parent) {
        if (isMatch("case")) {
            return CaseList(parent);
        }
        return true;
    }
    
    /// Case -> case Val > InstList break ;
    public AstNode Case() {
        if (!MatchAndConsume("case")) return null;
        
        AstNode val = Val();
        if (val == null) return null;
        
        if (!MatchAndConsume(">")) return null;
        
        AstNode body = InstList();
        if (body == null) return null;
        
        if (!MatchAndConsume("break")) return null;
        if (!MatchAndConsume(";")) return null;
        
        AstNode caseNode = new AstNode("Case");
        caseNode.addChild(val);
        caseNode.addChild(body);
        return caseNode;
    }

    /// Expr -> Term Expr'
    public AstNode Expr() {
        AstNode lhs = Term();
        if (lhs == null) return null;
        return ExprPr(lhs);
    }

    /// Expr' -> + Term Expr' | - Term Expr' | ε
    public AstNode ExprPr(AstNode lhs) {
        if (Contains(currentToken, new String[]{"+", "-"})) {
            String op = currentToken;
            consumeToken();
            AstNode rhs = Term();
            if (rhs == null) return null;
            
            AstNode newNode = new AstNode("BinaryOp", op);
            newNode.addChild(lhs);
            newNode.addChild(rhs);
            return ExprPr(newNode);
        }
        return lhs;
    }

    /// Term -> Factor Term'
    public AstNode Term() {
        AstNode lhs = Factor();
        if (lhs == null) return null;
        return TermPr(lhs);
    }

    /// Term' -> * Factor Term' | / Factor Term' | % Factor Term' | ε
    public AstNode TermPr(AstNode lhs) {
        if (Contains(currentToken, new String[]{"*", "/", "%"})) {
            String op = currentToken;
            consumeToken();
            AstNode rhs = Factor();
            if (rhs == null) return null;
            
            AstNode newNode = new AstNode("BinaryOp", op);
            newNode.addChild(lhs);
            newNode.addChild(rhs);
            return TermPr(newNode);
        }
        return lhs;
    }

    /// Factor -> ( CondExp ) CondExp' | Val Factor'
    public AstNode Factor() {
        if (CheckAndConsume("(")) {
            AstNode node = CondExp();
            if (node == null) return null;
            if (!MatchAndConsume(")")) return null;
            return CondExpPr(node);
        }
        
        AstNode val = Val();
        if (val == null) return null;
        return FactorPr(val);
    }

    /// Factor' -> ** Val | ε
    public AstNode FactorPr(AstNode lhs) {
        if (CheckAndConsume("**")) {
            AstNode rhs = Val();
            if (rhs == null) return null;
            
            AstNode newNode = new AstNode("BinaryOp", "**");
            newNode.addChild(lhs);
            newNode.addChild(rhs);
            return newNode; 
        }
        return lhs;
    }
    
    /// CondExp -> Expr CondExp'
    public AstNode CondExp() {
        AstNode lhs = Expr();
        if (lhs == null) return null;
        return CondExpPr(lhs);
    }
    
    /// CondExp' -> LogOp CondExp | ε
    public AstNode CondExpPr(AstNode lhs) {
        if (Contains(currentToken, logOp)) {
            String op = currentToken;
            consumeToken();
            AstNode rhs = CondExp(); // Recursive calls Expr -> ...
            if (rhs == null) return null;
            
            AstNode newNode = new AstNode("LogicOp", op);
            newNode.addChild(lhs);
            newNode.addChild(rhs);
            return newNode;
        }
        return lhs;
    }

    /// Val -> num | lit | Method | PI | INF | ident Val'
    public AstNode Val() {
        if (Contains(currentToken, vals)) { 
            AstNode node = new AstNode("Value", tokens.get(currentIndex).getValue());
            consumeToken();
            return node;
        } else if (isMatch("ID")) {
            String id = tokens.get(currentIndex).getValue();
            consumeToken();
            AstNode idNode = new AstNode("Identifier", id);
            
            // Val' -> [ Val ] | ε
            if (CheckAndConsume("[")) {
                 AstNode index = Val();
                 if (index == null) return null;
                 if (!MatchAndConsume("]")) return null;
                 
                 AstNode arrayAccess = new AstNode("ArrayAccess");
                 arrayAccess.addChild(idNode);
                 arrayAccess.addChild(index);
                 return arrayAccess;
            }
            return idNode;
        } else if (Contains(currentToken, methodNames)) {
            return Method();
        }
        return null;
    }

    /// Method -> MethodName ( Param )
    public AstNode Method() {
        String name = currentToken;
        if (!MethodName()) return null; // Consumes name
        
        if (!MatchAndConsume("(")) return null;
        
        AstNode methodNode = new AstNode("MethodCall", name);
        
        if (!Param(methodNode)) return null;
        
        if (!MatchAndConsume(")")) return null;
        return methodNode;
    }

    public boolean Param(AstNode parent) {
        AstNode val = Val();
        if (val == null) return false;
        parent.addChild(val);
        return ParamPr(parent);
    }
    
    public boolean ParamPr(AstNode parent) {
        if (CheckAndConsume(",")) {
            return Param(parent);
        }
        return true;
    }

    /// FParam -> ε | TypeIdent
    private boolean FParam(AstNode parent) {
        if (Contains(currentToken, dataTypes)) {
            return TypeIdent(parent);
        }
        return true;
    }
    
    /// TypeIdent -> Type ident TypeIdent'
    public boolean TypeIdent(AstNode parent) {
        AstNode typeNode = Type();
        if (typeNode == null) return false;
        
        String id = tokens.get(currentIndex).getValue();
        if (!MatchAndConsume("ID")) return false;
        
        AstNode param = new AstNode("Parameter");
        param.addChild(typeNode);
        param.addChild(new AstNode("Identifier", id));
        parent.addChild(param);
        
        return TypeIdentPr(parent);
    }
    
    public boolean TypeIdentPr(AstNode parent) {
        if (CheckAndConsume(",")) {
            return TypeIdent(parent);
        }
        return true;
    }

    // Helpers
    
    public boolean MethodName() {
        if (Contains(currentToken, methodNames)) {
            consumeToken();
            return true;
        }
        return false;
    }

    public boolean Op() {
        if (Contains(currentToken, op)) {
            consumeToken();
            return true;
        }
        return false;
    }
    
    public boolean OpIncr() {
        if (Contains(currentToken, opIncr)) {
            consumeToken();
            return true;
        }
        return false;
    }
    
    public boolean LogOp() {
        if (Contains(currentToken, logOp)) {
            consumeToken();
            return true;
        }
        return false;
    }

    private boolean MatchAndConsume(String token) {
        if (token.equals(currentToken)) {
            consumeToken();
            return true;
        }
        System.out.println("Expected '" + token + "', found '" + currentToken + "' at index " + currentIndex);
        return false;
    }

    private boolean CheckAndConsume(String token) {
        if (token.equals(currentToken)) {
            consumeToken();
            return true;
        }
        return false;
    }
    
    private boolean isMatch(String token) {
        return token.equals(currentToken);
    }
    
    private boolean Match(String token) {
        return token.equals(currentToken);
    }

    private void consumeToken() {
        currentIndex++;
        if (currentIndex < tokens.size()) {
            currentToken = tokens.get(currentIndex).getKey();
        } else {
            currentToken = "eof";
        }
    }

    private boolean Contains(String token, String[] list) {
        if (token == null || list == null) return false;
        for (String s : list) {
            if (token.equals(s)) return true;
        }
        return false;
    }
}
