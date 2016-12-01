//Copied from the lecture slides, code by Dag Langmyhr

package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class Program extends PascalDecl {

	Program(String id, int lNum) {
		super(id, lNum);
	}

	Block progBlock;

	@Override
	public String identify() {
		return "<Program> " + name + " on line " + lineNum;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyPrint("program ");
		Main.log.prettyPrint(name);
		Main.log.prettyPrintLn(" ;");
		Main.log.prettyIndent();
		progBlock.prettyPrint();
		Main.log.prettyPrintLn(".");
		Main.log.prettyOutdent();
	}

	public static Program parse(Scanner s) {
		enterParser("Program");// logging purposes
		s.skip(programToken);// tests to see if the current token is actually
								// program, hits the scanner
		s.test(nameToken);// checks again, but this time checks to see if the
							// next token is the expected token (name)

		Program p = new Program(s.curToken.id, s.curLineNum());// the static
																// method
																// creates an
																// object of the
																// class it is
																// in, using the
																// constructor,
																// and using the
																// nameToken as
																// the id of the
																// program token
		s.readNextToken();// read token
		s.skip(semicolonToken);// test if read token is semicolon
		p.progBlock = Block.parse(s);// goes to the next branch in the tree and
										// tries to create it.
		p.progBlock.context = p;// an upward link to the parent node in Block
								// class TODO
		s.skip(dotToken);// checks for the dot at the end of the Program

		leaveParser("program");// logging purposes
		return p;// goes up and returns to the doTestParser method and completes
					// it and completes the compiler

	}

	@Override
	public void check(Block curScope, Library lib) {
		progBlock.check(curScope, lib);
		declLevel = curScope.blockLevel;
	}

	@Override
	void checkWhetherAssignable(PascalSyntax where) {
		where.error("You cannot assign to a program.");
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
		where.error("Not a value.");
	}

	@Override
	public void genCode(CodeFile f) {
		progProcFuncName = "prog$" + f.getLabel(name);
		f.genInstr("", ".globl", "_main", "");
		f.genInstr("", ".globl", "main", "");

		f.genInstr("_main", "", "", "");
		f.genInstr("main", "", "", "");
		f.genInstr("", "call", progProcFuncName, "");
		f.genInstr("", "movl", "$0,%eax", "");
		f.genInstr("", "ret", "", "");
		// Generates func- and proc-decls
		for (int i = 0; i < progBlock.PDL.size(); i++) {
			progBlock.PDL.get(i).genCode(f);
		}
		f.genInstr(progProcFuncName, "", "", "#Start of program " + name);
		progBlock.genCode(f);
		f.genInstr("", "leave", "", "#End of program " + name);
		f.genInstr("", "ret", "", "");
	}
}
