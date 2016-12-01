package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class TermOpr extends Operator {

	static TokenKind[] allPossibleTokens = { addToken, subtractToken, orToken };

	public TermOpr(TokenKind chosenToken, int n) {
		super(chosenToken, n);
	}

	public static TermOpr parse(Scanner S) {
		enterParser("TermOpr");
		TermOpr o = null;
		for (int x = 0; x < allPossibleTokens.length; x++) {
			if (S.curToken.kind == allPossibleTokens[x]) {
				o = new TermOpr(S.curToken.kind, S.curLineNum());
				S.readNextToken();
				leaveParser("TermOpr");
				return o;
			}
		}
		leaveParser("TermOpr");
		return o;
	}

	@Override
	public String identify() {
		return "<TermOpr> on line " + lineNum;
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
		if (chosenToken == addToken) {
			f.genInstr("", "addl", "%ecx,%eax", "");// Adds the value in ecx to the value in eax
		} else if (chosenToken == subtractToken) {
			f.genInstr("", "subl", "%ecx,%eax", "");// Adds the value in ecx to the value in eax
		} else {
			// orToken
			f.genInstr("", "orl", "%ecx,%eax", "");// Does an or check on eax and ecx
		}
	}
}
