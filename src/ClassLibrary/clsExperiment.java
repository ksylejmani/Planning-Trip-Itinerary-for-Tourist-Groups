package ClassLibrary;
import java.io.File; 
import jxl.*; 
import jxl.write.*;


public class clsExperiment {

    public  WritableWorkbook workbook;
    public  WritableSheet sheet;
    public void CreateFile(String OutputFileName)
    {

     try{
          workbook = Workbook.createWorkbook(new File(OutputFileName+".xls"));
        }
    catch ( Exception ex ){
        }
    }
    public void CreateSheet(String SheetName,int SheetIndex)
    {

     try{
        sheet = workbook.createSheet(SheetName, SheetIndex);
        }
    catch ( Exception ex ){
        }
    }

public  void WriteColumnTitle( String FirstColumn,
                                int FirstColumnIndeks,
                                int FirstRowIndeks)
{

     try{
            Label title1 = new Label(FirstColumnIndeks, FirstRowIndeks, FirstColumn);
            sheet.addCell(title1);
        }
    catch ( Exception ex ){
        }
   }
    public  void WriteRowData(int SheetIndeks,int columnIndeks,int rowIndeks,double cellValue)
    {
     try{
            jxl.write.Number number = new jxl.write.Number(columnIndeks, rowIndeks, cellValue);
            //sheet.addCell(number);
            workbook.getSheets()[SheetIndeks].addCell(number);
        }
    catch ( Exception ex ){
        }

    }
    public  void WriteRowData(int SheetIndeks,int columnIndeks,int rowIndeks,String cellValue)
    {
     try{
            jxl.write.Label label = new jxl.write.Label(columnIndeks, rowIndeks, cellValue);
//            sheet.addCell(label);
            workbook.getSheets()[SheetIndeks].addCell(label);
        }
    catch ( Exception ex ){
        }

    }
    public  void closeExcelFile()
    {
     try{
            workbook.write();
            workbook.close();
        }
    catch ( Exception ex ){
        }
    }

}
