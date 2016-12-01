package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ConstDecl extends PascalDecl {

	public Constant c;

	// public ConstDecl next = null; //link to next const declaration

	public ConstDecl(String id, int lNum, Constant c) {
		super(id, lNum);
		this.c = c;
	}

	@Override
	public String identify() {
		if (lineNum == -1) {
			return "<const decl> in the library";
		}
		return "<const decl> on line " + lineNum;
	}

	@Override
	void prettyPrint() {

		Main.log.prettyPrint(name);
		Main.log.prettyPrint(" = ");
		c.prettyPrint();
		Main.log.prettyPrintLn(";");

	}

	public static ConstDecl parse(Scanner s) {
		enterParser("ConstDecl");
		s.test(nameToken);
		String name2 = s.curToken.id;
		s.readNextToken();// go to next token which should be equalToken
		s.skip(equalToken);
		Constant x = Constant.parse(s);
		ConstDecl CD = new ConstDecl(name2, s.curLineNum(), x);
		s.skip(semicolonToken);// finish

		leaveParser("ConstDecl");
		return CD;
	}

	@Override
	public void check(Block curScope, Library lib) {
		if (this.c instanceof NamedConst) {
			this.check(curScope, lib);
		}
		declLevel = curScope.blockLevel;
	}

	@Override
	void checkWhetherAssignable(PascalSyntax where) {
		where.error("You cannot assign to a constant.");
	}

	@Override
	void checkWhetherFunction(PascalSyntax where) {
		where.error("Not a function.");
	}

	@Override
	void checkWhetherProcedure(PascalSyntax where) {
		where.error("Not a procedure.");
	}

	@Override
	void checkWhetherValue(PascalSyntax where) {
		// Is value
	}

	@Override
	public void genCode(CodeFile f) {
		c.genCode(f);
	}
}
