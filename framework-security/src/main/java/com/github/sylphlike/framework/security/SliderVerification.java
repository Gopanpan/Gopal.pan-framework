package com.github.sylphlike.framework.security;

import com.github.sylphlike.framework.security.vo.SliderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑块认证
 * <p>目标图片大小需要590 * 360 目标图片文件后缀为.jpg 模板图片文件后缀为.png </p>
 * <p>  time 17:56 2010/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class SliderVerification {

    private static final Logger LOGGER = LoggerFactory.getLogger(SliderVerification.class);

    private static final int ORI_WIDTH = 590;  //源文件宽度
    private static final int MIN_X_DISTANCE = 50;
    private static final int Y_DISTANCE = 5;

    private static final String RESULT_IMAGE_TYPE = "png";
    private static final String IMAGE_TYPE_JPG = "jpg";






    /**
     * 根据模板切图
     * @return   com.github.sylphlike.security.vo.SliderVO
     * @author   Gopal.pan
     */
    public static SliderVO pictureTemplatesCut()  {
        InputStream targetFileInputStream = null;
        ByteArrayOutputStream outputStream = null;
        SliderVO sliderVO = new SliderVO();
        try {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int templateNo = random.nextInt(4) + 1;
            int targetNo = random.nextInt(20) + 1;

            String templatePath =  SliderVerification.class.getClassLoader().getResource("static/templates/" + templateNo + ".png").getPath();
            String targetPath = SliderVerification.class.getClassLoader().getResource("static/targets/" + targetNo + ".jpg").getPath();
            File templateFile = new File(templatePath);
            File targetFile = new File(targetPath);


            targetFileInputStream = new FileInputStream(targetFile);

            // 模板图
            BufferedImage imageTemplateBuffered = ImageIO.read(templateFile);
            int templateImageWidth = imageTemplateBuffered.getWidth();
            int templateImageHeight = imageTemplateBuffered.getHeight();


            //初始化小图
            BufferedImage smallImageBuffered = new BufferedImage(templateImageWidth, templateImageHeight, imageTemplateBuffered.getType());
            Graphics2D graphics = smallImageBuffered.createGraphics();
            graphics.setBackground(Color.white);

            int xDistance =  generateCutoutCoordinates(templateImageWidth);

            BufferedImage targetImageNoDeal = getTargetArea(xDistance, templateImageWidth, templateImageHeight, targetFileInputStream);


            // 根据模板图片抠图
            cutPictureByTemplate(targetImageNoDeal, imageTemplateBuffered, smallImageBuffered);

            // 设置“抗锯齿”的属性
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            graphics.drawImage(smallImageBuffered, 0, 0, null);
            graphics.dispose();

            outputStream = new ByteArrayOutputStream();
            ImageIO.write(smallImageBuffered, RESULT_IMAGE_TYPE, outputStream);
            byte[] smallImageByte = outputStream.toByteArray();


            // 源图生成遮罩
            BufferedImage oriImage = ImageIO.read(targetFile);
            BufferedImage oriPictureByTemplate = oriPictureByTemplate(oriImage, imageTemplateBuffered, xDistance);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(oriPictureByTemplate, RESULT_IMAGE_TYPE, outputStream);
            byte[] bigImageByte = outputStream.toByteArray();


            sliderVO.setSmallImageByte(smallImageByte);
            sliderVO.setBigImageByte(bigImageByte);
            sliderVO.setXDistance(xDistance);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(targetFileInputStream != null)
                    targetFileInputStream.close();
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sliderVO;


    }



    /**
     * 抠图后原图处理
     * @param oriImage      原始图片
     * @param templateImage 模板图片
     * @param xDistance     x轴距离
     * @return  java.awt.image.BufferedImage
     * @author  Gopal.pan
     */
    private static BufferedImage oriPictureByTemplate(BufferedImage oriImage, BufferedImage templateImage, int xDistance) throws Exception {


        // 源文件备份图像矩阵 支持alpha通道的rgb图像
        BufferedImage bufferedImage = new BufferedImage(oriImage.getWidth(), oriImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        // 源文件图像矩阵
        int[][] oriImageData = getData(oriImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);

        // 源图做不透明处理
        for (int i = 0; i < oriImageData.length; i++) {
            for (int j = 0; j < oriImageData[0].length; j++) {
                int rgb = oriImage.getRGB(i, j);
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                //无透明处理
                rgb = r + (g << 8) + (b << 16) + (255 << 24);
                bufferedImage.setRGB(i, j, rgb);
            }
        }

        // 小图部分透明处理
        for (int i = 0; i < templateImageData.length; i++) {
            for (int j = 0; j < templateImageData[0].length - 5; j++) {
                int rgb = templateImage.getRGB(i, j);
                //对源文件备份图像(xDistance+i,y+j)坐标点进行透明处理
                if (rgb <= 0) {
                    int rgb_ori = bufferedImage.getRGB(xDistance + i, Y_DISTANCE + j);
                    int r = (0xff & rgb_ori);
                    int g = (0xff & (rgb_ori >> 8));
                    int b = (0xff & (rgb_ori >> 16));
                    rgb_ori = r + (g << 8) + (b << 16) + (150 << 24);
                    bufferedImage.setRGB(xDistance + i, Y_DISTANCE + j, rgb_ori);
                }
            }
        }

        return bufferedImage;

    }




    /**
     * 根据模板图片抠图
     * @param oriImage      原始图片
     * @param templateImage 模板图片
     * @param targetImage   目标图片
     * @return   java.awt.image.BufferedImage
     * @author   Gopal.pan
     */
    private static void cutPictureByTemplate(BufferedImage oriImage, BufferedImage templateImage, BufferedImage targetImage)  {
        // 源文件图像矩阵
        int[][] oriImageData = getData(oriImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);
        // 模板图像宽度

        for (int i = 0; i < templateImageData.length; i++) {
            // 模板图片高度
            for (int j = 0; j < templateImageData[0].length; j++) {
                // 如果模板图像当前像素点不是白色 copy源文件信息到目标图片中
                int rgb = templateImageData[i][j];
                if (rgb <= 0) {
                    targetImage.setRGB(i, j, oriImageData[i][j]);
                }
            }
        }
    }




    /**
     * 小图片区域
     * @param xDistance     随机切图坐标x轴位置
     * @param targetWidth   切图后目标宽度
     * @param targetHeight  切图后目标高度
     * @param inputStream   源文件输入
     * @return  java.awt.image.BufferedImage
     * @author  Gopal.pan
     */
    private static BufferedImage getTargetArea(int xDistance, int targetWidth, int targetHeight, InputStream inputStream) throws Exception {
        Iterator<ImageReader> imageReaderList = ImageIO.getImageReadersByFormatName(IMAGE_TYPE_JPG);
        ImageReader imageReader = imageReaderList.next();
        // 获取图片流
        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
        // 输入源中的图像将只按顺序读取
        imageReader.setInput(iis, true);

        ImageReadParam param = imageReader.getDefaultReadParam();
        Rectangle rec = new Rectangle(xDistance, Y_DISTANCE, targetWidth, targetHeight);
        param.setSourceRegion(rec);
        return imageReader.read(0, param);
    }





    /**
     * 生成图像矩阵
     * @param bufferedImage    bufferedImage
     * @return  int[][]
     * @author  Gopal.pan
     */
    private static int[][] getData(BufferedImage bufferedImage) {
        int[][] data = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                data[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return data;
    }



    /**
     *  随机生成抠图坐标地址
     * @param templateImageWidth  模板图像宽度
     * @return  int
     * @author  Gopal.pan
     */
    private static int generateCutoutCoordinates(int templateImageWidth) {

        int xDistance = ThreadLocalRandom.current().nextInt(MIN_X_DISTANCE, ORI_WIDTH - templateImageWidth);
        LOGGER.info("【FW-security】滑块验证,小图X轴坐标地址[{}]",xDistance);
        return xDistance;

    }
}
