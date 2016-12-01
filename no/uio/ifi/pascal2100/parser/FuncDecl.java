package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class FuncDecl extends ProcDecl {
    TypeName returnType;


    public FuncDecl(String id, int lNum, ParamDeclList params, TypeName returnType) {
    	super(id, lNum, params);
    	this.returnType = returnType;
    }

    @Override public String identify() {
    	if(lineNum == -1){
    		return "<func decl> in the library";
    	}
    	return "<func decl> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint("function " + name);
    	if(params != null) {
    		params.prettyPrint();
    	}
    	Main.log.prettyPrintLn(";"); 
    	returnType.prettyPrint();
    	Main.log.prettyPrintLn(";"); 
    	Main.log.prettyIndent();
    	block.prettyPrint();
    	Main.log.prettyPrintLn(";");
    	Main.log.prettyOutdent();
    }

    public static FuncDecl parse(Scanner s) {
    	enterParser("FuncDecl");
    	s.skip(functionToken);
    	s.test(nameToken);
    	String name = s.curToken.id;
    	s.readNextToken();

    	ParamDeclList paramList = null;
    	if(s.curToken.kind == leftParToken) {
	    paramList = ParamDeclList.parse(s);
    	}

    	s.skip(colonToken);
    	TypeName returnTypeName = TypeName.parse(s);
	
    	FuncDecl FD = new FuncDecl(name, s.curLineNum(), paramList, returnTypeName);
	s.skip(semicolonToken);
    	FD.block = Block.parse(s);//this comes after because its on another line
	
    	
    	s.skip(semicolonToken);
    	
	
    	leaveParser("FuncDecl");
    	return FD;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
    	if(params != null){
    		params.check(curScope, lib);
    	}
    	
    	returnType.check(curScope, lib);
    	declLevel = curScope.blockLevel;
    }

    @Override void checkWhetherAssignable(PascalSyntax where) {
	//Is assignable
    }

    @Override void checkWhetherFunction(PascalSyntax where) {
	//Is function
    }
    @Override void checkWhetherProcedure(PascalSyntax where) {
	where.error("Not a procedure.");
    }
    @Override void checkWhetherValue(PascalSyntax where) {
	//Is value(return value)
    }

    @Override public void genCode(CodeFile f) {
    	//Generates func- and proc-decls
    	progProcFuncName = "func$" + f.getLabel(name);
    	for(int i = 0; i < block.PDL.size(); i++) {
    		block.PDL.get(i).genCode(f);
    	}
    	
    	f.genInstr(progProcFuncName, "", "", "Start of function " + name);
    	block.genCode(f);
    	f.genInstr("", "movl", "-32(%ebp),%eax", "");
    	f.genInstr("", "leave", "", "");
    	f.genInstr("", "ret", "", "");
    }
}
