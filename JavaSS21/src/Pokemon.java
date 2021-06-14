import java.util.Map;
import java.util.Set;

public class Pokemon {
	private String id;
	private String name;
	private Set<Property> properties;
	private Map<PointType, Integer> points;
	private boolean legendary;
	
	public enum PointType {
		HEALTH,
		ATTACK,
		DEFENSE,
		SP_ATTACK,
		SP_DEFENSE, 
		SPEED
	}
	
	public Pokemon(String id, String name, Set<Property> properties, Map<PointType, Integer> points_map, boolean legendary) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.points = points_map;
		this.legendary = legendary;
	}

	@Override
	public String toString() {
		return String.format("Pokemon [id=%s, name=%s, properties=%s, points=%s, legendary=%s]", id, name, properties,
				points, legendary);
	}
}
