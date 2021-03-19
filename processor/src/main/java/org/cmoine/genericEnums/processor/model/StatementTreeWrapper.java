package org.cmoine.genericEnums.processor.model;

import com.sun.source.tree.StatementTree;

public class StatementTreeWrapper {
    private final StatementTree statementTree;

    public StatementTreeWrapper(StatementTree statementTree) {
        this.statementTree = statementTree;
    }

    @Override
    public String toString() {
        return statementTree.toString();
    }
}
