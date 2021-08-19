package com.java.parser.visitor;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class S1118_ConstructorVisitor<A> extends VoidVisitorAdapter<A> {
	
	
	@Override public void visit(final ConstructorDeclaration n, final A arg) {
		
		n.getModifiers();

		if (n.getJavaDoc() != null) {
			n.getJavaDoc().accept(this, arg);
		}
		if (n.getAnnotations() != null) {
			for (final AnnotationExpr a : n.getAnnotations()) {
				a.accept(this, arg);
			}
		}
		if (n.getTypeParameters() != null) {
			for (final TypeParameter t : n.getTypeParameters()) {
				t.accept(this, arg);
			}
		}
		if (n.getParameters() != null) {
			for (final Parameter p : n.getParameters()) {
				p.accept(this, arg);
			}
		}
		if (n.getThrows() != null) {
			for (final NameExpr name : n.getThrows()) {
				name.accept(this, arg);
			}
		}
		n.getBlock().accept(this, arg);
	}
	
	@Override public void visit(final ClassOrInterfaceDeclaration n, final A arg) {
		
		n.getModifiers();	  		
		n.getChildrenNodes();
		n.getParentNode();
		
		if (n.getJavaDoc() != null) {
			n.getJavaDoc().accept(this, arg);
		}
		if (n.getAnnotations() != null) {
			for (final AnnotationExpr a : n.getAnnotations()) {
				a.accept(this, arg);
			}
		}
		if (n.getTypeParameters() != null) {
			for (final TypeParameter t : n.getTypeParameters()) {
				t.accept(this, arg);
			}
		}
		if (n.getExtends() != null) {
			for (final ClassOrInterfaceType c : n.getExtends()) {
				c.accept(this, arg);
			}
		}

		if (n.getImplements() != null) {
			for (final ClassOrInterfaceType c : n.getImplements()) {
				c.accept(this, arg);
			}
		}
		if (n.getMembers() != null) {		
			ConstructorDeclaration constructor = null;
			for (final BodyDeclaration member : n.getMembers()) {				
				
					if(member instanceof MethodDeclaration) {					
						if((((MethodDeclaration) member).getModifiers() -1) == ModifierSet.STATIC) { 
							String clazzName = ((CompilationUnit) arg).getTypes().get(0).getName();
							String m = String.valueOf(ModifierSet.PUBLIC);
							if(!(n.toString().contains("public " +  clazzName))){
							
								// create constructor
								constructor = new ConstructorDeclaration();
								constructor.setName(((CompilationUnit) arg).getTypes().get(0).getName());
								constructor.setModifiers(ModifierSet.PRIVATE);
								// add  body to  constructor
								BlockStmt constructor_block = new BlockStmt();
								constructor.setBlock(constructor_block);
							}	
						}
					}
				
					else if(member instanceof ConstructorDeclaration) {
						if(((ConstructorDeclaration) member).getModifiers() == ModifierSet.PUBLIC) {
							((ConstructorDeclaration) member).setModifiers(ModifierSet.PRIVATE);
						}
					}
					member.accept(this, arg);
				
			}
			if(constructor != null) {
			//add constructor to AST tree as part of Compilation unit
			ASTHelper.addMember(((CompilationUnit) arg).getTypes().get(0), constructor);
			}
		}		
		
	}
	
	@Override public void visit(final FieldDeclaration n, final A arg) {
		
		n.getModifiers();
	
		if (n.getJavaDoc() != null) {
			n.getJavaDoc().accept(this, arg);
		}
		if (n.getAnnotations() != null) {
			for (final AnnotationExpr a : n.getAnnotations()) {
				a.accept(this, arg);
			}
		}
		n.getType().accept(this, arg);
		for (final VariableDeclarator var : n.getVariables()) {
			var.accept(this, arg);
		}
	}

	/*@Override public void visit(final MethodDeclaration n, final A arg) {
		
		String val = null;
		String res = null;
	
		n.getModifiers();
		
		if (n.getJavaDoc() != null) {
			n.getJavaDoc().accept(this, arg);
		}
		if (n.getAnnotations() != null) {
			for (final AnnotationExpr a : n.getAnnotations()) {
				a.accept(this, arg);
			}
		}
		if (n.getTypeParameters() != null) {
			for (final TypeParameter t : n.getTypeParameters()) {
				t.accept(this, arg);
			}
		}
		n.getType().accept(this, arg);
		if (n.getParameters() != null) {
			for (final Parameter p : n.getParameters()) {
				p.accept(this, arg);
			}
		}
		if (n.getThrows() != null) {
			for (final NameExpr name : n.getThrows()) {
				name.accept(this, arg);
			}
		}
		if (n.getBody() != null) {
			n.getBody().getBeginLine();
			n.getBody().getEndLine();
			String loggerName = ((CompilationUnit)arg).getTypes().get(0).getName();
			
			NameExpr expr = new NameExpr(n.getBeginLine()+1, n.getBody().getBeginColumn(), n.getEndLine()+1, 
					n.getBody().getEndColumn(), "Logger logger = Logger.getLogger("+ loggerName +".class.getName())");
			NameExpr expr1 = new NameExpr(n.getBeginLine()+2, n.getBody().getBeginColumn(), n.getBeginLine()+2, 
					n.getBody().getEndColumn(),	"logger.log(Level.INFO, \" a \")");
			
			
			
			List<Statement> stmts = n.getBody().getStmts();
			for(Statement s : stmts) {
				if(s instanceof ReturnStmt)
				val = s.toString();				
			}
			if(val != null) {
			if(val.contains("return")) {
				res = val.replace(";", "");
			}
			}
						
			ASTHelper.addStmt(n.getBody(), expr);
			ASTHelper.addStmt(n.getBody(), expr1);
			if(res != null) {
			NameExpr retExpr = new NameExpr(res);
			//ReturnStmt ret = new ReturnStmt(retExpr);
			ASTHelper.addStmt(n.getBody(), retExpr);
			}
			n.getBody().accept(this, arg);
		}
	}*/
	
	@Override public void visit(final ImportDeclaration n, final A arg) {
		
		n.getName().accept(this, arg);
	}
	
	private void visitComment(final Comment n, final A arg) {
		if (n != null) {
			n.accept(this, arg);
		}
	}

	@Override public void visit(final ExpressionStmt n, final A arg) {
		visitComment(n.getComment(), arg);
		n.getExpression();		
		n.getExpression().accept(this, arg);
	}
	
	@Override public void visit(final MethodCallExpr n, final A arg) {
		visitComment(n.getComment(), arg);			
		
		if (n.getScope() != null) {
			n.getScope().accept(this, arg);
		}
		if (n.getTypeArgs() != null) {
			for (final Type t : n.getTypeArgs()) {
				t.accept(this, arg);
			}
		}
		if (n.getArgs() != null) {
			for (final Expression e : n.getArgs()) {	
				
				e.accept(this, arg);
			}
		}
		
	}
	
	
	@Override public void visit(final BlockStmt n, final A arg) {
		List<Statement> list = new CopyOnWriteArrayList<Statement>();
		visitComment(n.getComment(), arg);
		
		if (n.getStmts() != null) {			
			
			list = n.getStmts();
			Iterator<Statement> it = list.iterator();
			while(it.hasNext()) {
				Statement s = it.next();	
				
				if(s.toString().contains("System.out")){					
					//int ind = list.indexOf(s);
					it.remove();					
				}
				else if(s instanceof ReturnStmt) {
					
					it.remove();
				}
				s.accept(this, arg);				
			}					
		}
	}
	
	@Override public void visit(final BinaryExpr n, final A arg) {
		visitComment(n.getComment(), arg);		
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
		
	}
	
	@Override public void visit(final ReturnStmt n, final A arg) {
		visitComment(n.getComment(), arg);
		
		if (n.getExpr() != null) {
			n.getExpr().accept(this, arg);
		}
	}
	
	/*@Override public void visit(final CompilationUnit n, final A arg) {
	
	visitComment(n.getComment(), arg);
	
	if (n.getPackage() != null) {
		List<ImportDeclaration> list = new ArrayList<>();
		ImportDeclaration imports = new ImportDeclaration();
		imports.setName(new NameExpr("java.util.logging.*"));
		list.add(imports);
		n.setImports(list);				
		n.getPackage().accept(this, arg);
	}
	if (n.getImports() != null) {
		for (final ImportDeclaration i : n.getImports()) {
			i.accept(this, arg);
		}
	}
	if (n.getTypes() != null) {
		for (final TypeDeclaration typeDeclaration : n.getTypes()) {
			typeDeclaration.accept(this, arg);
		}
	}	
	
}*/

}
