package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ProcDecl extends PascalDecl {
	ParamDeclList params;
	Block block;

	public ProcDecl(String id, int lNum, ParamDeclList params) {
		super(id, lNum);
		this.params = params;
	}

	@Override
	public String identify() {
		if (lineNum == -1) {
			return "<proc decl> in the library";
		}
		return "<proc decl> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint("procedure " + name);
		if (params != null) {
			params.prettyPrint();
		}
		Main.log.prettyPrintLn(";");
		Main.log.prettyIndent();
		block.prettyPrint();
		Main.log.prettyPrintLn(";");
		Main.log.prettyOutdent();
	}

	public static ProcDecl parse(Scanner s) {
		enterParser("ProcDecl");

		s.skip(procedureToken);
		s.test(nameToken);
		String name = s.curToken.id;
		s.readNextToken();
		ParamDeclList paramList = null;

		if (s.curToken.kind == leftParToken) {
			paramList = ParamDeclList.parse(s);
		}

		ProcDecl PD = new ProcDecl(name, s.curLineNum(), paramList);
		s.skip(semicolonToken);
		PD.block = Block.parse(s);

		s.skip(semicolonToken);

		leaveParser("ProcDecl");
		return PD;
	}

	@Override
	public void check(Block curScope, Library lib) {
		if (params != null) {
			params.check(curScope, lib);
		}
		declLevel = curScope.blockLevel;
	}

	@Override
	void checkWhetherAssignable(PascalSyntax where) {
		where.error("Cannot assign to a procedure");
	}

	@Override
	void checkWhetherFunction(PascalSyntax where) {
		where.error("Not a function.");
	}

	@Override
	void checkWhetherProcedure(PascalSyntax where) {
		// Is procedure
	}

	@Override
	void checkWhetherValue(PascalSyntax where) {
		where.error("Not a value.");
	}

	@Override
	public void genCode(CodeFile f) {
		progProcFuncName = "proc$" + f.getLabel(name);
		// Generates func- and proc-decls
		for (int i = 0; i < block.PDL.size(); i++) {
			block.PDL.get(i).genCode(f);
		}

		f.genInstr(progProcFuncName, "", "", "Start of procedure " + name);

		block.genCode(f);
		f.genInstr("", "leave", "", "");
		f.genInstr("", "ret", "", "");
	}
}
