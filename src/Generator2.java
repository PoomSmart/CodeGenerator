import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class Generator2 {

	public static boolean writeToFile = true;
	public static boolean visualize = true;
	public static boolean sheetExclusion = false;
	public static boolean frameSimplification = false;
	private static Map<CellPosition<String, Integer>, Info> currentMap;
	private static String currentMusic;

	/**
	 * Assign action to a cell according to cell color. If incompatible color presents, returned action would be of null
	 * type
	 * 
	 * @param map
	 * @param cell
	 * @param i
	 * @param j
	 * @param numberOfSteps
	 * @param numRows
	 * @param numCols
	 */
	public static void haveAction(Map<CellPosition<String, Integer>, Info> map, XSSFCell cell, int i, int j,
			int numberOfSteps, int numRows, int numCols, int sheetNumber) {
		// FIXME: validate
		int r = numRows - i - 1;
		int c = numCols - j - 1;
		CellPosition<String, Integer> pos = new CellPosition<String, Integer>(r, c);
		if (!map.containsKey(pos))
			map.put(pos, new Info(pos, numberOfSteps, String.format("%dx%d", numRows, numCols), currentMusic));
		XSSFCellStyle style = cell.getCellStyle();
		if (style != null) {
			XSSFColor color = style.getFillForegroundXSSFColor();
			if (color != null) {
				String colorString = color.getARGBHex();
				map.get(pos).addAction(new Action(colorString, sheetNumber));
			} else
				map.get(pos).addAction(new Action(Action.Type.White, sheetNumber));
		} else
			map.get(pos).addAction(new Action("", sheetNumber));
	}

	private static void writeMapToFiles(Map<CellPosition<String, Integer>, Info> map, int fontSize, String outputPath) {
		for (Entry<CellPosition<String, Integer>, Info> entry : map.entrySet()) {
			CellPosition<String, Integer> pos = entry.getKey();
			Info info = entry.getValue();
			// System.out.println(pos + ": " + info);
			try {
				XWPFDocument doc = new XWPFDocument();
				XWPFParagraph paragraph = doc.createParagraph();
				addParagraphFromInfo(info, paragraph, fontSize);
				FileOutputStream out = new FileOutputStream(String.format("%s/%s.docx", outputPath, pos));
				doc.write(out);
				out.close();
				doc.close();
			} catch (IOException e) {
				ErrorReporter.report(e);
			}
		}
	}

	private static void addParagraphFromInfo(Info info, XWPFParagraph paragraph, int fontSize) {
		XWPFRun run = paragraph.createRun();
		run.setFontFamily("Consolas");
		String data = info.toString(false);
		if (data.contains("\n")) {
			String[] lines = data.split("\n");
			setTextWithTab(run, lines[0]);
			for (int i = 1; i < lines.length; i++) {
				run.addBreak();
				setTextWithTab(run, lines[i]);
			}
		} else
			run.setText(data, run.getTextPosition());
		run.setFontSize(fontSize);
	}

	private static void createOutput(String fileName, Map<CellPosition<String, Integer>, Info> map, int fontSize) {
		String outputPath = "Output/" + fileName;
		createPathIfNecessary(outputPath);
		if (writeToFile)
			writeMapToFiles(map, fontSize, outputPath);
	}

	/**
	 * Read excel file of given pattern and properties (from Input folder), and then generate codes for all cells (into
	 * Output folder)
	 * 
	 * @param filename
	 * @param numRows
	 * @param numCols
	 * @param rowsPerPage
	 * @param framesPerRow
	 * @param gap
	 * @param fontSize
	 */
	public static void workWithPattern(String filename, int fps, int numRows, int numCols, int rowsPerPage,
			int totalSheets, int framesPerRow, int gap, int fontSize) {
		createPathIfNecessary("Output/");
		Map<CellPosition<String, Integer>, Info> map = new TreeMap<CellPosition<String, Integer>, Info>();
		currentMusic = filename;
		int totalFrames = 0;
		try {
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("Input/" + filename + ".xlsx"));
			boolean stop = false;
			if (totalSheets == -1)
				totalSheets = wb.getNumberOfSheets();
			for (int ns = 0; ns < totalSheets; ns++) {
				if (sheetExclusion) {
					// Note: exclusion for sheets - Mit
					if (filename.equals("ET")) {
						// skip wave pattern
						if (ns == 4 || ns == 5)
							continue;
					}
				}
				XSSFSheet sheet = wb.getSheetAt(ns);
				for (int fn = 0; fn < rowsPerPage * framesPerRow; fn++) {
					if (frameSimplification) {
						// Note: read only last frame for each row - Mit
						if (filename.equals("Rocketeer")) {
							if (ns == 0 || ns == 1 || ns == 2 || ns == 3) {
								// skip non-last frame
								if ((fn + 1) % framesPerRow != 0) {
									// for Group 2, only first 8 frames are ignored
									// for Group 3, ...
									// for Group 4, only first 16 (or 12) frames are eligible
									if ((ns == 1 && fn >= 8) || (ns == 3 && fn < 16) || (ns != 3 && ns != 1) || ns == 2)
										continue;
								}
							}
						} else if (filename.equals("ET")) {
							if (ns <= 3 || ns == 6) {
								if ((fn + 1) % framesPerRow != 0) {
									// for non-Group 3, do so
									// for Group 3, frame 6-11 and 18-23
									if (ns != 3 || (ns == 3 && ((fn >= 6 && fn <= 11) || (fn >= 18 && fn <= 23))))
										continue;
								}
							}
						}
					}
					int foi = (fn / framesPerRow) * (gap + numRows);
					int foj = (fn % framesPerRow) * (gap + numCols);
					totalFrames++;
					for (int i = 0; i < numRows; i++) {
						XSSFRow row = sheet.getRow(foi + i);
						// If the current row is null, it is possible that we read to the end of the animation. So we
						// stop here
						if (row == null) {
							totalFrames--;
							stop = true;
							break;
						}
						for (int j = 0; j < numCols; j++) {
							XSSFCell cell = row.getCell(foj + j);
							if (cell != null)
								haveAction(map, cell, i, j, framesPerRow, numRows, numCols, ns + 1);
						}
					}
					if (stop)
						break;
				}
				if (stop)
					break;
			}
			currentMap = map;
			if (visualize) {
				CodeVisualizer vis = new CodeVisualizer(map, new Dimension(numRows, numCols), fps, totalFrames);
				vis.setVisible(true);
				vis.setLocationRelativeTo(null);
				Thread t = new Thread() {
					public void run() {
						synchronized (vis) {
							while (vis.isVisible()) {
								try {
									vis.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				};
				try {
					t.join();
				} catch (InterruptedException e) {
					ErrorReporter.report(e);
				}
			}
			wb.close();
		} catch (EncryptedDocumentException | IOException e) {
			ErrorReporter.report(e);
		}
		createOutput(filename, map, fontSize);
	}
	
	private static void setSingleLineSpacing(XWPFParagraph paragraph) {
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		CTPPr ppr = paragraph.getCTP().getPPr();
	    if (ppr == null) ppr = paragraph.getCTP().addNewPPr();
	    CTSpacing spacing = ppr.isSetSpacing()? ppr.getSpacing() : ppr.addNewSpacing();
	    spacing.setAfter(BigInteger.valueOf(0));
	    spacing.setBefore(BigInteger.valueOf(0));
	    spacing.setLineRule(STLineSpacingRule.AUTO);
	    spacing.setLine(BigInteger.valueOf(240));
	}

	private static void generateCodesFromMaps(Map<CellPosition<String, Integer>, Info> leftMap,
			Map<CellPosition<String, Integer>, Info> rightMap, int fontSize, String outputPath) {
		// Either map is okay for iteration
		for (CellPosition<String, Integer> position : leftMap.keySet()) {
			Info leftInfo = leftMap.get(position);
			Info rightInfo = rightMap.get(position);
			XWPFDocument document = new XWPFDocument();
			XWPFParagraph paragraph = document.createParagraph();
			setSingleLineSpacing(paragraph);

			// Narrow margin
			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
			CTPageMar pageMar = sectPr.addNewPgMar();
			pageMar.setLeft(BigInteger.valueOf(720L));
			pageMar.setTop(BigInteger.valueOf(720L));
			pageMar.setRight(BigInteger.valueOf(720L));
			pageMar.setBottom(BigInteger.valueOf(720L));

			// Alignment
			paragraph.setAlignment(ParagraphAlignment.CENTER);

			// Table
			XWPFTable table = document.createTable(1, 2);
			// Auto-fit table
			CTTbl table2 = table.getCTTbl();
			CTTblPr pr = table2.getTblPr();
			CTTblWidth tblW = pr.getTblW();
			tblW.setW(BigInteger.valueOf(5000));
			tblW.setType(STTblWidth.PCT);
			pr.setTblW(tblW);
			pr.unsetTblBorders();
			table2.setTblPr(pr);

			XWPFTableCell leftCell = table.getRow(0).getCell(0);
			XWPFParagraph leftParagraph = leftCell.addParagraph();
			setSingleLineSpacing(leftParagraph);
			addParagraphFromInfo(leftInfo, leftParagraph, fontSize);
			XWPFTableCell rightCell = table.getRow(0).getCell(1);
			XWPFParagraph rightParagraph = rightCell.addParagraph();
			setSingleLineSpacing(rightParagraph);
			addParagraphFromInfo(rightInfo, rightParagraph, fontSize);

			try {
				document.write(new FileOutputStream(String.format("%s/%s.docx", outputPath, position)));
				document.close();
			} catch (IOException e) {
				ErrorReporter.report(e);
			}
		}
	}

	public static void workWithTwoMusics() {
		writeToFile = visualize = false;
		sheetExclusion = frameSimplification = true;
		int fps = 8;
		int numRows = 10, numCols = 10;
		int rowsPerPage = 8;
		int totalSheets = -1;
		int framesPerRow = 4;
		int gap = 1;
		int fontSize = 10;
		// When in doubt, gather all data into maps first
		workWithPattern("Rocketeer", fps, numRows, numCols, rowsPerPage, totalSheets, framesPerRow, gap, fontSize);
		Map<CellPosition<String, Integer>, Info> rocketeerMap = currentMap;
		framesPerRow = 3;
		workWithPattern("ET", fps, numRows, numCols, rowsPerPage, totalSheets, framesPerRow, gap, fontSize);
		Map<CellPosition<String, Integer>, Info> etMap = currentMap;
		String outputPath = "Output/Both";
		createPathIfNecessary(outputPath);
		generateCodesFromMaps(etMap, rocketeerMap, fontSize, outputPath);
	}

	/**
	 * Create given path if necessary
	 * 
	 * @param path
	 */
	public static void createPathIfNecessary(String path) {
		File output = new File(path);
		if (!output.exists()) {
			try {
				System.out.println("Create directory " + Files.createDirectory(output.toPath()));
			} catch (IOException e) {
				ErrorReporter.report(e);
			}
		}
	}

	private static void setTextWithBold(XWPFRun run, String text) {
		if (text.matches("\\<b\\>.+\\<\\/b\\>")) {
			text = text.substring(3, text.length() - 4);
			text = text.toUpperCase();
		}
		run.setText(text, run.getTextPosition());
	}

	public static void setTextWithTab(XWPFRun run, String line) {
		// XWPFRun run2 = ((XWPFParagraph)(run.getParent())).insertNewRun(0);
		if (line.contains("\t")) {
			String[] tlines = line.split("\t");
			setTextWithBold(run, tlines[0]);
			for (int j = 1; j < tlines.length; j++) {
				run.addTab();
				setTextWithBold(run, tlines[j]);
			}
		} else
			setTextWithBold(run, line);
	}

}
