import java.util.ArrayList;

public class junkWork {
	public static void main(String[] Args) {
		Lexer lexer = new Lexer();
		String input = "print 5+3, (3+33), 5";
		String input2 = "x = (5 + 7) * 4 + (4 * 3) print 3,3,3";
		String input3 = "5.0 + 7.0 * 4.0 + (4.0 * 3.0)";
		String input4 = "(5.0 + 7.0) * 4.0 + (4.0 * 3.0)";
		lexer.lex(input2);
		ArrayList<Token> tokens = lexer.getTokens();

		Parser parser = new Parser(tokens);
		Node AstTree = parser.parse();
		try {
			String out = AstTree.toString();
			System.out.println(out);
		} catch (Error e) {
			System.out.println(e);
		}

	}
}
