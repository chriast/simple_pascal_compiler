package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class RelOpr extends Operator {

	static TokenKind[] allPossibleTokens = { equalToken, notEqualToken,
			lessToken, lessEqualToken, greaterToken, greaterEqualToken };

	public RelOpr(TokenKind chosenToken, int n) {
		super(chosenToken, n);
	}

	public static RelOpr parse(Scanner S) {
		enterParser("relOpr");
		RelOpr o = null;
		for (int x = 0; x < allPossibleTokens.length; x++) {
			if (S.curToken.kind == allPossibleTokens[x]) {
				o = new RelOpr(S.curToken.kind, S.curLineNum());
				S.readNextToken();
				leaveParser("RelOpr");
				return o;
			}

		}
		leaveParser("RelOpr");
		return null;
	}

	@Override
	public String identify() {
		return "<RelOpr> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint(" " + chosenToken.toString() + " ");
	}

	@Override
	public void check(Block curScope, Library lib) {

	}

	@Override
	public void genCode(CodeFile f) {
		if (chosenToken == equalToken) {
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "sete", "%al", "");// Checks if equal
		} else if (chosenToken == notEqualToken) {
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "setne", "%al", "");// Checks if not equal
		} else if (chosenToken == lessToken) {
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "setl", "%al", "");// Checks if less than
		} else if (chosenToken == lessEqualToken) {
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "setle", "%al", "");// Checks if less or equal
		} else if (chosenToken == greaterToken) {
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "setg", "%al", "");// Checks if greater than
		} else {
			// greaterEqualToken
			f.genInstr("", "cmpl", "%eax,%ecx", "");// Compares eax and ecx
			f.genInstr("", "movl", "$0,%eax", "");
			f.genInstr("", "setge", "%al", "");// Checks if greater or equal
		}
	}
}
