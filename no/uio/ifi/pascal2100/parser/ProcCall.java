package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;
import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ProcCall extends Statement {
	String name;
	ArrayList<Expression> exp; 
	PascalDecl declPointer;

	public ProcCall(int n, String id) {
		super(n);
		this.name = id;
	}

	@Override
	public String identify() {
		return "<ProcCall> on line " + lineNum;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyPrint(name);
		if (exp.size() > 0) {
			Main.log.prettyPrint("(");
			exp.get(0).prettyPrint();
			for (int i = 1; i < exp.size(); i++) {
				Main.log.prettyPrint(", ");
				exp.get(i).prettyPrint();
			}
			Main.log.prettyPrint(")");
		}
	}

	public static ProcCall parse(Scanner s) {
		enterParser("ProcCall");
		s.test(nameToken);
		ProcCall FC = new ProcCall(s.curLineNum(), s.curToken.id);
		s.readNextToken();
		if (s.curToken.kind == leftParToken) {
			FC.exp = new ArrayList<Expression>();
			s.readNextToken();
			Expression tmpExp = Expression.parse(s);
			FC.exp.add(tmpExp);
			while (s.curToken.kind == commaToken) {
				s.readNextToken();
				tmpExp = Expression.parse(s);
				FC.exp.add(tmpExp);
			}
			s.skip(rightParToken);
		}

		leaveParser("ProcCall");
		return FC;

	}

	@Override
	public void check(Block curScope, Library lib) {
		declPointer = curScope.findDecl(name.toLowerCase(), this, false);
		declPointer.checkWhetherProcedure(this);
		ProcDecl tmpPDecl = (ProcDecl) declPointer;
		String tmpName2 = name.toLowerCase();
        if(tmpName2.contains("write")){
        	
        }
		else if(exp == null && tmpPDecl.params == null){
			
		}
		else if(exp == null && tmpPDecl.params != null){
			error("The procCall " + name + " on line: " + lineNum + " is expecting a parameter!");
		}
		else if(exp != null && tmpPDecl.params == null){
			error("The procCall " + name + " on line: " + lineNum + " has parameters where non are expected!");
		}
		else{
			if(exp.size() != tmpPDecl.params.paramList.size()){
				error("The nummber of parameters for procCall " + name + " on line: " + lineNum + "are unequal to the number of parameters in the declared procedure on line: " + declPointer.lineNum);
			}
		}
		if(exp != null){
			for (int i = 0; i < exp.size(); i++) {			
				exp.get(i).check(curScope, lib);
			}
		}
		
	}

	@Override
	public void genCode(CodeFile f) {
		if (declPointer.name.equals("write")) {
			// If it's a write call
			Expression ePointer;
			Factor fPointer;
			// Goes through the arguments one by one
			for (int i = 0; i < exp.size(); i++) {
				ePointer = exp.get(i);
				fPointer = exp.get(i).SE1.TL.get(0).FL.get(0);
				if (fPointer instanceof StringLiteral) {
					// String literal
					Constant constPointer = (Constant) fPointer;
					genCodeStr(f, constPointer);
				} else {
					ePointer.genCode(f);
					fPointer = exp.get(i).SE1.TL.get(0).FL.get(0);

					if (fPointer instanceof CharLiteral) {
						// Char literal
						genCodeChar(f);
					} else if (fPointer instanceof NamedConst) {
						// Named constant
						NamedConst namePointer = (NamedConst) fPointer;
						ConstDecl constDeclPointer = (ConstDecl) namePointer.declPointer;
						genCodeName(f, constDeclPointer.c);
					} else if (fPointer instanceof Variable) {
						// Variable
						Variable varPointer = (Variable) fPointer;
						genCodeVar(f, varPointer);
					}else {
						// Number literal, or true(1)/false(0)
						genCodeNum(f);	
					}
				}

			}

			return;
		}
		// Adding the arguments into the stack backwards so that the first ends
		// up on top
		
		if(exp == null){//hacky workaround needed in order for null params to be detected in del 3
			exp = new ArrayList<Expression>();
		}
		
		int i = exp.size();
		while (i > 0) {
			exp.get(--i).genCode(f);
			f.genInstr("", "pushl", "%eax", "");
		}

		f.genInstr("", "call", declPointer.progProcFuncName, "");
		f.genInstr("", "addl", "$" + (exp.size() * 4) + ",%esp", "");// Removes the argument afterwards
																		
	}

	private void genCodeName(CodeFile f, Constant constPointer) {
		if (constPointer instanceof CharLiteral) {
			genCodeChar(f);
		} else if (constPointer instanceof NumberLiteral) {
			genCodeNum(f);
		} else if (constPointer instanceof NamedConst) {
			NamedConst namePointer = (NamedConst) constPointer;
			ConstDecl constDeclPointer = (ConstDecl) namePointer.declPointer;
			genCodeName(f, constDeclPointer.c);
		} else if (constPointer instanceof StringLiteral) {
			genCodeStr(f, constPointer);
		}
	}

	private void genCodeChar(CodeFile f) {
		f.genInstr("", "pushl", "%eax", "");
		f.genInstr("", "call", "write_char", "");
		f.genInstr("", "addl", "$4,%esp", "");
	}

	private void genCodeNum(CodeFile f) {
		f.genInstr("", "pushl", "%eax", "");
		f.genInstr("", "call", "write_int", "");
		f.genInstr("", "addl", "$4,%esp", "");
	}

	private void genCodeStr(CodeFile f, Constant constPointer) {
		String testLabel = f.getLocalLabel();
		StringLiteral stringPointer = (StringLiteral) constPointer;

		f.genInstr("", ".data", "", "");
		f.genInstr(testLabel, ".asciz", "\"" + stringPointer.value + "\"", "");// TODO
		f.genInstr("", ".align", "2", "");
		f.genInstr("", ".text", "", "");
		f.genInstr("", "leal", testLabel + ",%eax", "");
		f.genInstr("", "pushl", "%eax", "");
		f.genInstr("", "call", "write_string", "");
		f.genInstr("", "addl", "$4,%esp", "");
	}

	private void genCodeVar(CodeFile f, Variable varPointer) {
		// Writes the value
		if (varPointer.isChar()) {
			genCodeChar(f);
		} else {
			genCodeNum(f);
		}
		/*
		 * varPointer.genCode(f); f.genInstr("", "pushl", "%eax", "");
		 * f.genInstr("", "call", "write_int", ""); f.genInstr("", "addl",
		 * "$4,%esp", "");
		 */
	}
}
