package com.github.sylphlike.framework.utils.excel.core;


import com.github.sylphlike.framework.utils.general.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.github.sylphlike.framework.utils.excel.ExcelException;
import com.github.sylphlike.framework.utils.excel.support.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * POI excel文件导出，支持超大数据量
 * <p> 初始化excel文件 -- 添加数据 -- 渲染到客户端 --清理临时文件. new ExcelExport(sysUser1,list.size()).setDataList(list).write("D:\\test.xlsx").dispose(); </p>
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class ExcelExport<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**sheet个数*/
    private int sheetCount = 1;

    /**顶部title，header 占用总行数*/
    private int topOccupancyIndex;


    /**excel单个sheet最大行数*/
    private static final int SHEET_CAPACITY = 500000;

    /** 内存中缓存工作薄行数数*/
    private static final int MEMORY_CACHE_ROW = 50000;


    /**工作薄样式*/
    private final Map<String, CellStyle> styleMap = Maps.newLinkedHashMap();

    /**sheet个数*/
    private final List<Sheet> sheetList = Lists.newArrayList();


    private String sheetName;
    private String title;
    private short  titleHeight;
    private short  headerHeight;
    private short  contentHeight;
    private final List<Map<String,Object>> property = Lists.newArrayList();



    /**
     * 07版本已上版本 低内存占用工作空间创建方式
     */
    private final SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(MEMORY_CACHE_ROW);



    public ExcelExport(T excelModel,int exportDataSize) throws Exception {
        if(null == excelModel || exportDataSize == 0){
            throw new ExcelException("需要导出的数据为空");
        }


        if(exportDataSize > SHEET_CAPACITY){
            sheetCount = exportDataSize % SHEET_CAPACITY == 0? exportDataSize / SHEET_CAPACITY : exportDataSize / SHEET_CAPACITY + 1;
        }



        dealStyles(sxssfWorkbook,excelModel);
        dealProperty(excelModel);

        for (int x =0; x < sheetCount; x ++){
            topOccupancyIndex = 0;
            Sheet sheet = sxssfWorkbook.createSheet(String.format("%s%s%s", sheetName, "_", x));
            if(StringUtils.isNotEmpty(title)){
                //title
                Row titleRow = sheet.createRow(topOccupancyIndex++);
                titleRow.setHeightInPoints(20);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellStyle(styleMap.get("title"));
                titleCell.setCellValue(title);
                sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),
                        titleRow.getRowNum(), property.size() - 1));

                titleRow.setHeight(titleHeight);
            }

            //header
            Row headerRow = sheet.createRow(topOccupancyIndex++);
            for (int i = 0; i < property.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellStyle(styleMap.get("headerStyle"));
                cell.setCellValue(property.get(i).get("name").toString());

                sheet.setColumnWidth(i, (Integer)property.get(i).get("columnWidth"));
                headerRow.setHeight(headerHeight);
            }

            sheetList.add(sheet);
        }


        logger.info("【FW-utils】导出excel文件,初始化excel文件完成");
        logger.info(MessageFormat.format("【FW-utils】导出的数据长度为[{0}]", exportDataSize));
        logger.info(MessageFormat.format("【FW-utils】导出的excel sheet总页数为[{0}],单个sheet页最大行数[{1}]",sheetCount, SHEET_CAPACITY));

    }

    private void dealStyles(SXSSFWorkbook sxssfWorkbook, T excelModel) throws Exception {


        boolean annotationPresent = excelModel.getClass().isAnnotationPresent(ExcelModel.class);
        if(annotationPresent){
            ExcelModel annotation = excelModel.getClass().getAnnotation(ExcelModel.class);
            title = annotation.title();
            sheetName = annotation.sheetName();
            if(StringUtils.isEmpty(sheetName)){
                sheetName = excelModel.getClass().getName();
            }
            titleHeight  = annotation.titleHeight();
            headerHeight = annotation.headerHeight();
            contentHeight= annotation.contentHeight();


            Class<?> documentStyles = annotation.documentStyles();
            Object newInstance = documentStyles.getDeclaredConstructor().newInstance();
            Method titleStyleMethod = documentStyles.getDeclaredMethod("titleStyle",CellStyle.class,Font.class);
            Object titleStyle = titleStyleMethod.invoke(newInstance, sxssfWorkbook.createCellStyle(), sxssfWorkbook.createFont());

            Method method = documentStyles.getDeclaredMethod("headerStyle",CellStyle.class,Font.class);
            Object headerStyle = method.invoke(newInstance, sxssfWorkbook.createCellStyle(), sxssfWorkbook.createFont());

            Method contentStyleMethod = documentStyles.getDeclaredMethod("contentStyle",CellStyle.class,Font.class);
            Object contentStyle = contentStyleMethod.invoke(newInstance, sxssfWorkbook.createCellStyle(), sxssfWorkbook.createFont());

            styleMap.put("title", (CellStyle)titleStyle);
            styleMap.put("headerStyle", (CellStyle)headerStyle);
            styleMap.put("contentStyle", (CellStyle)contentStyle);

        }else {
            throw new ExcelException("未启用对应注解");
        }






    }

    private void dealProperty(T excelModel) {
        Field[] fields = excelModel.getClass().getDeclaredFields();

        for (Field field:fields) {
            boolean annotationPresent = field.isAnnotationPresent(ExcelModelProperty.class);
            if(annotationPresent) {
                Map<String,Object> tempProperty = Maps.newLinkedHashMap();
                ExcelModelProperty annotation = field.getAnnotation(ExcelModelProperty.class);
                String name = annotation.name();
                int columnWidth = annotation.columnWidth();

                name= StringUtils.isEmpty(name) ? field.getName() : name;

                tempProperty.put("name",name);
                tempProperty.put("columnWidth",columnWidth);

                property.add(tempProperty);
            }
        }
    }





    public ExcelExport setDataList(List<T> list) throws ExcelException {

        for (int x =0; x < sheetCount; x ++){
            List<T> sheetExportList;
            if(x == sheetCount -1){
                sheetExportList = list.subList(x * SHEET_CAPACITY, list.size());
            }else{
                sheetExportList = list.subList(x * SHEET_CAPACITY, (x+1) * SHEET_CAPACITY);
            }
            dealSheetData(sheetExportList,sheetList.get(x));

        }

        return this;


    }

    private void dealSheetData(List<T> sheetExportList, Sheet sheet) throws ExcelException {

        try {
            /* 数据内容起始号行*/
            int contentStartIndex = topOccupancyIndex;
            for(Object obj:sheetExportList){
                int cellNum = 0;
                Row row =  sheet.createRow(contentStartIndex++);
                for(Field field:obj.getClass().getDeclaredFields()){
                    boolean annotationPresent = field.isAnnotationPresent(ExcelModelProperty.class);
                    if(!annotationPresent) {continue;}
                    field.setAccessible(true);
                    ExcelModelProperty annotation = field.getAnnotation(ExcelModelProperty.class);
                    String dateFormat = annotation.dateFormat();
                    Class<?> aClass = annotation.enumFormat();
                    boolean enumTransform = annotation.enumTransform();
                    if(enumTransform){
                        Object o = field.get(obj);
                        Object invoke = null;
                        if(null != o){
                            Class<?> enumClass = o.getClass();
                            Method method = enumClass.getDeclaredMethod("desc");
                            invoke = method.invoke(o);
                        }
                        addCall(row,cellNum++,null == invoke ? "--" : invoke);
                    } else if(!StringUtils.isEmpty(dateFormat)){
                        Object va = field.get(obj);
                        if(va instanceof LocalDateTime){
                            addCall(row,cellNum++, DateUtils.date2String((LocalDateTime) va,dateFormat));
                        }else {
                            throw new ExcelException("转换类型失败");
                        }
                    }else if(!aClass.equals(void.class)){
                        if(aClass.isEnum()){
                            Object var = field.get(obj);
                            Object descriptionByByCode = Util.getDescriptionByByCode(var, aClass, annotation.enumFormatAttributes());

                            addCall(row,cellNum++,null == descriptionByByCode ? "--" : descriptionByByCode);

                        }else {
                            throw new ExcelException("转换enum类型失败");
                        }
                    }else {
                        Object var=field.get(obj);

                        addCall(row,cellNum++, null == var? "--": var);
                    }
                }
                row.setHeight(contentHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    private void addCall(Row row, int i, Object va) {
        Cell cell = row.createCell(i);
        cell.setCellValue(va.toString());
        cell.setCellStyle(styleMap.get("contentStyle"));
    }


    /**
     * 输出到客户端 web
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param response     响应流
     * @param fileName     编码后的文件名
     * @return  com.github.sylphlike.framework.utils.excel.core.ExcelExport
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public ExcelExport write(HttpServletResponse response, String fileName) throws IOException{
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ fileName);
        sxssfWorkbook.write(response.getOutputStream());
        return this;
    }


    /**
     * 输出到客户端 文件流
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param fileFullPath  文件地址路径
     * @return  com.github.sylphlike.framework.utils.excel.core.ExcelExport
     * @author  Gopal.pan
     */
    public ExcelExport write(String fileFullPath){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(fileFullPath));

            try {
                sxssfWorkbook.write(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;

    }




    /**
     * 清理临时文件
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @return  com.github.sylphlike.framework.utils.excel.core.ExcelExport
     * @author  Gopal.pan
     */
    public ExcelExport dispose(){
        sxssfWorkbook.dispose();
        logger.info("【FW-utils】导出excel文件,excel文件导出完成");
        return this;
    }



}
