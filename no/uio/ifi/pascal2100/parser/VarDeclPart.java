package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class VarDeclPart extends PascalSyntax {
	ArrayList<VarDecl> varList = new ArrayList<VarDecl>();

	public VarDeclPart(int lineNum) {
		super(lineNum);
	}

	@Override
	public String identify() {
		return "<VarDeclPart> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrintLn("var");
		Main.log.prettyIndent();
		for (int i = 0; i < varList.size(); i++) {
			varList.get(i).prettyPrint();
		}
		Main.log.prettyOutdent();
	}

	public static VarDeclPart parse(Scanner s) {
		enterParser("VarDeclPart");

		VarDeclPart VDP = new VarDeclPart(s.curLineNum());
		s.skip(varToken);

		while (s.curToken.kind == nameToken && s.nextToken.kind == colonToken) {
			VarDecl otherItem = VarDecl.parse(s);
			VDP.varList.add(otherItem);
		}

		leaveParser("VarDeclPart");
		return VDP;
	}

	@Override
	public void check(Block curScope, Library lib) {
		for (int i = 0; i < varList.size(); i++) {
			VarDecl tmp = varList.get(i);
			tmp.check(curScope, lib);
		}
	}

	@Override
	public void genCode(CodeFile f) {

	}
}
