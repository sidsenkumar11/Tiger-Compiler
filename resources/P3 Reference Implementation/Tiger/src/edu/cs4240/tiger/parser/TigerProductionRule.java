package edu.cs4240.tiger.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.cs4240.tiger.util.Utils;

/**
 * @author Roi Atalla
 */
public enum TigerProductionRule implements TigerSymbol {
	PROGRAM,
	DECLSEG,
	TYPEDECLS,
	TYPEDECL,
	TYPE,
	VARDECLS,
	VARDECL,
	IDS,
	IDS_TAIL,
	OPTINIT,
	FUNCDECLS,
	FUNCDECL,
	PARAMS,
	NEPARAMS,
	NEPARAMS_TAIL,
	PARAM,
	OPTRETTYPE,
	STMTS,
	STMTS_TAIL,
	FULLSTMT,
	STMT,
	STMT_TAIL,
	LVALUE,
	OPTOFFSET,
	OPTSTORE,
	NUMEXPRS,
	NEEXPRS,
	NEEXPRS_TAIL,
	BOOLEXPR,
	BOOLEXPR_TAIL,
	CLAUSE,
	CLAUSE_TAIL,
	PRED,
	BOOLOP,
	NUMEXPR,
	NUMEXPR_TAIL,
	LINOP,
	TERM,
	TERM_TAIL,
	NONLINOP,
	FACTOR,
	FACTOR_TAIL,
	CONST;
	
	public final List<List<TigerSymbol>> productions = new ArrayList<>();
	
	public static String printRule(TigerProductionRule rule, List<TigerSymbol> symbols) {
		char arrow = '→';
		char eps = 'ϵ';
		
		String s = rule.toString().toLowerCase() + " " + arrow;
		
		if(symbols.isEmpty()) {
			s += " " + eps;
		} else {
			for(TigerSymbol c : symbols) {
				if(c instanceof TigerTokenClass && Utils.specialTokenClassesToString.containsKey(c)) {
					s += " " + Utils.specialTokenClassesToString.get(c);
				} else {
					s += " " + c.toString().toLowerCase();
				}
			}
		}
		
		return s;
	}
	
	static {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(TigerProductionRule.class.getResourceAsStream("ProductionRules.txt"), "UTF-8"));
			
			char arrow = '→';
			
			String s;
			while((s = reader.readLine()) != null) {
				int arrowIdx = s.indexOf(arrow);
				if(arrowIdx == -1) {
					throw new RuntimeException("No arrow found: " + s);
				}
				
				String productionName = s.substring(0, arrowIdx).trim().toUpperCase();
				
				TigerProductionRule productionRule;
				try {
					productionRule = TigerProductionRule.valueOf(productionName);
				}
				catch(Exception exc) {
					throw new RuntimeException("Unrecognized production: " + productionName);
				}
				
				ArrayList<TigerSymbol> production = new ArrayList<>();
				
				String[] symbols = s.substring(arrowIdx + 1).trim().split(" ");
				for(String symbol : symbols) {
					symbol = symbol.trim();
					if(symbol.isEmpty()) {
						continue;
					}
					
					TigerTokenClass specialChar = Utils.specialTokenStringToClasses.get(symbol);
					if(specialChar != null) {
						production.add(specialChar);
						continue;
					}
					
					symbol = symbol.toUpperCase();
					
					try {
						TigerProductionRule rule = TigerProductionRule.valueOf(symbol);
						production.add(rule);
					}
					catch(Exception exc) {
						TigerTokenClass tokenClass;
						try {
							tokenClass = TigerTokenClass.valueOf(symbol);
							production.add(tokenClass);
						}
						catch(Exception exc2) {
							throw new RuntimeException("Unrecognized symbol '" + symbol + "' for rule " + productionName);
						}
					}
				}
				
				productionRule.productions.add(production);
			}
			
			for(TigerProductionRule rule : TigerProductionRule.values()) {
				if(rule.productions.isEmpty()) {
					throw new RuntimeException("Empty production rule: " + rule);
				}
			}
		}
		catch(IOException exc) {
			throw new RuntimeException("Error trying to read ProductionRules.txt file.", exc);
		}
	}
}
