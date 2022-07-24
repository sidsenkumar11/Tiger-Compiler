from typing import Callable, NamedTuple
from typing import Dict, List, Set
import copy

class Production(NamedTuple):
    lhs: str
    rhs: List[str]

terminals = {"array", "begin", "break", "do", "else", "end", "enddo", "endif",
            "float", "for", "func", "if", "in", "int", "let", "of", "return", "then", "to",
            "type", "var", "while", ",", ":", ";", "(", ")", "[", "]", "{", "}", ".", "+", "-",
            "*", "/", "=", "<>", "<", ">", "<=", ">=", "&", "|", ":="}

terminals.update({"id", "intlit", "floatlit", "''"})


def parse_production(line: str) -> Production:
    split = line.replace('ϵ', "''").split()
    if split[1] !='->' and split[1] != '→':
        raise Exception(f"Unable to parse production: {line}")

    if split[0] == split[2]:
        raise Exception(f"Production has left recursion: {line}")
    return Production(lhs=split[0], rhs=split[2:])


def read_productions_from_file(fname: str) -> List[Production]:
    with open(fname, 'r') as fin:
        text = fin.read()
        productions = [parse_production(line) for line in text.split('\n') if line and not line.startswith('#')]
    return productions


def add_to_dict(data_set: Dict[str, Set[str]], nonterminal: str, terminal: str, production: str):
    if nonterminal not in data_set:
        data_set[nonterminal] = {}

    if terminal in data_set[nonterminal]:
        raise Exception(f"Grammar is not LL(1). Tried to add multiple entries to ({nonterminal},{terminal}).\nExisting: {data_set[nonterminal][terminal]}\nNew: {production}")

    data_set[nonterminal][terminal] = production


def add_to_set(data_set: Dict[str, Set[str]], nonterminal: str, terminal: str):
    if nonterminal not in data_set:
        data_set[nonterminal] = set()

    data_set[nonterminal].add(terminal)


def update_set(data_set: Dict[str, Set[str]], nonterminal: str, terminal_set: Set[str]):
    if nonterminal not in data_set:
        data_set[nonterminal] = set()

    data_set[nonterminal].update(terminal_set)


def fixpoint(return_set: Set[str], func_that_mutates_return_set: Callable[[Set[str]], None]):
    old_return_set = {-1}
    while old_return_set != return_set:
        old_return_set = copy.deepcopy(return_set)
        func_that_mutates_return_set(return_set)
    return return_set
