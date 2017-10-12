package enshud.s2.parser.parsers.basic;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import enshud.s1.lexer.LexedToken;
import enshud.s1.lexer.TokenType;
import enshud.s2.parser.ParserInput;
import enshud.s2.parser.node.INode;
import enshud.s2.parser.node.basic.FailureNode;
import enshud.s2.parser.parsers.IParser;

public class LookAheadSelectParser implements IParser {
	final ParserPair[] pairs;

	public LookAheadSelectParser(ParserPair[] pairs) {
		this.pairs = Objects.requireNonNull(pairs);
	}

	@Override
	public Set<TokenType> getFirst() {
		Set<TokenType> set = new HashSet<>();
		for (ParserPair p : pairs) {
			set.addAll(p.look.getFirst());
		}
		return set;
	}

	@Override
	public INode parse(ParserInput input) {
		for (ParserPair p : pairs) {
			if (p.look.parse(input).isSuccess()) {
				return p.parser.parse(input);
			}
		}

		if (!input.isEmpty()) {
			LexedToken tk = input.getFront();
			return new FailureNode(tk, "Selection Not Found.");
		} else {
			return new FailureNode("Selection Not Found.");
		}

	}

}
