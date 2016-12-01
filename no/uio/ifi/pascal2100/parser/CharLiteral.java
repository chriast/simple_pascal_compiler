package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class CharLiteral extends Constant {

	public CharLiteral(int lNum, char value) {
		super(lNum);
		this.value = value;
	}

	@Override
	public String identify() {
		return "<CharLiteral> " + value + " on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint("'" + Character.toString((char) value) + "'");
	}

	public static CharLiteral parse(Scanner s) {
		enterParser("CharLiteral");
		char charValue = s.curToken.strVal.charAt(0);
		CharLiteral CL = new CharLiteral(s.curLineNum(), charValue);
		s.readNextToken();
		leaveParser("CharLiteral");
		return CL;
	}
	@Override
	public int getVal(Block curScope) {
		return (int)value;
	}
	@Override
	public int getVal2(){
		return (int)value;
	}
	
	@Override
	public void check(Block curScope, Library lib) {

	}

	@Override
	public void genCode(CodeFile f) {
		f.genInstr("", "movl", "$" + (int) value + ",%eax", "");
	}
}
