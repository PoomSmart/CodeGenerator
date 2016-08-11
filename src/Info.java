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

	private boolean isET() {
		return music.equals("ET") || music.equals("E.T.");
	}

	private boolean isRocketeer() {
		return music.equals("Rocketeer");
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
		int codeGroup = 1;
		int level = 0;
		int i = 0;
		int actionsSize = actions.size();
		// Deal with the heading group of Rocketeer
		if (isRocketeer()) {
			sb.append("\n");
			for (int j = 1; j <= 4; j++)
				appendString(sb, fixType(actions.get(i++).toString()));
			sb.append("\n");
			// Special type: count down
			appendString(sb, "3");
			appendString(sb, "2");
			appendString(sb, "1");
			appendString(sb, "T");
			sb.append("\n\n");
		} else
			sb.append("\nGroup 1\n\n");
		int perRow = 0;
		boolean rkt_group2Expansion = false;
		boolean rkt_group4Expansion = false;
		boolean rkt_group5Expansion = false;
		boolean et_group4Modification = false;
		int space = 5;
		do {
			if (actions.get(i).getGroup() != codeGroup) {
				// excess steps, got them covered here
				if (perRow > 0) {
					sb.append("\n");
					appendNumber(sb, level * steps + 1, level * steps + perRow);
					level = 0;
				}
				if (codeGroup + 1 == 7 && isET()) {
					sb.append("\nLast Group\n\n");
					++codeGroup;
				} else if (codeGroup + 1 >= 4 && isET())
					++codeGroup;
				else
					sb.append(String.format("Group %d\n\n", ++codeGroup - (isRocketeer() ? 1 : 0)));
			}
			if (codeGroup == 3 && isRocketeer() && !rkt_group2Expansion) {
				// We are iterating steps in group 2 of Rocketeer
				int eq = 1;
				while (actions.get(i).equals(actions.get(i + 1)) && eq < 8) {
					i++;
					eq++;
				}
				if (eq == 8) {
					sb.append(fixType(actions.get(i).toString()) + "\n");
					i++;
				} else {
					sb.append(String.format(String.format("%%-%ds%%s\n", space * eq),
							fixType(actions.get(i).toString()), fixType(actions.get(i + 1).toString())));
					i += 8 - eq + 1;
				}
				sb.append("2    2    3    1    2    2    3    2\n\n");
				for (int j = 3; j <= 8; j++)
					appendString(sb, fixType(actions.get(i++).toString()));
				sb.append("\n");
				appendNumber(sb, 3, 8);
				rkt_group2Expansion = true;
				continue;
			} else if (codeGroup == 5 && isRocketeer() && !rkt_group4Expansion) {
				// We are iterating steps in group 4 of Rocketeer
				// Duplication :(
				perRow = 0;
				for (int x = 1; x <= 4; x++) {
					appendString(sb, fixType(actions.get(i++).toString()));
					perRow++;
					if (level > 1)
						level = 0;
					if (perRow == steps) {
						perRow = 0;
						sb.append("\n");
						appendNumber(sb, level == 0 ? 1 : steps + 1, (level + 1) * steps);
						sb.append("\n");
						level++;
					}
				}
				for (int j = 1; j <= 8; j++)
					appendString(sb, fixType(actions.get(i++).toString()), space);
				sb.append("\n4    2    3    5    4    2    3    6\n\n");
				for (int j = 1; j <= 8; j++)
					appendString(sb, fixType(actions.get(i++).toString()), space);
				sb.append("\n4    2    3    7    4    2    3    8\n\n");
				for (int j = 1; j <= 8; j++)
					appendString(sb, fixType(actions.get(i++).toString()), space);
				sb.append("\n4    2    3    9    4    2    3    10\n\n");
				rkt_group4Expansion = true;
				continue;
			} else if (codeGroup == 4 && isET() && !et_group4Modification) {
				for (int k = 0; k < 2; k++) {
					for (int j = 1; j <= 2; j++)
						appendString(sb, fixType(actions.get(i++).toString()));
					sb.append("\n");
					for (String word : et_words[k])
						sb.append(String.format(String.format("%%-%ds", stepSpace), word));
					sb.append("\n\n");
					for (int j = 1; j <= 4; j++)
						appendString(sb, fixType(actions.get(i++).toString()));
					sb.append("\n");
					for (String word : et_words2[k])
						sb.append(String.format(String.format("%%-%ds", stepSpace), word));
					sb.append("\n\n");
					for (int j = 1; j <= 2; j++)
						appendString(sb, fixType(actions.get(i++).toString()));
					sb.append("\n");
					appendNumber(sb, 1, 2);
				}
				sb.append("\n-Wave-\n");
				et_group4Modification = true;
				// Because you ignore those two sheets
				codeGroup += 2;
				continue;
			} else if (codeGroup == 6 && isRocketeer() && !rkt_group5Expansion) {
				// We are iterating steps in group 5 of Rocketeer
				for (int line = 1; line <= 2; line++) {
					Action startAction = actions.get(i);
					Action beforeStartAction = actions.get(i - 1);
					if (!startAction.equals(beforeStartAction))
						appendString(sb, fixType(startAction.toString()), space);
					else
						appendString(sb, "", space);
					i++;
					for (int j = 2; j <= 8; j++) {
						Action previousAction = actions.get(i - 1);
						Action currentAction = actions.get(i);
						if (!currentAction.equals(previousAction))
							appendString(sb, fixType(currentAction.toString()), space);
						else
							appendString(sb, "", space);
						i++;
					}
					sb.append(
							String.format("\n5    2    3    %d    5    2    3    %d\n\n", 2 * (line - 1) + 1, 2 * line));
				}
				rkt_group5Expansion = true;
				// Done, should stop
				break;
			} else {
				appendString(sb, fixType(actions.get(i).toString()));
				perRow++;
				if (level > 1)
					level = 0;
				if (perRow == steps) {
					perRow = 0;
					sb.append("\n");
					appendNumber(sb, level * steps + 1, (level + 1) * steps);
					level++;
				}
			}
			i++;
		} while (i < actionsSize);
		// excess steps again, but the case for last group
		if (perRow > 0) {
			sb.append("\n\n");
			appendNumber(sb, level * steps + 1, level * steps + perRow);
			level = 0;
		}
		return sb.toString();
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
