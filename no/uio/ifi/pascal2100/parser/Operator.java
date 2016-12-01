package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;

public abstract class Operator extends PascalSyntax {
	
    TokenKind chosenToken;
    
    public Operator(TokenKind chosenToken,int n) {
	super(n);
	this.chosenToken = chosenToken;
    }
    
    @Override
    public String identify() {
	return null;
    }
}
