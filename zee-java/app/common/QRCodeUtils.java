package guide.app.common;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 武汉港迪软件信息技术有限公司
 * 版本        开发者            时间              描述
 * 1.0.0      dunfeng          2022/4/13          新建
 */
public class QRCodeUtils {
    //设置默认参数，可以根据需要进行修改
    private static final int QRCOLOR = 0xFF000000; // 默认是黑色
    private static final int BGWHITE = 0xFFFFFFFF; // 背景颜色
    private static final int WIDTH = 180; // 二维码宽
    private static final int HEIGHT = 180; // 二维码高
    /**
     * 用于设置QR二维码参数
     * com.google.zxing.EncodeHintType：编码提示类型,枚举类型
     * EncodeHintType.CHARACTER_SET：设置字符编码类型
     * EncodeHintType.ERROR_CORRECTION：设置误差校正
     * ErrorCorrectionLevel：误差校正等级，L = ~7% correction、M = ~15% correction、Q = ~25% correction、H = ~30% correction
     * 不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
     * EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
     */
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码方式
            put(EncodeHintType.MARGIN, 0);
        }
    };


    /**
     * 生成二维码和附带字体参数
     */
    private static BufferedImage encode(String desc, Font font) throws WriterException {
        Map<String, String> parse = (Map) JSONObject.parse(desc);
        String itemnum = parse.get("itemnum");
        String description = parse.get("description");
        String requestedBy = parse.get("requestedBy");
        String transdate = parse.get("transdate");
        String estTime = parse.get("estTime");
        String binnum = parse.get("binnum");
		// 设置二维码旁边的文字信息

		// 二维码内容
		Map map = new HashMap<>();
		map.put("itemnum", parse.get("itemnum"));
		map.put("binnum", parse.get("binnum"));
		map.put("lotnum", parse.get("lotnum"));
		map.put("location", parse.get("location"));
		String qrurl = new JSONObject(map).toString();

        /**
         * MultiFormatWriter:多格式写入，这是一个工厂类，里面重载了两个 encode 方法，用于写入条形码或二维码
         *      encode(String contents,BarcodeFormat format,int width, int height,Map<EncodeHintType,?> hints)
         *      contents:条形码/二维码内容
         *      format：编码类型，如 条形码，二维码 等
         *      width：码的宽度
         *      height：码的高度
         *      hints：码内容的编码类型
         * BarcodeFormat：枚举该程序包已知的条形码格式，即创建何种码，如 1 维的条形码，2 维的二维码 等
         * BitMatrix：位(比特)矩阵或叫2D矩阵，也就是需要的二维码
         */
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        /**参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
         * BitMatrix 的 get(int x, int y) 获取比特矩阵内容，指定位置有值，则返回true，将其设置为前景色，否则设置为背景色
         * BufferedImage 的 setRGB(int x, int y, int rgb) 方法设置图像像素
         *      x：像素位置的横坐标，即列
         *      y：像素位置的纵坐标，即行
         *      rgb：像素的值，采用 16 进制,如 0xFFFFFF 白色
         */
        BitMatrix bm = multiFormatWriter.encode(qrurl, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
        //创建一个图片缓冲区存放二维码图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
            }
        }
        
        // ------------------------------------------自定义文本描述-------------------------------------------------
        if (StringUtils.isNotEmpty(desc)) {
            //在内存创建图片缓冲区  这里设置画板的宽高和类型
            BufferedImage outImage = new BufferedImage(600, 350, BufferedImage.TYPE_4BYTE_ABGR);
            //创建画布
            Graphics2D outg = outImage.createGraphics();

            int drawHeight = 180 - (image.getHeight()/2);
            // 在画布上画上二维码  X轴Y轴，宽度高度
            outg.drawImage(image, 30, drawHeight, image.getWidth(), image.getHeight(), null);

            // 画文字到新的面板
            outg.setColor(Color.BLACK);
            // 字体、字型、字号
            outg.setFont(font);
            outg.drawString("物资编码:"+itemnum, 230, drawHeight+30);
            outg.drawString("货柜:"+binnum, 230, drawHeight+50);
            outg.drawString("请求人："+requestedBy, 230, drawHeight+70);
            outg.drawString("接收入库时间："+transdate, 230, drawHeight+90);
            outg.drawString("预计使用时间："+estTime, 230, drawHeight+110);
            int length = description.length();
            if (length<=30){
                outg.drawString("描述:"+description, 230, drawHeight+130);
            }else if (length>30 ){
                outg.drawString("描述:"+description.substring(0,31), 230, drawHeight+130);
                outg.drawString(description.substring(31,length), 275, drawHeight+150);
            }
            outg.dispose();
            outImage.flush();
            image = outImage;
        }
        image.flush();
        return image;
    }
    public static String QRCode(String desc,String path,String name) throws Exception {
        try{
            FileOutputStream fileOutputStream = null;
            try {
            	File folder = new File(path);
            	if (!folder.exists() && !folder.isDirectory()) {
            	    folder.mkdirs();
            	}
            	path = path+"\\"+name;
                fileOutputStream = new FileOutputStream(path);  //保存路径输出流，将图片输出到指定路径
                Font fontChinese = new Font("黑体", Font.BOLD, 16);
                BufferedImage image = QRCodeUtils.encode(desc, fontChinese);
                BufferedImage newBufferedImage = new BufferedImage(
                        image.getWidth(), image.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                // TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
                newBufferedImage.createGraphics().drawImage(image, 0, 0,
                        Color.WHITE, null);
                // write to jpeg file
                ImageIO.write(newBufferedImage, "jpg", new File(path));
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != fileOutputStream) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            return "OK";
        }catch (Exception e){
            e.printStackTrace();
            return "NO";
        }
    }
    
}