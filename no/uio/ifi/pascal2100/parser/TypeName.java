package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class TypeName extends Type {
	public String name;
	PascalDecl declPointer;

	public TypeName(int lNum) {
		super(lNum);
	}

	@Override
	public String identify() {
		return "<TypeName> " + name + " on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint(name);
	}

	public static TypeName parse(Scanner s) {
		enterParser("TypeName");

		s.test(nameToken);
		TypeName TN = new TypeName(s.curToken.lineNum);
		TN.name = s.curToken.id;

		s.readNextToken();

		leaveParser("TypeName");
		return TN;

	}

	@Override
	public void check(Block curScope, Library lib) {
		declPointer = curScope.findDecl(name.toLowerCase(), this, false);
	}

	@Override
	public void genCode(CodeFile f) {

	}

	public int findSize(Block curScope) {
		// Used by ArrayType if the TypeName-object is used as the size for an array
		TypeDecl typePointer = (TypeDecl) declPointer;
		int retVal = 0;
		if (typePointer.T instanceof RangeType) {
			// If it's a range, finde the size of the range
			RangeType rangePointer = (RangeType) typePointer.T;
			if (rangePointer.c2.getVal(curScope) > rangePointer.c1.getVal(curScope)) {
				retVal = rangePointer.c2.getVal(curScope) - rangePointer.c1.getVal(curScope);
			} else {
				retVal = rangePointer.c1.getVal(curScope) - rangePointer.c2.getVal(curScope);
			}
		} else if (typePointer.T instanceof ArrayType) {
			// If it's an array, find the size of the array
			ArrayType arrayPointer = (ArrayType) typePointer.T;
			retVal = arrayPointer.findSize(curScope);
		} else if (typePointer.T instanceof TypeName) {
			// If it's another type name, keep searching
			TypeName typeNamePointer = (TypeName) typePointer.T;
			retVal = typeNamePointer.findSize(curScope);
		}
		return retVal;
	}
	
	public int lowerLimit() {
		// Used by ArrayType to finde the lower limit of the array if the TypeName-object is used as the size for an array
		TypeDecl typePointer = (TypeDecl) declPointer;
		int retVal = 0;
		if (typePointer.T instanceof RangeType) {
			// If it's a range, find the lowest value of the range of the range
			RangeType rangePointer = (RangeType) typePointer.T;
			if (rangePointer.c2.getVal2() > rangePointer.c1.getVal2()) {
				retVal = rangePointer.c1.getVal2();
			} else {
				retVal = rangePointer.c2.getVal2();
			}
		} else if (typePointer.T instanceof ArrayType) {
			// If it's an array, find the lowest index of the array
			ArrayType arrayPointer = (ArrayType) typePointer.T;
			retVal = arrayPointer.lowerLimit();
		} else if (typePointer.T instanceof TypeName) {
			// If it's another type name, keep searching
			TypeName typeNamePointer = (TypeName) typePointer.T;
			retVal = typeNamePointer.lowerLimit();
		}
		return retVal;
		
	}
}
