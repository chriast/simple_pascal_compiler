package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class Variable extends Factor {
	String name;
	Expression E;
	int blockLevel;
	PascalDecl declPointer;
	EnumLiteral enumPointer;// Used in case of enum literal

	public Variable(String name, int n) {
		super(n);
		this.name = name;
	}

	@Override
	public String identify() {
		return "<Variable> " + name + " on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint(name);
		if (E != null) {
			Main.log.prettyPrint("[");
			E.prettyPrint();
			Main.log.prettyPrint("]");
		}
	}

	public static Variable parse(Scanner S) {
		enterParser("Variable");
		Variable v = new Variable(S.curToken.id, S.curLineNum());
		S.readNextToken();
		if (S.curToken.kind == leftBracketToken) {
			S.readNextToken();
			v.E = Expression.parse(S);
			S.skip(rightBracketToken);
		}
		leaveParser("Variable");
		return v;
	}

	@Override
	public void check(Block curScope, Library lib) {
		declPointer = curScope.findDecl(name.toLowerCase(), this, false);
		if (declPointer instanceof EnumLiteral) {
			declPointer.check(curScope, lib);
			enumPointer = (EnumLiteral) declPointer;
		} else if (declPointer instanceof ParamDecl) {
			declPointer = (ParamDecl) declPointer;
		}
		declPointer.checkWhetherValue(this);
		blockLevel = curScope.blockLevel;

		if (E != null) {
			E.check(curScope, lib);
		}
	}

	@Override
	public void genCode(CodeFile f) {
		if (declPointer instanceof EnumLiteral) {
			// It's an enum
			f.genInstr("", "movl", "$" + enumPointer.enumNumber + ",%eax",
					"#Enum value");
			return;// Nothing else to be done
		}
		// The variable is declared as a variable
		if (E != null) {
			// It's an array
			// Getting the lowest index limit of the array
			VarDecl varDeclPointer = (VarDecl) declPointer;
			ArrayType arrayPointer = (ArrayType) varDeclPointer.T;
			int lowVal = arrayPointer.lowerLimit();//This is as far as we have managed to get with regards to making arrays work. We really did our best.
			
			E.genCode(f);
			f.genInstr("", "subl", "$"+lowVal+",%eax", "");
		}
		f.genInstr("", "movl", ((-4) * declPointer.declLevel) + "(%ebp),%edx", "");
		if (E != null) {
			f.genInstr("", "leal", + declPointer.offset + "(%edx),%edx", "");
			f.genInstr("", "movl", "(%edx,%eax,4),%eax", "");
		} else {
			f.genInstr("", "movl", declPointer.offset + "(%edx),%eax", "");
		}
	}

	// Checks if the value the variable is holding is of type char
	public Boolean isChar() {
		VarDecl varDeclPointer;
		ParamDecl parDeclPointer;
		if (declPointer instanceof VarDecl) {
			varDeclPointer = (VarDecl) declPointer;
			if (varDeclPointer.T instanceof TypeName) {
				TypeName tName = (TypeName) varDeclPointer.T;
				if (tName.name.equals("char")) {
					return true;
				}
			}
		} else if (declPointer instanceof ParamDecl) {
			parDeclPointer = (ParamDecl) declPointer;
			if (parDeclPointer.ptype.name.equals("char")) {
				return true;
			}
		}
		return false;
	}
}
