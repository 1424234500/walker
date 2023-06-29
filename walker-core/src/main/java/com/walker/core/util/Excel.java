package com.walker.core.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Excel {
	protected static Logger log = LoggerFactory.getLogger(Excel.class);

	private final File file;

	public Excel(String filepath) {
		this.file = new File(filepath);
	}

	private static Workbook getWorkBook(File file) {
		String fileName = file.getName();
		Workbook wb = null;
		try {
			InputStream is = new FileInputStream(file);
			if (fileName.endsWith("xls")) {//2003
				wb = new HSSFWorkbook(is);
			} else if (fileName.endsWith("xlsx")) {//2007
				wb = new XSSFWorkbook(is);
			}
		} catch (IOException e) {
			log.error("getExcel " + file.getAbsolutePath() + " " + e.getMessage(), e);
		}
		return wb;
	}

	public static String getCellValue(Cell cell) {
		String cellValue = null;
		if (cell != null) {
			//x num 1 -> 1.0
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
			}
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					cellValue = String.valueOf(cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					cellValue = String.valueOf(cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN: //Boolean
					cellValue = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA://fun
					cellValue = String.valueOf(cell.getCellFormula());
					break;
				case Cell.CELL_TYPE_BLANK:
					cellValue = "";
					break;
				case Cell.CELL_TYPE_ERROR:
					cellValue = "errchar";
					break;
				default:
					cellValue = "errtype";
					break;
			}
		}
		return cellValue;
	}

	private static XSSFCellStyle getStyle(XSSFWorkbook wb, boolean title) {
		// font
		XSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) (title ? 12 : 10));
		if (title)
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("Consolars");
		XSSFCellStyle style = wb.createCellStyle();
		//边框 Border
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.RED.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setFont(font);
		style.setWrapText(false);//自动换行
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT); //水平居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);//垂直居中
		return style;
	}

	private static XSSFCellStyle getStyleTop(XSSFWorkbook wb) {
		return getStyle(wb, true);
	}

	private static XSSFCellStyle getStyle(XSSFWorkbook wb) {
		return getStyle(wb, false);
	}

	public String save(ExcelModel excelModel) {
		long t = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder("excel save listList > " + file.getAbsolutePath() + " ");
		if (file.exists()) {
			sb.append("delete " + file.getAbsolutePath());
//			log.warn(sb.toString());
			file.delete();
		}
		if (excelModel == null) {
			sb.append(" excelModel is null ");
		} else if (excelModel.size() == 0) {
			sb.append(" excelModel size=0 ");
			log.warn(sb.toString());
		} else {
			sb.append(" excelModel size " + excelModel.size());

			FileOutputStream os = null;
			try {
				os = new FileOutputStream(file);

				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFCellStyle titleStyle = getStyleTop(wb);
				XSSFCellStyle style = getStyle(wb);

				for (int i = 0; i < excelModel.size(); i++) {
					String sheetName = excelModel.getSheetName(i);
					List<List<String>> listList = excelModel.getSheet(sheetName);
					int fff = -1;
					XSSFSheet sheet = wb.createSheet(sheetName);
					for (int rowNum = 0; listList != null && rowNum < listList.size(); rowNum++) {
						List<?> list = listList.get(rowNum);
						if (list != null) {
							XSSFRow row = sheet.createRow(rowNum);
							if (list.size() > 0) {
								for (int colNum = 0; list != null && colNum < list.size(); colNum++) {
									XSSFCell cell = row.createCell(colNum);
									cell.setCellType(HSSFCell.CELL_TYPE_STRING);
									cell.setCellValue(String.valueOf(list.get(colNum)));
									XSSFCellStyle style1 = (XSSFCellStyle) (rowNum == 0 ? titleStyle.clone() : style.clone());
									if (fff < 0) {
										style1.setFillBackgroundColor(HSSFColor.ROSE.index);
										fff = 0 - fff;
									}
									cell.setCellStyle(style1);
								}
							} else {
								fff = -1;
							}
						}
					}
				}
				wb.write(os);
			} catch (Exception e) {
				sb.append(" " + e.getMessage());
				log.error(sb.toString(), e);
			} finally {
				if (os != null) {
					try {
						os.flush();
						os.close();
					} catch (IOException e) {
						sb.append(" " + e.getMessage());
						log.error(sb.toString(), e);
					}
				}
				sb.append(" cost " + (System.currentTimeMillis() - t));
				log.info(sb.toString());
			}
		}

		return sb.toString();
	}

	public ExcelModel read() {
		ExcelModel res = new ExcelModel();
		StringBuilder sb = new StringBuilder("excel read < " + file.getAbsolutePath() + " ");
		try {
			if (!file.isFile()) {
				sb.append(" read no exitsts");
				log.error(sb.toString());
			} else {
				Workbook wb = getWorkBook(file);
				if (wb != null) {
					for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
						List<List<String>> listList = new ArrayList<>();

						//all sheet
						Sheet sheet = wb.getSheetAt(sheetNum);
						if (sheet == null) {
							sb.append(" getSheetAt num:" + sheetNum + " null");
							continue;
						}
						String sheetName = sheet.getSheetName();

						for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
							Row row = sheet.getRow(rowNum);
							List<String> line = new ArrayList<>();
							if (row == null) {
								sb.append(" getRow num:" + sheetNum + " null");
							} else {
								int toCol = row.getLastCellNum();
								for (int col = 0; col < toCol; col++) {
									line.add(getCellValue(row.getCell(col)));
								}
							}
							listList.add(line);
						}

						res.setSheetData(sheetName, listList);
					}
				} else {
					sb.append(" read error null");
				}
			}
		} finally {
			log.info(sb.toString());
		}

		return res;
	}

}
