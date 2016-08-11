import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class GeneralTester {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*
		 * XWPFDocument document = new XWPFDocument(new FileInputStream("TwoColumns.docx")); XWPFParagraph tmpParagraph
		 * = document.getParagraphs().get(0); System.out.println(document.getDocument().);
		 * 
		 * for (int i = 0; i < 100; i++) { XWPFRun tmpRun = tmpParagraph.createRun(); tmpRun.setText("Testing Testing");
		 * tmpRun.setFontSize(14); System.out.println(tmpRun.getTextPosition()); } document.write(new
		 * FileOutputStream("TwoColumns2.docx")); document.close();
		 */
		XWPFDocument document = new XWPFDocument();
		XWPFParagraph paragraph = document.createParagraph();
		
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
		table2.setTblPr(pr);
		
		table.getRow(0).getCell(0).setText("FOO1");
		table.getRow(0).getCell(1).setText("FOO2");
		document.write(new FileOutputStream("TwoColumns2.docx"));
		document.close();
	}

}
