//import java.util.stream.Stream;

public enum Property {
	 GRASS,
	 FIRE,
	 WATER,
	 GROUND,
	 ELECTRIC,
	 BUG,
	 POISON,
	 FLYING,
	 NORMAL,
	 FAIRY,
	 GHOST,
	 STEEL,
	 DARK,
	 FIGHTING,
	 PSYCHIC,
	 ROCK,
	 DRAGON,
	 ICE;
	public static Property fromString(String s) {
		for(Property p:values()) {
			if(p.name().equalsIgnoreCase(s)) {
				return p;
			}
		}
		return null;
		
		/* alternative using Streams:
		return Stream.of(values())
				.filter(p -> p.name().equalsIgnoreCase(s))
				.findFirst()
				.orElse(null);
		*/
	};
}
