package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class AssignStatm extends Statement {
	Variable V;
	Expression E;
	int varType;

	public AssignStatm(int n) {
		super(n);
	}

	public static AssignStatm parse(Scanner S) {
		enterParser("AssignStatm");
		AssignStatm AS = new AssignStatm(S.curLineNum());
		AS.V = Variable.parse(S);
		S.skip(assignToken);
		AS.E = Expression.parse(S);
		leaveParser("AssignStatm");
		return AS;
	}

	@Override
	public String identify() {
		return "<AssignStatm> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		V.prettyPrint();
		Main.log.prettyPrint(" := ");
		E.prettyPrint();
	}

	@Override
	public void check(Block curScope, Library lib) {
		V.check(curScope, lib);
		curScope.findDecl(V.name.toLowerCase(), V, true)
				.checkWhetherAssignable(this);
		E.check(curScope, lib);

		if (curScope.findDecl(V.name.toLowerCase(), V, true) instanceof VarDecl) {
			// Normal variable
			varType = 1;
		} else if (curScope.findDecl(V.name.toLowerCase(), V, true) instanceof FuncDecl) {
			// Function
			varType = 2;
		} else {
			// Array-element
			varType = 3;
		}
	}

	@Override
	public void genCode(CodeFile f) {
		E.genCode(f);

		if (varType == 1) {
			// Normal variable
			f.genInstr("", "movl", ((-4) * V.declPointer.declLevel) + "(%ebp),%edx", "");
			f.genInstr("", "movl", "%eax," + V.declPointer.offset + "(%edx)",
					"");
		} else if (varType == 2) {
			f.genInstr("", "movl", "%eax,-32(%ebp)", "");
			// Function
		} else if (varType == 3) {
			// Array-element
			
			//Get the lower index limit of the array
			VarDecl varDeclPointer = (VarDecl) V.declPointer;
			ArrayType aType = (ArrayType) varDeclPointer.T;
			int lowVal = aType.lowerLimit();
			
			f.genInstr("", "pushl", "%eax", "");
			V.E.genCode(f);
			f.genInstr("", "subl", "$"+lowVal+",%eax", "");
			f.genInstr("", "movl", ((-4) * V.declPointer.declLevel) + "(%ebp),%edx", "");
			f.genInstr("", "leal", V.declPointer.offset + "(%edx),%edx", "");
			f.genInstr("", "popl", "%ecx", "");
			f.genInstr("", "movl", "%ecx,(%edx,%eax,4)", "");
		}
	}
}
