import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Action {
	
	public static enum Type {
		Blue, Red, Black, White, Down, Fold, Fallback
	}
	
	private final Type type;
	private final int group;
	
	public Action(Type type, int group) {
		this.type = type;
		this.group = group;
	}
	
	public static Color colorWithType(Type type) {
		switch (type) {
		case Fold:
			return Color.decode("#FFFF00");
		case Down:
			return Color.decode("#FFC000");
		case Blue:
			return Color.decode("#0000FF");
		case Red:
			return Color.decode("#FF0000");
		case Black:
			return Color.decode("#000000");
		case White:
			return Color.decode("#FFFFFF");
		case Fallback:
			return Color.decode("#767676");
		}
		return null;
	}
	
	public static Type typeWithColor(String color) {
		switch (color) {
		case "FFA6A6A6":
		case "FFFFFF00":
			return Type.Fold;
		case "FFF79646":
		case "FFFFC000":
			return Type.Down;
		case "FF3333FF":
		case "FF0000FF":
		case "FF00B0F0":
			return Type.Blue;
		case "FFFF0000":
			return Type.Red;
		case "FF000000":
			return Type.Black;
		case "FFFFFFFF":
			return Type.White;
		}
		System.out.println("Null color: " + color);
		return Type.Fallback;
	}
	
	public Action(String color, int group) {
		this(typeWithColor(color), group);
	}
	
	public Type getType() {
		return type;
	}
	
	public static List<Type> boldableTypes() {
		Type[] types = { Type.Blue, Type.Red, Type.Black, Type.White, Type.Down };
		return Arrays.asList(types);
	}
	
	public String toString() {
		if (type == null)
			return "null";
		if (type == Type.Fold)
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
