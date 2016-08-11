
public class CellPosition<C, R> implements Comparable<Object> {
	private final String c;
	private final Integer r;

	public CellPosition(String c, Integer r) {
		this.c = c;
		this.r = r;
	}

	public CellPosition(int irow, int icol) {
		this(alphabet(icol), irow);
	}

	private static String alphabet(int x) {
		return Character.toString((char) (x + 'A'));
	}

	public String getC() {
		return c;
	}

	public Integer getR() {
		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CellPosition))
			return false;
		@SuppressWarnings("unchecked")
		CellPosition<String, Integer> pos = (CellPosition<String, Integer>) obj;
		return pos.c.equals(c) && pos.r == r;
	}

	public String toString() {
		return String.format("%s%d", c, (r + 1));
	}

	@Override
	public int compareTo(Object obj) {
		if (!(obj instanceof CellPosition))
			return 0;
		@SuppressWarnings("unchecked")
		CellPosition<String, Integer> pos = (CellPosition<String, Integer>) obj;
		int cc = c.compareTo(pos.c);
		if (cc == 0) {
			if (pos.r > r)
				return -1;
			if (pos.r < r)
				return 1;
		}
		return cc;
	}
}