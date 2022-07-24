from typing import Dict, List, Set
from helpers import Production, terminals, add_to_set, update_set, read_productions_from_file, fixpoint, add_to_dict

def generate_firsts(prod: Production, productions: List[Production], firsts: Dict[str, Set[str]]):
    rhs_first = prod.rhs[0]

    if rhs_first in terminals:
        if rhs_first != "''":
            add_to_set(firsts, prod.lhs, rhs_first)
    else:
        filtered_prods = [_ for _ in productions if _[0] == rhs_first]
        for fp in filtered_prods:
            generate_firsts(fp, productions, firsts)
        update_set(firsts, prod.lhs, firsts[rhs_first])


def generate_first_sets(productions: List[Production]):
    firsts = {}
    def compute_firsts_sets(firsts):
        for prod in productions:
            generate_firsts(prod, productions, firsts)

    fixpoint(firsts, compute_firsts_sets)
    return firsts


def generate_follows(curr: str, next: str, firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]) -> bool:

    # don't need to compute follows of a terminal
    if curr in terminals:
        return False

    # non-terminal followed by non-terminal -> follow(curr).extend(firsts(next))
    if next not in terminals:
        update_set(follows, curr, firsts[next])
        return False

    # non-terminal followed by terminal -> follow(curr).add(next)
    elif next != '':
        add_to_set(follows, curr, next)
        return False

    # non-terminal followed by epsilon -> skip this one
    else:
        return True


def generate_follows_pass(productions: List[Production], firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]):
    for prod in productions:
        for i in range(len(prod.rhs)-1):
            curr = prod.rhs[i]
            next = prod.rhs[i+1]

            go_next = generate_follows(curr, next, firsts, follows)
            next_ind = i + 2
            while go_next:
                if next_ind < len(prod.rhs):
                    go_next = generate_follows(curr, prod.rhs[next_ind], firsts, follows)
                    next_ind += 1
                else:
                    # reached end of rhs -> follows(curr).extend(follows(lhs))
                    update_set(follows, curr, follows[prod.lhs])
                    break


        if prod.rhs[-1] not in terminals:
            update_set(follows, prod.rhs[-1], follows[prod.lhs])


def generate_follow_sets(start_symbol: str, productions: List[Production], firsts: Dict[str, Set[str]]):
    follows = {start_symbol: {"$"}}
    def compute_follows_sets(follows):
        generate_follows_pass(productions, firsts, follows)

    fixpoint(follows, compute_follows_sets)
    return follows


def generate_parse_table(productions: List[Production], firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]) -> Dict[str, Dict[str, str]]:
    table = {}
    for prod in productions:
        rhs_firsts = {prod.rhs[0]} if prod.rhs[0] in terminals else firsts[prod.rhs[0]]
        for first in rhs_firsts:
            if first != "''":
                add_to_dict(table, prod.lhs, first, prod)

        if "''" in rhs_firsts:
            for follow in follows[prod.lhs]:
                add_to_dict(table, prod.lhs, follow, prod)
    return table

if __name__ == '__main__':
    productions = read_productions_from_file('productions.txt')
    firsts = generate_first_sets(productions)
    follows = generate_follow_sets("program", productions, firsts)
    table = generate_parse_table(productions, firsts, follows)
    import pprint
    pprint.pprint(table)
