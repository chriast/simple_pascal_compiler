package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;
import no.uio.ifi.pascal2100.main.*;

public class Library extends Block {

	public Library() {
		super(0);

		// eol
		CharLiteral e = new CharLiteral(-1, (char) 10);// 10 = ascii value of
														// eol
		ConstDecl eo = new ConstDecl("eol", -1, e);
		this.CDP = new ConstDeclPart(-1);
		this.CDP.constList = new ArrayList<ConstDecl>();
		this.CDP.constList.add(eo);
		this.decls.put("eol", eo);

		this.TDP = new TypeDeclPart(-1);// for boolean, char, and integer

		// Boolean
		EnumLiteral f = new EnumLiteral("false", -1);
		f.enumNumber = 0;
		EnumLiteral t = new EnumLiteral("true", -1);
		t.enumNumber = 1;
		EnumType b = new EnumType(-1);
		b.enumList.add(f);
		b.enumList.add(t);
		TypeName type4 = new TypeName(-1);
		type4.name = "boolean";
		TypeDecl bool = new TypeDecl(type4.name, -1, type4);
		bool.T = b;
		decls.put("boolean", bool);
		decls.put("true", t);
		decls.put("false", f);
		this.TDP.typeList.add(bool);

		// char
		CharLiteral c0 = new CharLiteral(-1, (char) 0);
		CharLiteral c255 = new CharLiteral(-1, (char) 255);
		RangeType c = new RangeType(-1, c0, c255);
		TypeName type2 = new TypeName(-1);
		type2.name = "char";
		TypeDecl chara = new TypeDecl(type2.name, -1, type2);
		chara.T = c;// lol, karate
		decls.put("char", chara);
		this.TDP.typeList.add(chara);

		// integer
		NumberLiteral intMin = new NumberLiteral(-1, Integer.MIN_VALUE);
		NumberLiteral intMax = new NumberLiteral(-1, Integer.MAX_VALUE);
		RangeType i = new RangeType(-1, intMin, intMax);
		TypeName type3 = new TypeName(-1);
		type3.name = "integer";
		TypeDecl in = new TypeDecl("integer", -1, type3);
		in.T = i;
		decls.put("integer", in);
		this.TDP.typeList.add(in);

		// write, could be wrong
		ProcDecl write = new ProcDecl("write", -1, null);
		decls.put("write", write);
		this.PDL.add(write);
	}

	@Override
	public void check(Block curScope, Library lib) {
		System.out
				.println("check called in Library. This should never happen!");
		System.exit(0);
	}

	@Override
	public void genCode(CodeFile f) {
		f.genInstr("", ".extern", "write_char", "");
		f.genInstr("", ".extern", "write_int", "");
		f.genInstr("", ".extern", "write_string", "");
	}
}
