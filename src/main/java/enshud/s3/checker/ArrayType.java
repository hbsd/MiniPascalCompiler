package enshud.s3.checker;

public class ArrayType implements IType {
	final RegularType type;
	final int begin;
	final int end;
	public ArrayType(RegularType type, int begin, int end) {
		this.type = type;
		this.begin = begin;
		this.end = end;
	}
}
