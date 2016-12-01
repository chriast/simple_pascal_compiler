package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ArrayType extends Type {
	Type size2;
	Type type2;

	public ArrayType(int lineNum, Type size, Type type3) {
		super(lineNum);

		this.size2 = size;
		this.type2 = type3;
	}

	@Override
	public String identify() {
		return "<ArrayType> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint("array[");
		size2.prettyPrint();
		Main.log.prettyPrint("] of ");
		type2.prettyPrint();
	}

	public int findSize(Block curScope) {
		int retVal = 0;
		if (size2 instanceof RangeType) {
			// The size of an array will at some point come down to a range
			RangeType rangePointer = (RangeType) size2;

			if (rangePointer.c2.getVal(curScope) > rangePointer.c1.getVal(curScope)) {
				retVal = rangePointer.c2.getVal(curScope) - rangePointer.c1.getVal(curScope);
			} else {
				retVal = rangePointer.c1.getVal(curScope) - rangePointer.c2.getVal(curScope);
			}
		} else if (size2 instanceof ArrayType) {
			// If it's an array, find the size of the array
			ArrayType arrayPointer = (ArrayType) size2;
			retVal = arrayPointer.findSize(curScope);
		} else if (size2 instanceof TypeName) {
			TypeName typePointer = (TypeName) size2;
			// If it's a type name, find the size of the type it refers to
			retVal = typePointer.findSize(curScope);
		}
		return retVal;
	}

	
	public static ArrayType parse(Scanner s) {
		enterParser("ArrayType");

		s.skip(arrayToken);
		s.skip(leftBracketToken);

		int num = s.curLineNum();
		Type size = Type.parse(s);
		s.skip(rightBracketToken);
		s.skip(ofToken);
		Type type3 = Type.parse(s);

		ArrayType AT = new ArrayType(num, size, type3);

		leaveParser("ArrayType");
		return AT;
	}

	@Override
	public void check(Block curScope, Library lib) {
		if (size2 instanceof RangeType) {
			size2.check(curScope, lib);
		}
		if (type2 instanceof RangeType) {
			type2.check(curScope, lib);
		}

	}

	public void recursiveEnumCheck(Block b) {
		if (size2 instanceof EnumType) {
			// enum check for arrays
			EnumType e2tmp = (EnumType) size2;
			for (int j = 0; j < e2tmp.enumList.size(); j++) {
				EnumLiteral ee2tmp = e2tmp.enumList.get(j);
				b.addDecl(ee2tmp.name.toLowerCase(), ee2tmp);
			}
		}
		if (type2 instanceof EnumType) {
			// enum check for arrays
			EnumType e3tmp = (EnumType) type2;
			for (int j = 0; j < e3tmp.enumList.size(); j++) {
				EnumLiteral ee3tmp = e3tmp.enumList.get(j);
				b.addDecl(ee3tmp.name.toLowerCase(), ee3tmp);
			}
		}

		if (size2 instanceof ArrayType) {
			ArrayType atmp = (ArrayType) size2;
			atmp.recursiveEnumCheck(b);
		}
		if (type2 instanceof ArrayType) {
			ArrayType atmp = (ArrayType) type2;
			atmp.recursiveEnumCheck(b);
		}
	}

	public int lowerLimit() {
		
		int retVal = 0;
		if (size2 instanceof RangeType) {
			// The indexing of an array will at some point come down to a range
			RangeType rangePointer = (RangeType) size2;
			if (rangePointer.c2.getVal2() > rangePointer.c1.getVal2()) {
				//System.out.println("This happens");
				retVal = rangePointer.c1.getVal2();
			} else {
				retVal = rangePointer.c2.getVal2();
			}
		} else if (size2 instanceof ArrayType) {
			// If it's an array, find the lowest index of the array
			ArrayType arrayPointer = (ArrayType) size2;
			retVal = arrayPointer.lowerLimit();
		} else if (size2 instanceof TypeName) {
			TypeName typePointer = (TypeName) size2;
			// If it's a type name, find the lowest value of the type it refers to(which will at some point be either a range or an array)
			retVal = typePointer.lowerLimit();
		}
		return retVal;
	}
	
	@Override
	public void genCode(CodeFile f) {

	}
}
