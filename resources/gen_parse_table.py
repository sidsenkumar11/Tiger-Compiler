import copy
import pprint

terminals = {"array", "begin", "break", "do", "else", "end", "enddo", "endif",
            "float", "for", "func", "if", "in", "int", "let", "of", "return", "then", "to",
            "type", "var", "while", ",", ":", ";", "(", ")", "[", "]", "{", "}", ".", "+", "-",
            "*", "/", "=", "<>", "<", ">", "<=", ">=", "&", "|", ":="}

terminals.update({"id", "intlit", "floatlit", "''"})


def parse_production(line):
    line = line.split()
    nonterminal = line[0]
    assert(line[1] == '->')
    return (nonterminal, line[2:])


def init():
    with open('productions.txt', 'r') as fin:
        text = fin.read()
        productions = [parse_production(line) for line in text.split('\n') if line]
    return productions

def add_to_firsts(firsts, nonterminal, thing_to_add, multiple=False):
    if nonterminal not in firsts:
        firsts[nonterminal] = set()

    if not multiple:
        firsts[nonterminal].add(thing_to_add)
    else:
        firsts[nonterminal].update(thing_to_add)

def generate_firsts(prod, productions, firsts):
    nonterminal, rhs = prod
    rhs_first = rhs[0]

    if rhs_first in terminals:
        add_to_firsts(firsts, nonterminal, rhs_first)
    else:
        filtered_prods = [_ for _ in productions if _[0] == rhs_first]
        for fp in filtered_prods:
            generate_firsts(fp, productions, firsts)
        add_to_firsts(firsts, nonterminal, firsts[rhs_first], multiple=True)

def generate_first_sets(productions):
    firsts = {}
    old_firsts = {-1}
    while old_firsts != firsts:
        old_firsts = copy.deepcopy(firsts)
        for prod in productions:
            generate_firsts(prod, productions, firsts)

    return firsts

productions = init()
firsts = generate_first_sets(productions)
pprint.pprint(firsts)
