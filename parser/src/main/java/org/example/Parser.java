package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private final List<String> tokens;
    private String currentToken;
    private int currentIndex;
    private boolean res;

    private String[] dataTypes = {"string", "void", "int", "bool", "char", "double"};
    private String[] firstAssign = {"string", "void", "int", "bool", "char", "double", "ID"};
    private String[] methodNames = {"Show", "Input", "Genf", "Deriv", "DerivX", "Integ", "IntegX", "Graph", "Slope", "Tab", "Root", "RootX", "Sen", "Cos", "Tan", "Sec", "Csc", "Cot", "Limit", "Concat", "Dist"};
    private String[] logOp = {"&&", "||", "!=", "==", "<=", ">=", "<", ">"};
    private String[] op = {"+", "-", "*", "/", "%", "**"};
    private String[] vals = {"NUM", "LITERAL", "INF", "PI"};
    private String[] opIncr = {"++", "--"};
    private String[] syntacticWords = {"{", "}", "(", ")", "[", "]", ";", "," };


    public Parser(String[] tokens) {
        this.tokens = new ArrayList<>(Arrays.asList(tokens));
        this.tokens.add("eof");
        this.res = false;
        this.currentIndex = 0;
        this.currentToken = this.tokens.get(currentIndex);
    }

    public void Parse() {
        System.out.println("Starting parse. Current token: " + currentToken + " (index: " + currentIndex + ")");
        res = Program();
        if (!res) {
            System.out.println("Error: Program failed at token: " + currentToken + " (index: " + currentIndex + ")");
        }
        else {
            System.out.println("Program parsed successfully");
        }
    }

    /// Program -> Function Program | ε
    public boolean Program() {
        if (Contains(currentToken, dataTypes)) {
            res = Function();
            if (!res) {
                System.out.println("Error in Program: Function() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return Program();
        }
        return true;
    }

    /// Type -> TypeVal Type'
    public boolean Type() {
        res = TypeVal();
        if (!res) {
            System.out.println("Error in Type: TypeVal() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = TypePr();
        if (!res) {
            System.out.println("Error in Type: TypePr() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return true;
    }

    /// Type' -> ε | [ ]
    public boolean TypePr(){
        res = Match("[");
        if (res) {
            currentToken = getNextToken();
            res = Match("]");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in TypePr: Expected ']', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return true;
    }

    /// TypeVal -> void | string | int | double | char | bool
    public boolean TypeVal() {
        res = Contains(currentToken, dataTypes);
        if (!res) {
            System.out.println("Error in TypeVal: Expected dataType, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// Op -> + | - | / | * | % | **
    public boolean Op() {
        res = Contains(currentToken, op);
        if (!res) {
            System.out.println("Error in Op: Expected operator, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    //MethodName -> Show | Graph | Deriv | DerivX | Integ | IntegX | Root | RootX | Limit | Genf | Sin | Cos | Tan | Sec | Csc | Cot | Concat | Tab | Slope | Dist | Input
    public boolean MethodName() {
        res = Contains(currentToken, methodNames);
        if (!res) {
            System.out.println("Error in MethodName: Expected method name, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// InstList -> Inst InstList | ε
    public boolean InstList() {
        if (Contains(currentToken, firstAssign) || Contains(currentToken, methodNames) || Match("if") || Match("for") ||  Match("switch") || Match("return")) {
            res = Inst();
            if (!res) {
                System.out.println("Error in InstList: Inst() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return InstList();
        }
        return true;
    }

    /// Assign ; | Cond | For | Switch | Method ; | return Val ;
    public boolean Inst() {
        if(Match("return")){
            currentToken = getNextToken();
            res = Val();
            if (!res) {
                System.out.println("Error in Inst: Val() failed after return at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match(";");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in Inst: Expected ';' after return Val, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        if(Contains(currentToken, firstAssign)) {
            res = Assign();
            if (!res) {
                System.out.println("Error in Inst: Assign() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match(";");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in Inst: Expected ';' after Assign, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        if(Match("if")) {
            return Cond();
        }
        if(Match("for")) {
            return For();
        }
        if(Match("switch")) {
            return Switch();
        }
        if(Contains(currentToken, methodNames)) {
            res = Method();
            if (!res) {
                System.out.println("Error in Inst: Method() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match(";");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in Inst: Expected ';' after Method, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        System.out.println("Error in Inst: Unexpected token: " + currentToken + " (index: " + currentIndex + ")");
        return false;
    }

    /// LogOp -> && | || | != | < | <= | > | >= | ==
    public boolean LogOp() {
        res = Contains(currentToken, logOp);
        if (!res) {
            System.out.println("Error in LogOp: Expected logical operator, found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }



    /// Function -> Type ident ( FParam ) { InstList }
    public boolean Function() {
        res = Type();
        if (!res) {
            System.out.println("Error in Function: Type() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("ID");
        if (!res) {
            System.out.println("Error in Function: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("(");
        if (!res) {
            System.out.println("Error in Function: Expected '(', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = FParam();
        if (!res) {
            System.out.println("Error in Function: FParam() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(")");
        if (!res) {
            System.out.println("Error in Function: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("{");
        if (!res) {
            System.out.println("Error in Function: Expected '{', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = InstList();
        if (!res) {
            System.out.println("Error in Function: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("}");
        if (!res) {
            System.out.println("Error in Function: Expected '}', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// FParam -> ε | TypeIdent
    private boolean FParam() {
        if(Contains(currentToken, dataTypes)) {
            return TypeIdent();
        }
        return true;
    }


    /// TypeIdent -> Type ident TypeIdent'
    public boolean TypeIdent() {
        res = Type();
        if (!res) {
            System.out.println("Error in TypeIdent: Type() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("ID");
        if (!res) {
            System.out.println("Error in TypeIdent: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return TypeIdentPr();
    }

    /// TypeIdent' -> ε | , TypeIdent
    public boolean TypeIdentPr(){
        res = Match(",");
        if (res) {
            currentToken = getNextToken();
            return TypeIdent();
        }
        return true;
    }


    /// Match non-terminals in a production rule with Terminal non-terminal Terminal style.
    /// If true, advance to next token, else dont.
    public boolean Match(String token) {
        return token.equals(currentToken);
    }

    /// Get next token in the token arraylist
    private String getNextToken() {
        currentIndex++;
        if (currentIndex >= tokens.size()) {
            System.out.println("No more tokens to analyze, this should not happen, I tink");
            res = false;
        }
        return tokens.get(currentIndex);
    }

    /// Contains as if .NET, java is shit, does not have C# functions
    private boolean Contains(String token, String[] stringsToCompareTo) {
        if (token == null || stringsToCompareTo == null) {
            return false;
        }
        for (String str : stringsToCompareTo) {
            if (token.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /// Val -> num | lit | Method | PI | INF | ident Val'
    public boolean Val() {
        if (Contains(currentToken, vals)) {
            currentToken = getNextToken();
            return true;
        } else if (Match("ID")) {
            currentToken = getNextToken();
            return ValPr();
        } else {
            res = Method();
            if (!res) {
                System.out.println("Error in Val: Method() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return true;
        }
    }

    /// Method -> MethodName ( Param )
    public boolean Method() {
        res = MethodName();
        if (!res) {
            System.out.println("Error in Method: MethodName() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("(");
        if (!res) {
            System.out.println("Error in Method: Expected '(', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Param();
        if (!res) {
            System.out.println("Error in Method: Param() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(")");
        if (res) {
            currentToken = getNextToken();
            return true;
        }
        System.out.println("Error in Method: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
        return false;
    }

    /// Param -> Val Param'
    public boolean Param() {
        res = Val();
        if (!res) {
            System.out.println("Error in Param: Val() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return ParamPr();
    }

    ///  Param' -> ε | , Param
    public boolean ParamPr() {
        res = Match(",");
        if (res) {
            currentToken = getNextToken();
            return Param();
        }
        return true;
    }

    /// Val' -> ε | [ *num ]
    public boolean ValPr() {
        res = Match("[");
        if (res) {
            currentToken = getNextToken();
            res = Val();
            if (!res) {
                System.out.println("Error in ValPr: Val() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match("]");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in ValPr: Expected ']', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return true;
    }

    /// OpIncr -> ++ | - -
    public boolean OpIncr() {
        res = Contains(currentToken, opIncr);
        if (!res) {
            System.out.println("Error in OpIncr: Expected '++' or '--', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// Expr -> Term Expr'
    public boolean Expr() {
        res = Term();
        if (!res) {
            System.out.println("Error in Expr: Term() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return ExprPr();
    }

    /// Term -> Factor Term'
    public boolean Term() {
        res = Factor();
        if (!res) {
            System.out.println("Error in Term: Factor() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return TermPr();
    }

    /// Factor -> ( CondExp ) CondExp' | Val Factor'
    public boolean Factor() {
        res = Match("(");
        if (res) {
            currentToken = getNextToken();
            res = CondExp();
            if (!res) {
                System.out.println("Error in Factor: CondExp() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match(")");
            if  (!res) {
                System.out.println("Error in Factor: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            currentToken = getNextToken();
            return CondExpPr();
        }
        res = Val();
        if (!res) {
            System.out.println("Error in Factor: Val() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return FactorPr();
    }

    /// Expr' -> + Term Expr' | - Term Expr' | ε
    public boolean ExprPr() {
        String []  masmenos = {"+","-"};
        if(Contains(currentToken ,masmenos)){
            currentToken = getNextToken();
            res = Term();
            if (!res) {
                System.out.println("Error in ExprPr: Term() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return ExprPr();
        }
        return true;
    }

    /// Term' -> * Factor Term' | / Factor Term' | % Factor Term' | ε
    public boolean TermPr(){
        String[] prdivmod = {"*", "/", "%"};
        if(Contains(currentToken ,prdivmod)){
            currentToken = getNextToken();
            res = Factor();
            if (!res) {
                System.out.println("Error in TermPr: Factor() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return TermPr();
        }
        return true;
    }

    /// Factor' -> ** Val | ε
    public boolean FactorPr(){
        res = Match("**");
        if (res) {
            currentToken = getNextToken();
            res = Val();
            if (!res) {
                System.out.println("Error in FactorPr: Val() failed after '**' at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return true;
        }
        return true;
    }

    /// CondExp -> Expr CondExp'
    public boolean CondExp(){
        res = Expr();
        if (!res) {
            System.out.println("Error in CondExp: Expr() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return CondExpPr();
    }

    /// CondExp' -> LogOp CondExp | ε
    public boolean CondExpPr(){
        if(Contains(currentToken, logOp)) {
            res = LogOp();
            if (!res) {
                System.out.println("Error in CondExpPr: LogOp() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return CondExp();
        }
        return true;
    }

    /// Cond -> if ( CondExp ) { InstList } Cond'
    public boolean Cond() {
        res = Match("if");
        if (!res) {
            System.out.println("Error in Cond: Expected 'if', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("(");
        if (!res) {
            System.out.println("Error in Cond: Expected '(', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = CondExp();
        if (!res) {
            System.out.println("Error in Cond: CondExp() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(")");
        if (!res) {
            System.out.println("Error in Cond: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("{");
        if (!res) {
            System.out.println("Error in Cond: Expected '{', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = InstList();
        if (!res) {
            System.out.println("Error in Cond: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("}");
        if (!res) {
            System.out.println("Error in Cond: Expected '}', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return CondPr();
    }

    /// Cond' -> ε | else Cond''
    public boolean CondPr(){
        if(Match("else")){
            currentToken = getNextToken();
            return CondPrPr();
        }
        return true;
    }

    /// Cond'' -> { InstList } | Cond
    public boolean CondPrPr(){
        if(Match("{")){
            currentToken = getNextToken();
            res = InstList();
            if (!res) {
                System.out.println("Error in CondPrPr: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Match("}");
            if (res) {
                currentToken = getNextToken();
                return true;
            }
            System.out.println("Error in CondPrPr: Expected '}', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return Cond();
    }

    /// For -> for ( Assign ; CondExp ; Incr ) { InstList }
    public boolean For() {
        res = Match("for");
        if (!res) {
            System.out.println("Error in For: Expected 'for', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("(");
        if (!res) {
            System.out.println("Error in For: Expected '(', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Assign();
        if (!res) {
            System.out.println("Error in For: Assign() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(";");
        if (!res) {
            System.out.println("Error in For: Expected ';', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = CondExp();
        if (!res) {
            System.out.println("Error in For: CondExp() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(";");
        if (!res) {
            System.out.println("Error in For: Expected ';', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Incr();
        if (!res) {
            System.out.println("Error in For: Incr() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(")");
        if (!res) {
            System.out.println("Error in For: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("{");
        if (!res) {
            System.out.println("Error in For: Expected '{', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = InstList();
        if (!res) {
            System.out.println("Error in For: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("}");
        if (res) {
            currentToken = getNextToken();
            return true;
        }
        System.out.println("Error in For: Expected '}', found: " + currentToken + " (index: " + currentIndex + ")");
        return false;
    }

    /// Switch -> switch ( ident ) { CaseList default > InstList }
    public boolean Switch() {
        res = Match("switch");
        if (!res) {
            System.out.println("Error in Switch: Expected 'switch', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("(");
        if (!res) {
            System.out.println("Error in Switch: Expected '(', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("ID");
        if (!res) {
            System.out.println("Error in Switch: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match(")");
        if (!res) {
            System.out.println("Error in Switch: Expected ')', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("{");
        if (!res) {
            System.out.println("Error in Switch: Expected '{', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = CaseList();
        if (!res) {
            System.out.println("Error in Switch: CaseList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("default");
        if (!res) {
            System.out.println("Error in Switch: Expected 'default', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match(">");
        if (!res) {
            System.out.println("Error in Switch: Expected '>', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = InstList();
        if (!res) {
            System.out.println("Error in Switch: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("}");
        if (res) {
            currentToken = getNextToken();
            return true;
        }
        System.out.println("Error in Switch: Expected '}', found: " + currentToken + " (index: " + currentIndex + ")");
        return false;
    }

    /// CaseList -> Case CaseList'
    public boolean CaseList() {
        res = Case();
        if (!res) {
            System.out.println("Error in CaseList: Case() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return CaseListPr();
    }

    /// CaseList' -> ε | CaseList
    public boolean CaseListPr(){
        if(Match("case")){
            currentToken = getNextToken();
            return CaseList();
        }
        return true;
    }

    /// Case -> case Val > InstList break ;
    public boolean Case(){
        res = Match("case");
        if (!res) {
            System.out.println("Error in Case: Expected 'case', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Val();
        if (!res) {
            System.out.println("Error in Case: Val() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match(">");
        if (!res) {
            System.out.println("Error in Case: Expected '>', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = InstList();
        if (!res) {
            System.out.println("Error in Case: InstList() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res =  Match("break");
        if (!res) {
            System.out.println("Error in Case: Expected 'break', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match(";");
        if (res) {
            currentToken = getNextToken();
            return true;
        }
        System.out.println("Error in Case: Expected ';', found: " + currentToken + " (index: " + currentIndex + ")");
        return false;
    }

    /// Incr -> ident Incr'
    public boolean Incr() {
        res = Match("ID");
        if (!res) {
            System.out.println("Error in Incr: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        return IncrPr();
    }

    /// Incr' -> = ident Op Val | OpIncr
    public boolean IncrPr(){
        if (Match("=")){
            currentToken = getNextToken();
            res = Match("ID");
            if (!res) {
                System.out.println("Error in IncrPr: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            currentToken = getNextToken();
            res = Op();
            if (!res) {
                System.out.println("Error in IncrPr: Op() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            res = Val();
            if (!res) {
                System.out.println("Error in IncrPr: Val() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return true;
        }
        return OpIncr();
    }

    /// Assign -> Type ident = Expr | ident = Expr
    public boolean Assign() {
        if(Match("ID")){
            currentToken = getNextToken();
            res = Match("=");
            if  (!res) {
                System.out.println("Error in Assign: Expected '=', found: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            currentToken = getNextToken();
            res = Expr();
            if (!res) {
                System.out.println("Error in Assign: Expr() failed at token: " + currentToken + " (index: " + currentIndex + ")");
                return false;
            }
            return true;
        }
        res = Type();
        if (!res) {
            System.out.println("Error in Assign: Type() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        res = Match("ID");
        if (!res) {
            System.out.println("Error in Assign: Expected 'ID', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Match("=");
        if (!res) {
            System.out.println("Error in Assign: Expected '=', found: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        currentToken = getNextToken();
        res = Expr();
        if (!res) {
            System.out.println("Error in Assign: Expr() failed at token: " + currentToken + " (index: " + currentIndex + ")");
            return false;
        }
        return true;
    }
}


