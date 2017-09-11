import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Action {
	
	public static enum Type {
		Shake,Yellow,Red,Down,Empty,Error;
	}
	
	private final Type type;
	private final int group;
	
	public Action(Type type, int group) {
		this.type = type;
		this.group = group;
	}
	
	public static Color colorWithType(Type type) {
		switch (type) {
		case Down:
			return Color.decode("#FFFFFF");
		case Red:
			return Color.decode("#FF5A04");
		case Yellow:
			return Color.decode("#FFC000");
		case Error:
			return Color.decode("#00FF00");
		case Empty:
			return Color.decode("#00B0F0");
		case Shake:
			return Color.decode("#92D050");
		}
		return null;
	}
	
	public static Type typeWithColor(String color) {
		switch (color) {
		case "FFFFFFFF":
			return Type.Down;
		case "FFFF5A04":
			return Type.Red;
		case "FFFFC000":
			return Type.Yellow;
		case "FF92D050":
			return Type.Shake;
		case "FF00B0F0":
			return Type.Empty;
		}
		System.out.println("Null color: " + color);
		return Type.Error;
	}
	
	public Action(String color, int group) {
		this(typeWithColor(color), group);
	}
	
	public Type getType() {
		return type;
	}
	
	public static List<Type> boldableTypes() {
		Type[] types = { Type.Red, Type.Yellow, Type.Down,Type.Empty,Type.Shake};
		return Arrays.asList(types);
	}
	
	public String toString() {
		if (type == null)
			return "null";
		if (type.equals(type.Empty))
			return "";
		String str = type.toString();
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Action))
			return false;
		Action action = (Action)obj;
		return action.type == type;
	}

	public int getGroup() {
		return group;
	}
}
