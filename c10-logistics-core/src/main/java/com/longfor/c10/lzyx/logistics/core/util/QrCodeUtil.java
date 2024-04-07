package com.longfor.c10.lzyx.logistics.core.util;


import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/14 14:10
 */
@Slf4j
public class QrCodeUtil {

    /**
     * 图片的宽度
     */
    private static final int IMAGE_WIDTH = 350;
    /**
     * 图片的高度(需按实际内容高度进行调整)
     */
    private static final int IMAGE_HEIGHT = 350;
    /**
     * 生成二维码的格式
     */
    private static final String FORMAT = "jpg";



    /**
     * 根据内容生成二维码数据
     *
     * @param content   二维码文字内容[为了信息安全性，一般都要先进行数据加密]
     * @param imgWidth  二维码图片宽度
     * @param imgHeight 二维码图片高度
     */
    private static BitMatrix createQrcodeMatrix(String content, int imgWidth, int imgHeight) {
        log.info("createQrcodeMatrix content：{},width:{}, height:{} ", content, imgWidth, imgHeight);
        Map<EncodeHintType, Object> hints = Maps.newEnumMap(EncodeHintType.class);
        // 设置字符编码
        hints.put(EncodeHintType.CHARACTER_SET, Charsets.UTF_8.name());
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码四周白色区域的大小
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            if(StringUtils.isBlank(content)){
                throw new RuntimeException("很抱歉,二维码生成失败!");
            }
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, imgWidth == 0 ? IMAGE_WIDTH : imgWidth, imgHeight == 0 ? IMAGE_HEIGHT : imgHeight, hints);
            //自定义白边边框宽度
            int margin = 5;
            //生成新的bitMatrix
            return updateBit(bitMatrix, margin);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     *因为二维码边框设置那里不起作用，不管设置多少，都会生成白边，所以根据网上
     *的例子进行修改，自定义控制白边宽度，该方法生成自定义白边框后的bitMatrix；
     */
    private static BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        //获取二维码图案的属性
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        // 按照自定义边框生成新的BitMatrix
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        //循环，将二维码图案绘制到新的bitMatrix中
        for(int i= margin; i < resWidth- margin; i++){
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 根据指定边长创建生成的二维码，允许配置logo属性
     *
     * @param content    二维码内容
     * @param imgWidth   二维码宽度
     * @param imgLength  二维码长度
     * @return 二维码图片的字节数组
     */
    public static byte[] createQrcode(String content, int imgWidth, int imgLength) {
        BitMatrix qrCodeMatrix = createQrcodeMatrix(content, imgWidth, imgLength);
        if (qrCodeMatrix == null) {
            return null;
        }
        try {
            Path path = Files.createTempFile("qrcode_log", "." + FORMAT);
            // 测试生成到本地用下面这句
            // File file = new File("/Users/admin/Downloads/qrcode_log.jpg");
            MatrixToImageWriter.writeToPath(qrCodeMatrix, FORMAT, path);
            return com.google.common.io.Files.toByteArray(path.toFile());
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 根据指定边长创建生成的二维码，默认宽高350
     *
     * @param content    二维码内容
     * @return 二维码图片的字节数组
     */
    public static byte[] createQrcode(String content) {
        BitMatrix qrCodeMatrix = createQrcodeMatrix(content, IMAGE_WIDTH, IMAGE_HEIGHT);
        if (qrCodeMatrix == null) {
            return null;
        }
        try {
            Path path = Files.createTempFile("qrcode_log", "." + FORMAT);
            // 测试生成到本地用下面这句
            // File file = new File("/Users/admin/Downloads/qrcode_log.jpg");
            MatrixToImageWriter.writeToPath(qrCodeMatrix, FORMAT, path);
            return com.google.common.io.Files.toByteArray(path.toFile());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据指定边长创建生成的二维码，
     *
     * @param content   二维码内容
     * @param imgWidth  二维码宽度
     * @param imgHeight 二维码长度
     */
    public static byte[] bufferedImage(String content, int imgWidth, int imgHeight) {
        BitMatrix qrCodeMatrix = createQrcodeMatrix(content, imgWidth, imgHeight);
        if (qrCodeMatrix == null) {
            return null;
        }
        try {
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(qrCodeMatrix);
            return imageToBytes(bufferedImage, FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据指定边长创建生成的二维码，
     *
     * @param content   二维码内容
     * @param imgWidth  二维码宽度
     * @param imgHeight 二维码长度
     * @return Base64 字符串码
     */
    public static String image2Base64(String content, int imgWidth, int imgHeight) {
        BitMatrix qrCodeMatrix = createQrcodeMatrix(content, imgWidth, imgHeight);
        if (qrCodeMatrix == null) {
            return null;
        }
        try {
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(qrCodeMatrix);
            byte[] bytes = imageToBytes(bufferedImage, FORMAT);
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            return null;
        }
    }


    private static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            return null;
        }
        return out.toByteArray();
    }



    public static void main(String[] args) throws IOException {
        byte[] bytes = createQrcode("https://www.baidu.com", 350, 350);
        if(bytes == null){
            System.out.println("不存在");
            return;
        }
        File f = new File("/Users/admin/Downloads/qrcode_log2.jpg");
        //获取到输出对象
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }
}
