package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;
import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;


public class FuncCall extends Factor {
    String name;
    PascalDecl declPointer;
    ArrayList<Expression> exp;
    
    public FuncCall(int n,String id) {
    	super(n);
    	this.name = id;
    }
    
    @Override
    public String identify() {
    	return "<FuncCall> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	Main.log.prettyPrint(name);
    	if(exp.size() > 0) {
    		Main.log.prettyPrint("(");
    		exp.get(0).prettyPrint();
    		for(int i = 0; i < exp.size(); i++) {
    			Main.log.prettyPrint(", ");
    			exp.get(i).prettyPrint();
    		}
    		Main.log.prettyPrint(")");
    	}
    }
    
    public static FuncCall parse(Scanner s){
    	enterParser("FuncCall");
    	s.test(nameToken);
    	FuncCall FC = new FuncCall(s.curLineNum(), s.curToken.id);
    	s.readNextToken();
    	if(s.curToken.kind == leftParToken){
    		FC.exp = new ArrayList<Expression>();
    		s.readNextToken();
    		Expression tmpExp = Expression.parse(s);
    		FC.exp.add(tmpExp);
    		while(s.curToken.kind == commaToken){
    			s.readNextToken();
    			tmpExp = Expression.parse(s);
    			FC.exp.add(tmpExp);
    		}
    		s.skip(rightParToken);
    	}
	
    	leaveParser("funcCall");
    	return FC;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
        declPointer = curScope.findDecl(name.toLowerCase(), this,false);
        FuncDecl tmpFDecl = (FuncDecl) declPointer;
        String tmpName2 = name.toLowerCase();
        if(tmpName2.contains("write")){
        	
        }
        else if(exp == null && tmpFDecl.params == null){
			
		}
		else if(exp == null && tmpFDecl.params != null){
			error("The funcCall " + name + " on line: " + lineNum + " is expecting a parameter!");
		}
		else if(exp != null && tmpFDecl.params == null){
			error("The funcCall " + name + " on line: " + lineNum + " has parameters where non are expected!");
		}
		else{
			if(exp.size() != tmpFDecl.params.paramList.size()){
				error("The nummber of parameters for funcCall " + name + " on line: " + lineNum + "are unequal to the number of parameters in the declared function on line: " + declPointer.lineNum);
			}
		}
        declPointer.checkWhetherValue(this);
        declPointer.checkWhetherFunction(this);
        
        if(exp != null){
        	for(int i = 0;i<exp.size();i++){
            	exp.get(i).check(curScope, lib);
            }
        }
        
        
    }
    
    @Override public void genCode(CodeFile f) {
	//Adding the arguments into the stack backwards so that the first ends up on top
    	
    	if(exp == null){//hacky workaround needed in order for null params to be detected in del 3
			exp = new ArrayList<Expression>();
		}
    	
    	int i = exp.size();
    	while(i > 0) {
    		exp.get(--i).genCode(f);
    		f.genInstr("", "pushl", "%eax", "");
    	}

    	f.genInstr("", "call", declPointer.progProcFuncName, "");
    	f.genInstr("", "addl", "$"+(exp.size()*4)+",%esp", "");//Removes the arguments afterwards
    }
}
