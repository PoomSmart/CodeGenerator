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

import javax.swing.JOptionPane;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;



public class Generator {

	public static boolean writeToFile = true;
	public static boolean visualize = true;
	public static boolean sheetExclusion = false;
	public static boolean frameSimplification = false;
	public static boolean syncSimulation = true;
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
		XSSFCellStyle style = cell.getCellStyle();
		String colorString="";
		if(style!=null){
			XSSFColor color = style.getFillForegroundXSSFColor();
			if(color!=null){
				colorString = color.getARGBHex();
				if(colorString.equals("FF00B0F0")) //not include
					return;
			}
			else
				colorString = "FFFFFFFF"; //down (blank cell)
		}
		else
			colorString = "Error";
		int r = numRows - i - 1;
		int c = numCols - j - 1;
		CellPosition<String, Integer> pos = new CellPosition<String, Integer>(c, r);
		if (!map.containsKey(pos))
			map.put(pos, new Info(pos, numberOfSteps, String.format("%dx%d", numRows, numCols), currentMusic));
		if(colorString.equals("Error"))
			if(sheetNumber==5||sheetNumber==7)
				for(int k = 0 ; k<2;k++)
					map.get(pos).addAction(new Action(Action.Type.Error, sheetNumber));
			else
				for(int k = 0 ; k<4;k++)
					map.get(pos).addAction(new Action(Action.Type.Error, sheetNumber));
		else
			if(sheetNumber==5||sheetNumber==7)
				for(int k = 0 ; k<2;k++)
					map.get(pos).addAction(new Action(colorString, sheetNumber));
			else
				for(int k = 0 ; k<4;k++)
					map.get(pos).addAction(new Action(colorString, sheetNumber));
				
		
		
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
				FileOutputStream stream = new FileOutputStream(String.format("%s/%s.docx", outputPath, pos));
				doc.write(stream);
				doc.close();
				stream = null;
				doc = null;
			} catch (IOException e) {
				ErrorReporter.report(e);
			}
		}
	}

	private static void setFontSize(XWPFRun run, int fontSize) {
		run.setFontFamily("Consolas");
		run.setFontSize(fontSize);
	}

	private static void addParagraphFromData(XWPFRun run, String data, XWPFParagraph paragraph, int fontSize) {
		if (data.isEmpty())
			data = " ";
		setFontSize(run, fontSize);
		if (data.contains("\n")) {
			String[] lines = data.split("\n");
			setTextWithTab(run, lines[0]);
			for (int i = 1; i < lines.length; i++) {
				run.addBreak();
				setTextWithTab(run, lines[i]);
			}
		} else
			setText(run, data);
	}

	private static void addParagraphFromInfo(Info info, XWPFParagraph paragraph, int fontSize) {
		XWPFRun run = paragraph.createRun();
		String data = info.toString(false);
		addParagraphFromData(run, data, paragraph, fontSize);
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
		try {
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("Input/" + filename + ".xlsx"));
			boolean stop = false;
			boolean advance = false;
			int totalFrames = 0;
			if (totalSheets == -1)
				totalSheets = wb.getNumberOfSheets();
			for (int ns = 0; ns < totalSheets; ns++) {
				XSSFSheet sheet = wb.getSheetAt(ns);
				// Special case: Rocketeer at page ns = 4 has 10 rows
				int framesPerPage = framesPerRow * (rowsPerPage + ((ns == 4 && filename.equals("Rocketeer")) ? 2 : 0));
				for (int fn = 0; fn < framesPerPage; fn++) {
					advance = false;
					int foi = (fn / framesPerRow) * (gap + numRows);
					int foj = (fn % framesPerRow) * (gap + numCols);
					totalFrames++;
					for (int i = 0; i < numRows; i++) {
						XSSFRow row = sheet.getRow(foi + i);
						// If the current row is null, it is possible that we read to the end of the animation. So we
						// stop here
						if (row == null) {
							totalFrames--;
							if (ns == totalSheets - 1)
								stop = true;
							// This page doesn't fill rows, advance to next page
							advance = true;
							break;
						}
						for (int j = 0; j < numCols; j++) {
							XSSFCell cell = row.getCell(foj + j);
							if (cell != null)
								haveAction(map, cell, i, j, framesPerRow, numRows, numCols, ns + 1);
						}
					}
					if (stop || advance)
						break;
				}
				if (stop)
					break;
			}
			currentMap = map;
			if (visualize) {
				CodeVisualizer vis = new CodeVisualizer(map, new Dimension(numCols, numRows), fps, totalFrames);
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
			wb = null;
		} catch (EncryptedDocumentException | IOException e) {
			ErrorReporter.report(e);
		}
		createOutput(filename, map, fontSize);
	}

	private static void setSingleLineSpacing(XWPFParagraph paragraph) {
		paragraph.setSpacingBeforeLines(0);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfterLines(0);
		paragraph.setSpacingAfter(0);
		CTPPr ppr = paragraph.getCTP().getPPr();
		if (ppr == null)
			ppr = paragraph.getCTP().addNewPPr();
		CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
		spacing.setAfter(BigInteger.valueOf(0));
		spacing.setBefore(BigInteger.valueOf(0));
		spacing.setLineRule(STLineSpacingRule.AUTO);
		spacing.setLine(BigInteger.valueOf(240));
	}

	private static boolean hasAnyAction(String text) {
		for (Action.Type type : Action.boldableTypes()) {
			String stype = type.toString();
			if (text.lastIndexOf(stype) != -1)
				return true;
		}
		return text.lastIndexOf("Up") != -1;
	}

	private static void reapplyBold(XWPFTableCell cell) {
		String content = cell.getText();
		cell.removeParagraph(0);
		for (String subcontent : content.split("\n")) {
			XWPFParagraph paragraph = cell.addParagraph();
			setSingleLineSpacing(paragraph);
			XWPFRun run = paragraph.createRun();
			if (subcontent.contains("Group") || subcontent.contains("Wave")) {
				run.setUnderline(UnderlinePatterns.SINGLE);
				if (subcontent.contains("Wave"))
					run.setItalic(true);
			} else if (hasAnyAction(subcontent))
				run.setBold(true);
			addParagraphFromData(run, subcontent, paragraph, 10);
		}
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

	private static void setText(XWPFRun run, String text) {
		run.setText(text, run.getTextPosition());
	}

	public static void setTextWithTab(XWPFRun run, String line) {
		if (line.contains("\t")) {
			String[] tlines = line.split("\t");
			setText(run, tlines[0]);
			for (int j = 1; j < tlines.length; j++) {
				run.addTab();
				setText(run, tlines[j]);
			}
		} else
			setText(run, line);
	}

}
