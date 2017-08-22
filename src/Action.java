import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Action {
	
	public static enum Type {
		Blue, Red, Black, White, Down, Gold,Error
	}
	
	private final Type type;
	private final int group;
	
	public Action(Type type, int group) {
		this.type = type;
		this.group = group;
	}
	
	public static Color colorWithType(Type type) {
		switch (type) {
		case Error:
			return Color.decode("#00FF00");
		case Down:
			return Color.decode("#FFFFFF");
		case Blue:
			return Color.decode("#012060");
		case Red:
			return Color.decode("#C00004");
		case Black:
			return Color.decode("#000000");
		case White:
			return Color.decode("#808080");
		case Gold:
			return Color.decode("#C55A11");
		}
		return null;
	}
	
	public static Type typeWithColor(String color) {
		switch (color) {
		case "FFFFFFFF":
			return Type.Down;
		case "FFFF00FF":
			return Type.Blue;
		case "FFFFFF00":
			return Type.Red;
		case "FF000000":
			return Type.Black;
		case "FF0000FF":
			return Type.White;
		case "FF00FF00":
			return Type.Gold;
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
		Type[] types = { Type.Blue, Type.Red, Type.Black, Type.White, Type.Down,Type.Gold };
		return Arrays.asList(types);
	}
	
	public String toString() {
		if (type == null)
			return "null";
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
