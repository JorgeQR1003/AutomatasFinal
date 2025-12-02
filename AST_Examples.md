# AST Examples Based on Grammar

This document shows example Abstract Syntax Trees (ASTs) for various constructs in your grammar.

## Example 1: Simple Function with Assignment

**Code:**
```
int Main() {
    int x = 10;
    return x;
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε (empty)
    ├── Identifier: "Main"
    ├── FParam: ε (empty)
    └── InstList
        ├── Inst (Assign)
        │   ├── Type
        │   │   ├── TypeVal: "int"
        │   │   └── TypePr: ε
        │   ├── Identifier: "x"
        │   └── Expr
        │       └── Term
        │           └── Factor
        │               └── Val: "NUM" (10)
        └── Inst (Return)
            └── Val
                └── Identifier: "x"
```

## Example 2: Arithmetic Expression

**Code:**
```
int Main() {
    int result = 10 + 5 * 2;
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Assign)
            ├── Type
            │   ├── TypeVal: "int"
            │   └── TypePr: ε
            ├── Identifier: "result"
            └── Expr
                ├── Term
                │   └── Factor
                │       └── Val: "NUM" (10)
                └── Expr'
                    ├── Operator: "+"
                    └── Term
                        ├── Factor
                        │   └── Val: "NUM" (5)
                        └── Term'
                            ├── Operator: "*"
                            └── Factor
                                └── Val: "NUM" (2)
```

## Example 3: If-Else Statement

**Code:**
```
void Main() {
    if (x > 10 && y == 5) {
        Show("Greater");
    } else {
        Show("Less");
    }
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "void"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Cond)
            ├── Keyword: "if"
            ├── CondExp
            │   ├── Expr
            │   │   └── Term
            │   │       └── Factor
            │   │           └── Val
            │   │               └── Identifier: "x"
            │   └── CondExp'
            │       ├── LogOp: ">"
            │       └── CondExp
            │           ├── Expr
            │           │   └── Term
            │           │       └── Factor
            │           │           └── Val: "NUM" (10)
            │           └── CondExp'
            │               ├── LogOp: "&&"
            │               └── CondExp
            │                   ├── Expr
            │                   │   └── Term
            │                   │       └── Factor
            │                   │           └── Val
            │                   │               └── Identifier: "y"
            │                   └── CondExp'
            │                       ├── LogOp: "=="
            │                       └── CondExp
            │                           └── Expr
            │                               └── Term
            │                                   └── Factor
            │                                       └── Val: "NUM" (5)
            ├── InstList (then branch)
            │   └── Inst (Method)
            │       ├── MethodName: "Show"
            │       └── Param
            │           └── Val: "LITERAL" ("Greater")
            └── CondPr (else)
                └── CondPrPr
                    └── InstList
                        └── Inst (Method)
                            ├── MethodName: "Show"
                            └── Param
                                └── Val: "LITERAL" ("Less")
```

## Example 4: For Loop

**Code:**
```
void Main() {
    for (i = 0; i < 10; i++) {
        Show(i);
    }
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "void"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (For)
            ├── Keyword: "for"
            ├── Assign (initialization)
            │   ├── Identifier: "i"
            │   └── Expr
            │       └── Term
            │           └── Factor
            │               └── Val: "NUM" (0)
            ├── CondExp (condition)
            │   ├── Expr
            │   │   └── Term
            │   │       └── Factor
            │   │           └── Val
            │   │               └── Identifier: "i"
            │   └── CondExp'
            │       ├── LogOp: "<"
            │       └── CondExp
            │           └── Expr
            │               └── Term
            │                   └── Factor
            │                       └── Val: "NUM" (10)
            ├── Incr (increment)
            │   ├── Identifier: "i"
            │   └── IncrPr
            │       └── OpIncr: "++"
            └── InstList (body)
                └── Inst (Method)
                    ├── MethodName: "Show"
                    └── Param
                        └── Val
                            └── Identifier: "i"
```

## Example 5: Switch Statement

**Code:**
```
void Main() {
    switch (opcion) {
        case 1 > Show("One"); break;
        case 2 > Show("Two"); break;
        default > Show("Other");
    }
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "void"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Switch)
            ├── Keyword: "switch"
            ├── Identifier: "opcion"
            ├── CaseList
            │   ├── Case
            │   │   ├── Keyword: "case"
            │   │   ├── Val: "NUM" (1)
            │   │   ├── InstList
            │   │   │   └── Inst (Method)
            │   │   │       ├── MethodName: "Show"
            │   │   │       └── Param
            │   │   │           └── Val: "LITERAL" ("One")
            │   │   └── Keyword: "break"
            │   └── CaseList'
            │       └── CaseList
            │           └── Case
            │               ├── Keyword: "case"
            │               ├── Val: "NUM" (2)
            │               ├── InstList
            │               │   └── Inst (Method)
            │               │       ├── MethodName: "Show"
            │               │       └── Param
            │               │           └── Val: "LITERAL" ("Two")
            │               └── Keyword: "break"
            ├── Keyword: "default"
            └── InstList
                └── Inst (Method)
                    ├── MethodName: "Show"
                    └── Param
                        └── Val: "LITERAL" ("Other")
```

## Example 6: Function with Parameters

**Code:**
```
int Add(int x, int y) {
    return x + y;
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε
    ├── Identifier: "Add"
    ├── FParam
    │   └── TypeIdent
    │       ├── Type
    │       │   ├── TypeVal: "int"
    │       │   └── TypePr: ε
    │       ├── Identifier: "x"
    │       └── TypeIdentPr
    │           └── TypeIdent
    │               ├── Type
    │               │   ├── TypeVal: "int"
    │               │   └── TypePr: ε
    │               └── Identifier: "y"
    └── InstList
        └── Inst (Return)
            └── Val
                └── Expr
                    ├── Term
                    │   └── Factor
                    │       └── Val
                    │           └── Identifier: "x"
                    └── Expr'
                        ├── Operator: "+"
                        └── Term
                            └── Factor
                                └── Val
                                    └── Identifier: "y"
```

## Example 7: Array Declaration and Access

**Code:**
```
int Main() {
    int[] arr = 5;
    int value = arr[0];
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        ├── Inst (Assign)
        │   ├── Type
        │   │   ├── TypeVal: "int"
        │   │   └── TypePr
        │   │       └── Array brackets: "[", "]"
        │   ├── Identifier: "arr"
        │   └── Expr
        │       └── Term
        │           └── Factor
        │               └── Val: "NUM" (5)
        └── Inst (Assign)
            ├── Type
            │   ├── TypeVal: "int"
            │   └── TypePr: ε
            ├── Identifier: "value"
            └── Expr
                └── Term
                    └── Factor
                        └── Val
                            ├── Identifier: "arr"
                            └── ValPr
                                ├── "["
                                ├── Val: "NUM" (0)
                                └── "]"
```

## Example 8: Method Call with Multiple Parameters

**Code:**
```
void Main() {
    Show(10, "Hello", x);
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "void"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Method)
            ├── MethodName: "Show"
            └── Param
                ├── Val: "NUM" (10)
                └── ParamPr
                    └── Param
                        ├── Val: "LITERAL" ("Hello")
                        └── ParamPr
                            └── Param
                                └── Val
                                    └── Identifier: "x"
```

## Example 9: Exponentiation

**Code:**
```
int Main() {
    int result = 2 ** 3;
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Assign)
            ├── Type
            │   ├── TypeVal: "int"
            │   └── TypePr: ε
            ├── Identifier: "result"
            └── Expr
                └── Term
                    └── Factor
                        ├── Val: "NUM" (2)
                        └── FactorPr
                            ├── Operator: "**"
                            └── Val: "NUM" (3)
```

## Example 10: Complex Expression with Parentheses

**Code:**
```
int Main() {
    int result = (x + y) * (a - b);
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "int"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (Assign)
            ├── Type
            │   ├── TypeVal: "int"
            │   └── TypePr: ε
            ├── Identifier: "result"
            └── Expr
                └── Term
                    ├── Factor
                    │   ├── "("
                    │   ├── CondExp
                    │   │   └── Expr
                    │   │       ├── Term
                    │   │       │   └── Factor
                    │   │       │       └── Val
                    │   │       │           └── Identifier: "x"
                    │   │       └── Expr'
                    │   │           ├── Operator: "+"
                    │   │           └── Term
                    │   │               └── Factor
                    │   │                   └── Val
                    │   │                       └── Identifier: "y"
                    │   └── ")"
                    └── Term'
                        ├── Operator: "*"
                        └── Factor
                            ├── "("
                            ├── CondExp
                            │   └── Expr
                            │       ├── Term
                            │       │   └── Factor
                            │       │       └── Val
                            │       │           └── Identifier: "a"
                            │       └── Expr'
                            │           ├── Operator: "-"
                            │           └── Term
                            │               └── Factor
                            │                   └── Val
                            │                       └── Identifier: "b"
                            └── ")"
```

## Example 11: For Loop with Complex Increment

**Code:**
```
void Main() {
    for (i = 0; i < 10; i = i + 1) {
        Show(i);
    }
}
```

**AST:**
```
Program
└── Function
    ├── Type
    │   ├── TypeVal: "void"
    │   └── TypePr: ε
    ├── Identifier: "Main"
    ├── FParam: ε
    └── InstList
        └── Inst (For)
            ├── Keyword: "for"
            ├── Assign (initialization)
            │   ├── Identifier: "i"
            │   └── Expr
            │       └── Term
            │           └── Factor
            │               └── Val: "NUM" (0)
            ├── CondExp (condition)
            │   ├── Expr
            │   │   └── Term
            │   │       └── Factor
            │   │           └── Val
            │   │               └── Identifier: "i"
            │   └── CondExp'
            │       ├── LogOp: "<"
            │       └── CondExp
            │           └── Expr
            │               └── Term
            │                   └── Factor
            │                       └── Val: "NUM" (10)
            ├── Incr (increment)
            │   ├── Identifier: "i"
            │   └── IncrPr
            │       ├── "="
            │       ├── Identifier: "i"
            │       ├── Op: "+"
            │       └── Val: "NUM" (1)
            └── InstList (body)
                └── Inst (Method)
                    ├── MethodName: "Show"
                    └── Param
                        └── Val
                            └── Identifier: "i"
```

## Example 12: Multiple Functions

**Code:**
```
int Add(int x, int y) {
    return x + y;
}

void Main() {
    int result = Add(5, 3);
}
```

**AST:**
```
Program
├── Function
│   ├── Type
│   │   ├── TypeVal: "int"
│   │   └── TypePr: ε
│   ├── Identifier: "Add"
│   ├── FParam
│   │   └── TypeIdent
│   │       ├── Type
│   │       │   ├── TypeVal: "int"
│   │       │   └── TypePr: ε
│   │       ├── Identifier: "x"
│   │       └── TypeIdentPr
│   │           └── TypeIdent
│   │               ├── Type
│   │               │   ├── TypeVal: "int"
│   │               │   └── TypePr: ε
│   │               └── Identifier: "y"
│   └── InstList
│       └── Inst (Return)
│           └── Val
│               └── Expr
│                   ├── Term
│                   │   └── Factor
│                   │       └── Val
│                   │           └── Identifier: "x"
│                   └── Expr'
│                       ├── Operator: "+"
│                       └── Term
│                           └── Factor
│                               └── Val
│                                   └── Identifier: "y"
└── Program
    └── Function
        ├── Type
        │   ├── TypeVal: "void"
        │   └── TypePr: ε
        ├── Identifier: "Main"
        ├── FParam: ε
        └── InstList
            └── Inst (Assign)
                ├── Type
                │   ├── TypeVal: "int"
                │   └── TypePr: ε
                ├── Identifier: "result"
                └── Expr
                    └── Term
                        └── Factor
                            └── Val
                                └── Method
                                    ├── MethodName: "Add"
                                    └── Param
                                        ├── Val: "NUM" (5)
                                        └── ParamPr
                                            └── Param
                                                └── Val: "NUM" (3)
```

## Notes on AST Structure

1. **Program**: Root node containing zero or more Functions
2. **Function**: Contains Type, Identifier, FParam (parameters), and InstList (body)
3. **Type**: Can be simple (TypeVal) or array (TypeVal + TypePr with brackets)
4. **InstList**: List of instructions (can be empty)
5. **Inst**: Can be Assign, Cond, For, Switch, Method call, or Return
6. **Expr**: Handles arithmetic with proper precedence (Term handles *, /, %; Expr handles +, -)
7. **CondExp**: Handles logical expressions with operators (&&, ||, ==, !=, <, >, <=, >=)
8. **Val**: Can be literal, identifier, method call, or array access
9. **Method**: Built-in methods like Show, Input, Graph, etc.
10. **For**: Contains initialization (Assign), condition (CondExp), increment (Incr), and body (InstList)
11. **Switch**: Contains identifier, CaseList, and default case
12. **Cond**: If statement with optional else (CondPr)

