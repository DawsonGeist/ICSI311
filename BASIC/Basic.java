import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Basic {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		// Check args to ensure that only one was passed in ; else throw error
		if (args.length != 1) {
			System.out.println("Invalid arguments");
		} else {
			Node astTree = new StatementsNode();

			// Get all the lines from the filename passed as an argument
			String fileName = args[0];
			Path path = Paths.get(fileName);
			List<String> lines = Files.readAllLines(path);

			// Variable to hold all the tokens
			ArrayList<String> tokens = new ArrayList<String>();
			ArrayList<String> temp = new ArrayList<String>();

			// Loop through the lines Creating a lexer object for each line
			for (String line : lines) {
				Lexer l = new Lexer();
				temp = l.lex(line);

				// Parse Tokens
				Parser parser = new Parser(l.getTokens());
				Node tempAST = parser.parse();
				for (Node statements : ((StatementsNode) tempAST).getStatements()) {
					((StatementsNode) astTree).addStatement(statements);
				}
				try {
					String out = tempAST.toString();
					System.out.println(out);
				} catch (Error e) {
					System.out.println(e);
				}

				// Clear list
				temp.clear();
				// Print a new line
				System.out.println();
			}

			// Pass to the interpreter
			Interpreter interpret = new Interpreter(((StatementsNode) astTree).getStatements());
		}

	}

}
