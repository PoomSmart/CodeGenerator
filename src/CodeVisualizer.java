import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class CodePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Map<CellPosition<String, Integer>, Info> map;

	private final int shiftLeft;
	private final int shiftTop;
	private final Dimension bounds;
	private final Dimension tileSize;
	private final Dimension absoluteSize;

	private int step;

	private static final int textGap = 10;

	class Cell<X, Y> {
		public String x;
		public Integer y = -1;

		public Cell(String x, Integer y) {
			this.x = x;
			this.y = y;
		}

		public boolean isNull() {
			return x == null || y == -1;
		}

		public String toString() {
			if (isNull())
				return "Null";
			return String.format("%s-%d", x, y);
		}
	}

	public CodePanel(Map<CellPosition<String, Integer>, Info> map, int shiftLeft, int shiftTop, Dimension bounds,
			Dimension tileSize, Dimension absoluteSize) {
		this.shiftLeft = shiftLeft;
		this.shiftTop = shiftTop;
		this.map = map;
		this.bounds = bounds;
		this.tileSize = tileSize;
		this.absoluteSize = absoluteSize;
		this.step = 0;
	}

	private static String alphabet(int x) {
		return Character.toString((char) (x + 'A'));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int tileWidth = tileSize.width;
		int tileHeight = tileSize.height;
		String fpsLabel = String.valueOf(step + 1);
		FontMetrics metrics = g.getFontMetrics();
		int labelHeight = metrics.getHeight();
		int labelWidth = metrics.stringWidth(fpsLabel);
		g.setColor(Color.red);
		g.drawString(fpsLabel, shiftLeft - textGap - (int) (0.5 * labelWidth), shiftTop - textGap);
		for (int x = 0; x < bounds.width; x++) {
			g.setColor(Color.black);
			String xLabel = String.valueOf(bounds.width - x);
			labelWidth = metrics.stringWidth(xLabel);
			g.drawString(xLabel, shiftLeft + x * tileWidth + (int) (0.5 * tileWidth) - (int) (0.5 * labelWidth),
					shiftTop - textGap);
			for (int y = 0; y < bounds.height; y++) {
				// FIXME: Check this
				Info info = map.get(new CellPosition<String, Integer>(bounds.height - y - 1, bounds.width - x - 1));
				if (info != null) {
					int idx = step;
					List<Action> actions = info.getActions();
					// If we reach the end, just stop
					if (idx >= actions.size())
						break;
					g.setColor(Action.colorWithType(actions.get(idx).getType()));
					g.fillRect(shiftLeft + x * tileWidth, shiftTop + y * tileHeight, tileWidth, tileHeight);
				}
			}
			g.setColor(Color.white);
			g.drawLine(shiftLeft + x * tileWidth, shiftTop, shiftLeft + x * tileWidth, absoluteSize.height);
		}
		for (int y = 0; y < bounds.height; y++) {
			g.setColor(Color.white);
			g.drawLine(shiftLeft, shiftTop + y * tileHeight, absoluteSize.width, shiftTop + y * tileHeight);
			g.setColor(Color.black);
			String yLabel = alphabet(bounds.height - y - 1);
			labelWidth = metrics.stringWidth(yLabel);
			g.drawString(yLabel, shiftLeft - textGap - (int) (0.5 * labelWidth),
					shiftTop + (int) (0.5 * labelHeight) + y * tileHeight + (int) (0.5 * tileHeight));
		}
		g.setColor(Color.white);
		g.drawLine(absoluteSize.width, shiftTop, absoluteSize.width, absoluteSize.height);
		g.drawLine(shiftLeft, absoluteSize.height, absoluteSize.width, absoluteSize.height);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

}

public class CodeVisualizer extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Map<CellPosition<String, Integer>, Info> map;

	private static final int shiftLeft = 30;
	private static final int shiftTop = 30;

	private final int fps;
	private final int totalFrames;
	private final Dimension bounds;
	private static final Dimension tileSize = new Dimension(40, 40);
	private final Dimension absoluteSize;

	private final CodePanel panel;

	public CodeVisualizer(Map<CellPosition<String, Integer>, Info> map, Dimension bounds, int fps, int totalFrames) {
		this.map = map;
		this.fps = fps;
		this.totalFrames = totalFrames;
		this.bounds = bounds;
		this.absoluteSize = new Dimension(bounds.width * tileSize.width + shiftLeft,
				bounds.height * tileSize.height + shiftTop);
		this.setTitle("Code Visualizer");
		this.setSize(absoluteSize.width + shiftLeft, absoluteSize.height + (int) (tileSize.height * 0.5) + shiftTop);
		panel = new CodePanel(this.map, shiftLeft, shiftTop, this.bounds, tileSize, absoluteSize);
		this.add(panel);
		this.setResizable(false);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					run();
					return;
				}
				int d = 0;
				if (e.getKeyCode() == 37)
					d = -1;
				else if (e.getKeyCode() == 39)
					d = 1;
				changeStep(d);
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});
	}

	private void changeStep(int d) {
		if (d == 0 || (panel.getStep() >= totalFrames - 1 && d > 0) || (panel.getStep() == 0 && d < 0))
			return;
		panel.setStep(panel.getStep() + d);
		panel.repaint();
	}

	// Run animation of the pattern
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setStep(0);
				Timer timer = new Timer(1000 / fps, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (panel.getStep() >= totalFrames - 1) {
							((Timer) e.getSource()).stop();
							return;
						}
						panel.setStep(panel.getStep() + 1);
						panel.repaint();

					}
				});
				timer.start();
			}
		});
	}
}
