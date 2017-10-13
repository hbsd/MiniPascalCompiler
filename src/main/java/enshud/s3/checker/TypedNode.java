package enshud.s3.checker;

import enshud.s2.parser.node.INode;

public class TypedNode implements INode {
	final IType type;
	final INode node;
	
	public TypedNode(IType type, INode node) {
		this.type = type;
		this.node = node;
	}

	@Override
	public boolean isSuccess() {
		return node.isSuccess();
	}

	@Override
	public int getLine() {
		return node.getLine();
	}

	@Override
	public int getColumn() {
		return node.getColumn();
	}

}
