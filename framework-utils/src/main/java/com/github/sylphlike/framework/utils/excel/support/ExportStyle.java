package com.github.sylphlike.framework.utils.excel.support;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;


/**
 * 导出Excel样式接口
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public interface ExportStyle{


    /**
     * 标题样式
     * @param cellStyle  CellStyle
     * @param font       Font
     * @return  org.apache.poi.ss.usermodel.CellStyle
     * @author  Gopal.pan
     */
    CellStyle titleStyle(CellStyle cellStyle, Font font);




    /**
     * 列表头样式
     * @param cellStyle  CellStyle
     * @param font       Font
     * @return  org.apache.poi.ss.usermodel.CellStyle
     * @author  Gopal.pan
     */
    CellStyle headerStyle(CellStyle cellStyle, Font font);



    /**
     * 内容样式
     * @param cellStyle  CellStyle
     * @param font       Font
     * @return  org.apache.poi.ss.usermodel.CellStyle
     * @author  Gopal.pan
     */
    CellStyle contentStyle(CellStyle cellStyle, Font font);



}
