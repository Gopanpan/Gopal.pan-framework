package com.github.sylphlike.framework.utils.general;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * 二维码生成工具类
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class QRCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRCode.class);

    private static final int WIDTH = 350;
    private static final int HEIGHT = 350;
    private static final int FONT = 21;

    /** 默认字体颜色 */
    private static final Color FONT_COLOR = Color.black;
    /** logo默认边框宽度*/
    public static final int LOGO_BORDER = 3;
    /** logo默认边框颜色*/
    public static final Color LOGO_BORDER_COLOR = Color.WHITE;

    public static final String PICTURE_TYPE = "PNG";



    // 默认二维码参数设定
    private static final Map<EncodeHintType, Object> HINTS = new HashMap<>() {
        private static final long serialVersionUID = 1L;
        {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.CHARACTER_SET, "utf-8");
            put(EncodeHintType.MARGIN, 1);                                //二维码内容与图片边框边距
        }
    };






    /**
     * 生成二维码图片,保存到指定文件目录下
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents    二维码内容
     * @param savePath    二维码图片存储地址
     * @author  Gopal.pan
     */
    public static void QRCodeFile(String contents, String savePath){
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HINTS);
            Path path = FileSystems.getDefault().getPath(savePath);
            MatrixToImageWriter.writeToPath(bitMatrix, PICTURE_TYPE, path);
        } catch (Exception e) {
            LOGGER.error("【FW-utils】生成二维码时系统异常",e);
        }
    }




    /**
     * 生成二维码图片,使用默认的宽高
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents   二维码内容
     * @return   byte[]
     * @author   Gopal.pan
     */
    public static byte[] QRCodeByte(String contents) {
        return QRCodeByte(contents,WIDTH,HEIGHT);
    }




    /**
     * 生成二维码图片
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents    二维码内容
     * @param width       二维码宽度
     * @param height      二维码高度
     * @return   byte[]   生成二维码字节流
     * @author   Gopal.pan
     */
    public static byte[] QRCodeByte(String contents, int width, int height) {
        byte[] pngData = new byte[0];
        try(ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, width, height,HINTS);

            MatrixToImageWriter.writeToStream(bitMatrix, PICTURE_TYPE, pngOutputStream);
            pngData = pngOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("【FW-utils】生成二维码时系统异常",e);
        }
        return pngData;
    }



    /**
     * 生成二维码图片带logo
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents  二维码内容
     * @param logoBytes  logo图片字节数组
     * @return   byte[]
     * @author   Gopal.pan
     */
    public static byte[] QRCodeByte(String contents,byte[] logoBytes) {
       return QRCodeByte(contents,WIDTH,HEIGHT,logoBytes);
    }


    /**
     * 生成二维码图片带logo
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents    二维码内容
     * @param width       二维码宽度
     * @param height      二维码高度
     * @param logoBytes   logo图片字节数组
     * @return   byte[]
     * @author   Gopal.pan
     */
    public static byte[] QRCodeByte(String contents, int width, int height,byte[] logoBytes) {
        byte[] bytes = QRCodeByte(contents, width, height);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(InputStream contentsStream = new ByteArrayInputStream(bytes,0,bytes.length);
            ByteArrayInputStream logoFileStream= new ByteArrayInputStream(logoBytes, 0, logoBytes.length)){

            BufferedImage image = ImageIO.read(contentsStream);
            Graphics2D g = image.createGraphics();
            BufferedImage logo = ImageIO.read(logoFileStream);
            g.setStroke(new BasicStroke(LOGO_BORDER));
            g.setColor(LOGO_BORDER_COLOR);
            g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
            g.drawRoundRect(width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, 15, 15);

            g.dispose();
            logo.flush();
            image.flush();
            ImageIO.write(image,PICTURE_TYPE,out);
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("【FW-utils】生成二维码时系统异常",e);
        }
        return null;
    }



    /**
     * 生成二维码图片
     * @param contents    二维码内容
     * @param title       二维码title
     * @return   byte[]   生成二维码字节流
     */
    public static byte[] QRCodeByte(String contents,String title) {
        try {
            return pressContents(contents,title);
        } catch (Exception e) {
            LOGGER.error("【FW-utils】生成二维码时系统异常",e);
        }
        return null;
    }






    /**
     * 生成二维码图片
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param contents  二维码内容
     * @param title     二维码title
     * @param filePath  图片保存路径地址
     * @author  Gopal.pan
     */
    public static void QRCodeFile(String contents,String title,String filePath) {

        try {
            byte[] bytes = pressContents(contents, title);

            FileUtils.byte2Image(bytes,filePath);
        } catch (Exception e) {
            LOGGER.error("【FW-utils】生成二维码时系统异常",e);
        }
    }





    private static byte[] pressContents(String contents, String title) throws IOException, WriterException {
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        InputStream byteArrayInputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //HINTS.put(EncodeHintType.MARGIN, 4);

        try{
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, WIDTH, HEIGHT + 20, HINTS);

            MatrixToImageWriter.writeToStream(bitMatrix, PICTURE_TYPE, pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            byteArrayInputStream = new ByteArrayInputStream(pngData,0,pngData.length);
            BufferedImage src = ImageIO.read(byteArrayInputStream);

            int imageW = src.getWidth(null);
            int imageH = src.getHeight(null);
            BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(src, 0, 0, imageW, imageH, null);
            graphics.setColor(FONT_COLOR);
            graphics.setFont(new Font("思源宋体", Font.BOLD, FONT));

            int startX = graphics.getFontMetrics().stringWidth(title), startY = 27;
            graphics.drawString(title, WIDTH / 2 -  startX / 2, startY);
            graphics.dispose();

            ImageIO.write(image,PICTURE_TYPE,out);

            return out.toByteArray();
        }finally {
            pngOutputStream.close();
            if(byteArrayInputStream != null){
                byteArrayInputStream.close();
            }
            out.close();
        }
    }



}
