package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class FactorOpr extends Operator {

    static TokenKind[] allPossibleTokens = {multiplyToken,divToken,modToken,andToken};
    
    public FactorOpr(TokenKind chosenToken, int n) {
    	super(chosenToken, n);
    }
    
    public static FactorOpr parse(Scanner S){
	enterParser("FactorOpr");
	FactorOpr o = null;
	for(int x = 0;x < allPossibleTokens.length;x++){
	    if(S.curToken.kind == allPossibleTokens[x]){
		o = new FactorOpr(S.curToken.kind,S.curLineNum());
		S.readNextToken();
		leaveParser("factorOpr");
		return o;
	    }	
	    
	}
	leaveParser("factorOpr");
	return o;
    }
    
    @Override
    public String identify() {
    	return "<FactorOpr> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	Main.log.prettyPrint(" " + chosenToken.toString() + " ");
    }
    
    @Override
    public void check(Block curScope, Library lib) {	
	
    }
    
    @Override public void genCode(CodeFile f) {
	if(chosenToken == multiplyToken) {
	    f.genInstr("", "imull", "%ecx,%eax", "");//Multiplies the value in eax by the value in ecx
	}else if(chosenToken == divToken) {
	    f.genInstr("", "cdq", "", "");//Required by idivl
	    f.genInstr("", "idivl", "%ecx", "");//Divides the value in eax with the value in ecx
	}else if(chosenToken == modToken) {
	    f.genInstr("", "cdq", "", "");//Required by idivl
	    f.genInstr("", "idivl", "%ecx", "");//Divides the value in eax with the value in ecx
	     f.genInstr("", "movl", "%edx,%eax", "");//Moves the remainder of idivl from edx to eax
	}else {
	    //andToken
	    f.genInstr("", "andl", "%ecx,%eax", "");//Adds the value in ecx to the value in eax
	}
    }
}
