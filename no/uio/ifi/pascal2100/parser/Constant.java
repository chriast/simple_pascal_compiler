package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.main.*;
import no.uio.ifi.pascal2100.scanner.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public abstract class Constant extends Factor {
	public int value;

	public Constant(int lineNum) {
		super(lineNum);
	}

	@Override
	public String identify() {
		return "<Constant> on line " + lineNum;
	}

	public static Constant parse(Scanner s) {
		enterParser("Constant");

		if (s.curToken.kind == nameToken) {
			Constant c = NamedConst.parse(s);
			leaveParser("Constant");
			return c;
		} else if (s.curToken.kind == intValToken) {
			Constant c = NumberLiteral.parse(s);
			leaveParser("Constant");
			return c;
		} else if (s.curToken.kind == stringValToken) {
			if (s.curToken.strVal.length() == 1) {
				Constant c = CharLiteral.parse(s);
				leaveParser("Constant");
				return c;
			} else {
				Constant c = StringLiteral.parse(s);
				leaveParser("Constant");
				return c;
			}
		} else {
			Main.error(s.curLineNum(), "Invalid constant. found: "
					+ s.curToken.kind);
		}
		return null;
	}
	
	public abstract int getVal(Block curScope);
	public abstract int getVal2();
}
