package org.example;

import java.util.ArrayList;
import java.util.List;

public class AstNode {
    private String label;      // e.g., "Function", "If", "Assign", "BinaryOp"
    private String value;      // e.g., "+", "10", "myVar" (optional)
    public List<AstNode> children;

    public AstNode(String label) {
        this(label, "");
    }

    public AstNode(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(AstNode child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
    public List<AstNode> getChildren() { return children; }

    @Override
    public String toString() {
        return toStringRecursive(0);
    }

    private String toStringRecursive(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) sb.append("  ");
        
        sb.append(label);
        if (value != null && !value.isEmpty()) {
            sb.append(": ").append(value);
        }
        sb.append("\n");

        for (AstNode child : children) {
            sb.append(child.toStringRecursive(indent + 1));
        }
        return sb.toString();
    }
}
