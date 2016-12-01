//This is taken from the lecture
package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

/* <while-statm> ::= ’while’ <expression> ’do’ <statement> */
class WhileStatm extends Statement {

	public Expression expr;
	public Statement body;

	WhileStatm(int lNum) {
		super(lNum);
	}

	@Override
	public String identify() {
		return "<WhileStatm> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint("while ");
		expr.prettyPrint();
		Main.log.prettyPrintLn(" do");
		Main.log.prettyIndent();
		body.prettyPrint();
		Main.log.prettyOutdent();
	}

	public static WhileStatm parse(Scanner s) {
		enterParser("while-statm");
		WhileStatm ws = new WhileStatm(s.curLineNum());
		s.skip(whileToken);
		ws.expr = Expression.parse(s);
		s.skip(doToken);// brings the scanner curToken to the first token to be read by "body"
		ws.body = Statement.parse(s);// will be at first token after the end of "body"
		leaveParser("while-statm");
		return ws;
	}

	@Override
	public void check(Block curScope, Library lib) {
		expr.check(curScope, lib);
		body.check(curScope, lib);
	}

	@Override
	public void genCode(CodeFile f) {
		String testLabel = f.getLocalLabel();
		String endLabel = f.getLocalLabel();

		f.genInstr(testLabel, "", "", "#Start while-statement");
		expr.genCode(f);
		f.genInstr("", "cmpl", "$0,%eax", "");
		f.genInstr("", "je", endLabel, "");
		body.genCode(f);
		f.genInstr("", "jmp", testLabel, "");
		f.genInstr(endLabel, "", "", "#End while-statement");
	}
}
