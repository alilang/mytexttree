package com.mytexttree.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OfficeUtil {

    public static void readXLSXFile(String excelPath) {
        InputStream is = null;
        XSSFWorkbook wb = null;

        try{
            is = new FileInputStream(excelPath);
            wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();
                Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        System.out.print(cell.getStringCellValue() + " ");
                    } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        System.out.print(cell.getNumericCellValue() + " ");
                    }
                }
                System.out.println();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(null != wb)
            {
                try {
                    wb.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String getDateString(Date date)
    {
        String dateStr = null;
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wensday", "Thursday", "Friday", "Saturday"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = sdf.format(date);
        
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
        {
            w = 0;
        }
        
        dateStr = dateStr + " (" + weekDays[w] + ")";
        return dateStr;
    }
    
    
    public static String[] getDateArray()
    {
        String[] ret = new String[2];
        Date date = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -7);

        ret[0] = getDateString(date);
        ret[1] = getDateString(cal.getTime());
        return ret;
    }

    public static void writeXLSXFile(String excelPath){
        String sheetName = "DailyReport";// name of sheet
        FileOutputStream fos = null;
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("和并列");
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(region);
        XSSFCellStyle style= wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);

        try{
            fos = new FileOutputStream(excelPath);
            wb.write(fos);
            fos.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                wb.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(null != fos)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        //writeXLSXFile("d://zzz.xlsx");
        //readXLSXFile("d://zzz.xlsx");
        String[] date = getDateArray();
        System.out.println(date[0]);
        System.out.println(date[1]);
    }

}
