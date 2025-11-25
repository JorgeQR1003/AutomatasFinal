package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final List<String> tokens;
    private String currentToken;
    private int currentIndex;
    private boolean res;

    private String[] dataTypes = {"string", "void", "int", "bool", "char", "double"};
    private String[] methodNames = {"Show", "Input", "Genf", "Deriv", "DerivX", "Integ", "IntegX", "Graph", "Slope", "Tab", "Root", "RootX", "Sen", "Cos", "Tan", "Sec", "Csc", "Cot", "Limit", "Concat", "Dist"};
    private String[] logOp = {"&&", "||", "!=", "==", "<=", ">=", "<", ">"};
    private String[] op = {"+", "-", "*", "/", "%", "**"};
    private String[] vals = {"NUM", "LITERAL", "INF", "PI"};
    private String[] opIncr = {"++", "--"};


    public Parser(String[] tokens) {
        this.tokens = Arrays.asList(tokens);
        this.tokens.add(0, "placeholder, just for pseudo inspo to work");
        this.tokens.add(-1, "eof");
        this.res = false;
        this.currentIndex = 0;
        this.currentToken = this.tokens.get(currentIndex);
    }

    /// Program -> Function Program | ε
    public boolean Program() {
        currentToken = getNextToken();
        res = Function();
        if (!res) {
            return false;
        } else {
            return Program();
        }
    }

    /// Type -> TypeVal Type’
    public boolean Type() {
        res = TypeVal();
        if (!res) {
            return false;
        }
        return TypePr();
    }

    /// Type’ -> ε | [ ]
    public boolean TypePr(){
        res = Match("[");
        if (res) {
            return Match("]");
        }
        return true;
    }

    /// TypeVal -> void | string | int | double | char | bool
    public boolean TypeVal() {
        res = Contains(currentToken, dataTypes);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// Op -> + | - | / | * | % | **
    public boolean Op() {
        res = Contains(currentToken, op);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    //MethodName -> Show | Graph | Deriv | DerivX | Integ | IntegX | Root | RootX | Limit | Genf | Sin | Cos | Tan | Sec | Csc | Cot | Concat | Tab | Slope | Dist | Input
    public boolean MethodName() {
        res = Contains(currentToken, methodNames);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// InstList -> Inst InstList | ε
    public boolean InstList() {
        res = Inst();
        if (!res) {
            return false;
        }
        else
            return InstList();
    }

    /// Assign ; | Cond | For | Switch | Method ; | return Val ;
    public boolean Inst() {

    }

    /// LogOp -> && | || | != | < | <= | > | >= | ==
    public boolean LogOp() {
        res = Contains(currentToken, logOp);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
    }



    /// Function -> Type ident ( FParam ) { InstList }
    public boolean Function() {
        res = Type();
        if (!res) {
            return false;
        }
        res = Match("ID");
        if (!res) {
            return false;
        }
        res = Match("(");
        if (!res) {
            return false;
        }
        res = FParam();
        if (!res) {
            return false;
        }
        res = Match(")");
        if (!res) {
            return false;
        }
        res = Match("{");
        if (!res) {
            return false;
        }
        res = InstList();
        if (!res) {
            return false;
        }
        res = Match("}");
        if (!res) {
            return false;
        }
        return true;
    }

    /// FParam -> ε | TypeIdent
    private boolean FParam() {
        if(Contains(currentToken, dataTypes)) {
            return TypeIdent();
        }
        return true;
    }


    /// TypeIdent -> Type ident TypeIdent’
    public boolean TypeIdent() {
        res = Type();
        if (!res) {
            return false;
        }
        res = Match("ID");
        if (!res) {
            return false;
        }
        return TypeIdentPr();
    }

    /// TypeIdent’ -> ε | , TypeIdent
    public boolean TypeIdentPr(){
        res = Match(",");
        if (res) {
            return TypeIdent();
        }
        return true;
    }


    /// Match non-terminals in a production rule with Terminal non-terminal Terminal style.
    public boolean Match(String token) {
        res = token.equals(currentToken);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
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

    /// Val -> num | lit | Method | PI | INF | ident Val’
    public boolean Val() {
        if (Contains(currentToken, vals)) {
            currentToken = getNextToken();
            return true;
        } else if (Match("ID")) {
            return ValPr();
        } else {
            return Method();
        }
    }

    /// Method -> MethodName ( Param )
    public boolean Method() {
        res = MethodName();
        if (!res) {
            return false;
        }
        res = Match("(");
        if (!res) {
            return false;
        }
        res = Param();
        if (!res) {
            return false;
        }
        res = Match(")");
        return res;
    }

    /// Param -> Val Param’
    public boolean Param() {
        res = Val();
        if (!res) {
            return false;
        }
        return ParamPr();
    }

    ///  Param’ -> ε | , Param
    public boolean ParamPr() {
        res = Match(",");
        if (res) {
            return Param();
        }
        return true;
    }

    /// Val’ -> ε | [ *num ]
    public boolean ValPr() {
        res = Match("[");
        if (res) {
            res = Val();
            if (!res) {
                return false;
            }
            return Match("]");
        }
        return true;
    }

    /// OpIncr -> ++ | - -
    public boolean OpIncr() {
        res = Contains(currentToken, opIncr);
        if (!res) {
            return false;
        }
        currentToken = getNextToken();
        return true;
    }

    /// Expr -> Term Expr’
    public boolean Expr() {
        res = Term();
        if (!res) {
            return false;
        }
        return ExprPr();
    }

    /// Term -> Factor Term’
    public boolean Term() {
        res = Factor();
        if (!res) {
            return false;
        }
        return TermPr();
    }

    /// Factor -> ( CondExp ) CondExp’ | Val Factor’
    public boolean Factor() {
        res = Match("(");
        if (res) {
            res = CondExp();
            if (!res) {
                return false;
            }
            res = Match(")");
            if  (!res) {
                return false;
            }
            return CondExprPr();
        }
        res = Val();
        if (!res) {
            return false;
        }
        return FactorPr();
    }

    /// Expr’ -> + Term Expr’ | - Term Expr’ | ε
    public boolean ExprPr() {
        String []  masmenos = {"+","-"};
        if(Contains(currentToken ,masmenos)){
            currentToken = getNextToken();
            res = Term();
            if (!res) {
                return false;
            }
            return ExprPr();
        }
        return true;
    }

    /// Term’ -> * Factor Term’ | / Factor Term’ | % Factor Term’ | ε
    public boolean TermPr(){
        String[] prdivmod = {"*", "/", " %"};
        if(Contains(currentToken ,prdivmod)){
            currentToken = getNextToken();
            res = Factor();
            if (!res) {
                return false;
            }
            return TermPr();
        }
        return true;
    }

    /// Factor’ -> ** Val | ε
    public boolean FactorPr(){
        res = Match("**");
        if (res) {
            return Val();
        }
        return true;
    }

    /// CondExp -> Expr CondExp’
    public boolean CondExp(){
        res = Expr();
        if (!res) {
            return false;
        }
        return CondExprPr();
    }

    /// CondExp’ -> LogOp CondExp | ε
    public boolean CondExprPr(){
        res = Contains(currentToken, logOp);
        if (res) {
            res = CondExp();
            if (!res) {
                return false;
            }
            return CondExp();
        }
        return true;
    }
}
