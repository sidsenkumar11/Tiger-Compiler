from typing import Dict, List, Set
from helpers import Production, terminals, add_to_set, update_set, read_productions_from_file, fixpoint, add_to_dict
import pprint
import sys

def generate_firsts_one_pass(productions: List[Production], firsts: Dict[str, Set[str]]):
    for prod in productions:
        for rhs_first in prod.rhs:

            # rhs_first is terminal
            if rhs_first in terminals:
                add_to_set(firsts, prod.lhs, rhs_first)
                break

            # rhs_first is non-terminal
            next_symbols_firsts = firsts.get(rhs_first, set())
            update_set(firsts, prod.lhs, next_symbols_firsts)
            if "''" not in next_symbols_firsts:
                break


def generate_first_sets(productions: List[Production]):
    firsts = {}
    def compute_firsts_sets(firsts):
        generate_firsts_one_pass(productions, firsts)

    fixpoint(firsts, compute_firsts_sets)
    return firsts


def generate_follows(curr: str, next: str, firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]) -> bool:

    # don't need to compute follows of a terminal
    if curr in terminals:
        return False

    # non-terminal followed by non-terminal -> follow(curr).extend(firsts(next))
    if next not in terminals:
        update_set(follows, curr, firsts[next] - set(["''"]))
        return "''" in firsts[next]

    # non-terminal followed by terminal -> follow(curr).add(next)
    elif next != '':
        add_to_set(follows, curr, next)
        return False

    # non-terminal followed by epsilon -> skip this one
    else:
        return True


def generate_follows_one_pass(productions: List[Production], firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]):
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
        generate_follows_one_pass(productions, firsts, follows)

    fixpoint(follows, compute_follows_sets)
    return follows


def generate_parse_table(productions: List[Production], firsts: Dict[str, Set[str]], follows: Dict[str, Set[str]]) -> Dict[str, Dict[str, str]]:
    table = {}
    for prod in productions:
        for rhs_first in prod.rhs:

            # if rhs_first is epsilon, go to next symbol in production
            if rhs_first == "''":
                continue

            # rhs_first is terminal
            if rhs_first in terminals:
                add_to_dict(table, prod.lhs, rhs_first, prod)
                break

            # rhs_first is non-terminal
            entries = firsts.get(rhs_first)
            for first in entries:
                if first != "''":
                    add_to_dict(table, prod.lhs, first, prod)

            if "''" not in entries:
                break
        else:
            # whole production is nullable; add follows
            for follow in follows[prod.lhs]:
                add_to_dict(table, prod.lhs, follow, prod)
    return table


if __name__ == '__main__':
    productions = read_productions_from_file('productions.txt')
    firsts = generate_first_sets(productions)
    follows = generate_follow_sets("program", productions, firsts)

    if len(sys.argv) > 1 and sys.argv[1] == "--firsts":
        pprint.pprint(firsts)
    elif len(sys.argv) > 1 and sys.argv[1] == "--follows":
        pprint.pprint(follows)
    else:
        table = generate_parse_table(productions, firsts, follows)
        pprint.pprint(table)
