import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
//import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
//import java.util.stream.Stream;

//import Pokemon.PointType;
//import static Pokemon.PointType.*;

public class SimpleCsvParser {
    private static final String PATH = "/home/Xchange/PR2/ue9/pokemon.csv"; //change to your path
	
    // hide ctor
    private SimpleCsvParser() {
    }

    public static List<String> readAllLinesFromCsv() throws IOException {
        return Files.lines(Paths.get(PATH)).skip(1).collect(Collectors.toList());
    }

    public static Pokemon parseLine(final String line) {
        String[] parts = line.split(",");

        Set<Property> prop_set = new HashSet<Property> (Arrays.asList(Property.fromString(parts[2])));
        if(Property.fromString(parts[3]) != null)
        	prop_set.add(Property.fromString(parts[3]));

        /* alternative using streams
        Set<Property> prop_set = Stream.of(Property.fromString(parts[2]), Property.fromString(parts[3]))
        		.filter(Objects::nonNull)
        		.collect(Collectors.toSet());
        */
        
        Map<Pokemon.PointType, Integer> points_map = Map.of(Pokemon.PointType.HEALTH, Integer.valueOf(parts[4]), Pokemon.PointType.ATTACK, Integer.valueOf(parts[5]),
                Pokemon.PointType.DEFENSE, Integer.valueOf(parts[6]), Pokemon.PointType.SP_ATTACK, Integer.valueOf(parts[7]),
                Pokemon.PointType.SP_DEFENSE, Integer.valueOf(parts[8]), Pokemon.PointType.SPEED, Integer.valueOf(parts[9]));
        return new Pokemon(
                parts[0],
                parts[1],
                prop_set,
                points_map,
                Boolean.valueOf(parts[10]));
    }

}
