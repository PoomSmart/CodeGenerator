import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Action {
	
	public static enum Type {
		Blue, Red, Black, White, Down, Gold,Error, Wave,Shake,Not
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
			return Color.decode("#002060");
		case Red:
			return Color.decode("#C00000");
		case Black:
			return Color.decode("#000000");
		case White:
			return Color.decode("#BFBFBF");
		case Gold:
			return Color.decode("#ED7D31");
		case Wave:
			return Color.decode("#FFC000");
		case Not:
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
		case "FF002060":
			return Type.Blue;
		case "FFC00000":
			return Type.Red;
		case "FF000000":
			return Type.Black;
		case "FFBFBFBF":
			return Type.White;
		case "FFED7D31":
			return Type.Gold;
		case "FFFFC000":
			return Type.Wave;
		case "FF00B0F0":
			return Type.Not;
		case "FF92D050":
			return Type.Shake;
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
		Type[] types = { Type.Blue, Type.Red, Type.Black, Type.White, Type.Down,Type.Gold,Type.Not,Type.Shake,Type.Wave };
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
