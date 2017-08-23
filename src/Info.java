import java.util.List;
import java.util.Vector;

public class Info {

	private static final int stepSpace = 7;
	public static final boolean useFormatting = true;

	private final CellPosition<String, Integer> position;
	private final String music;
	private final String dimension;
	private final int steps;
	private final Vector<Action> actions;

	private static final String[][] et_words = { { "Kiss", "Me" }, { "Take", "Me" } };
	private static final String[][] et_words2 = { { "Kiss", "Kiss", "Kiss", "Me" }, { "Take", "Take", "Take", "Me" } };

	public Info(CellPosition<String, Integer> position, int steps, String dimension, String music) {
		this.position = position;
		this.steps = Math.max(steps, 4);
		this.dimension = dimension;
		if (music.equals("ET"))
			music = "E.T.";
		this.music = music;
		this.actions = new Vector<Action>();
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	private String fixType(String str) {
		// Workaround for ET
		if (music.equals("E.T.")) {
			if (str.contains("Black"))
				str = str.replaceAll("Black", "Down");
			else if (str.contains("Blue"))
				str = str.replaceAll("Blue", "Up");
		}
		return str;
	}

	private static void appendString(StringBuilder sb, String str, int stepSpace) {
		if (useFormatting)
			sb.append(String.format(String.format("%%-%ds", stepSpace), str));
		else
			sb.append(String.format("%s\t", str));
	}

	private static void appendString(StringBuilder sb, String str) {
		appendString(sb, str, stepSpace);
	}

	private static void appendNumber(StringBuilder sb, int min, int max) {
		for (int j = min; j <= max; j++) {
			if (useFormatting)
				sb.append(String.format(String.format("%%-%ds", stepSpace), j));
			else
				sb.append(String.format("%d\t", j));
		}
		sb.append("\n\n");
	}
	
	private static void appendNumberNew(StringBuilder sb, int start, int max) {
		if (useFormatting)
			sb.append(String.format(String.format("%%-%ds", stepSpace), start));
		else
			sb.append(String.format("%d\t", start));
		for (int j = 2; j <= max; j++) {
			if (useFormatting)
				sb.append(String.format(String.format("%%-%ds", stepSpace), j));
			else
				sb.append(String.format("%d\t", j));
		}
		sb.append("\n\n");
	}

	public String toString(boolean debug) {
		if (debug)
			return "Info->Actions[" + actions + "]";
		StringBuilder sb = new StringBuilder();
		// Don't try this at home
		if (useFormatting) {
			int halfSpace1 = stepSpace * steps / 2;
			int halfSpace2 = (stepSpace * steps) - halfSpace1 + 14;
			sb.append(String.format(String.format("%%-%ds%%%ds", halfSpace1, halfSpace2), "Seat: " + position, music));
		} else
			sb.append(String.format("Seat: %s\t\t\t\t\t%s", position, music));
		sb.append("\n");
		//initial
		sb.append("\nDown\n");
		for(int i = 1 ; i<=5;i++){
			appendNumberNew(sb,i,4);
			sb.deleteCharAt(sb.length()-1);
		}
		int codeGroup = 1;
		int level = 1;
		int i = 0;
		int actionsSize = actions.size();
		sb.append("\nGroup 1\n\n");
		int perRow = 0;
		int space = 5;
		do {
			if (actions.get(i).getGroup() != codeGroup) {
				// excess steps, got them covered here
				if (perRow > 0) {
					sb.append("\n");
					appendNumber(sb, level * steps + 1, level * steps + perRow);
					level = 0;
				}
				sb.append(String.format("Group %d\n\n", ++codeGroup ));
			}
			appendString(sb, fixType(actions.get(i).toString()));
			perRow++;
			if (level > 8)
				level = 1;
			if (perRow == steps) {
				perRow = 0;
				sb.append("\n");
				appendNumberNew(sb, level, 4);
				level++;
			}
			i++;
		} while (i < actionsSize);
		// excess steps again, but the case for last group
		if (perRow > 0) {
			sb.append("\n\n");
			appendNumber(sb, level * steps + 1, level * steps + perRow);
			level = 0;
		}
		String str = sb.toString();
		sb = null;
		return str;
	}

	public String toString() {
		return toString(true);
	}

	public List<Action> getActions() {
		return actions;
	}

	public String getMusic() {
		return music;
	}

	public String getDimension() {
		return dimension;
	}

}
