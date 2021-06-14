import java.io.IOException;
import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws IOException {
		String currentPath = System.getProperty("user.dir");
		System.out.println(currentPath);
//		List<String> strs = SimpleCsvParser.readAllLinesFromCsv();
		
//		for(String s:strs) {
//			System.out.println(SimpleCsvParser.parseLine(s));
//		}
		
		/* alternative using streams
		 strs.forEach(s->System.out.println(SimpleCsvParser.parseLine(s)));
		 or
		 strs.stream().map(SimpleCsvParser::parseLine).forEach(System.out::println);
		*/
	}
}
