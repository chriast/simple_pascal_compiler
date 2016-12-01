package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class PrefixOpr extends Operator {

	static TokenKind[] allPossibleTokens = { addToken, subtractToken };

	public PrefixOpr(TokenKind chosenToken, int n) {
		super(chosenToken, n);
	}

	public static PrefixOpr parse(Scanner S) {
		enterParser("PrefixOpr");
		PrefixOpr o = null;
		for (int x = 0; x < allPossibleTokens.length; x++) {
			if (S.curToken.kind == allPossibleTokens[x]) {
				o = new PrefixOpr(S.curToken.kind, S.curLineNum());
				S.readNextToken();
				leaveParser("PrefixOpr");
				return o;
			}
		}
		leaveParser("PrefixOpr");
		return o;
	}

	@Override
	public String identify() {
		return "<PrefixOpr> on line " + lineNum;
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
		if (chosenToken == subtractToken) {
			// Negative prefix
			f.genInstr("", "negl", "%eax", "");
		}
		// Positive prefix -> nothing happens, "yup" -chris
	}
}
