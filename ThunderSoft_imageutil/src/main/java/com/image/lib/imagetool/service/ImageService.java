package com.image.lib.imagetool.service;

import com.netease.lowcode.core.annotation.NaslLogic;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.Headers;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Date;

@Component
public class ImageService {

    @NaslLogic(enhance = false)
    public String imageBuffer(String imagePath, Long size, Boolean bool) {
        URL url = null;
        if (bool) {
            url = generateSimplePresignedDownloadUrl(imagePath);
        } else {
            try {
                url = new URL(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (size.equals(1l)) {
            return thumbnail_w_h(url, 300, 600);
        } else {
//         解码Base64字符串
            byte[] imageBytes = Base64.getDecoder().decode(thumbnail_w_h(url, 300, 600));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                // 创建图片
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                int width = image.getWidth(null);
                int height = image.getHeight(null);
                final Rectangle rectangle = calcRotatedSize(width, height, 90);
                final BufferedImage targetImg = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2d = targetImg.createGraphics();
                // 抗锯齿
                graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                // 从中心旋转
                graphics2d.translate((rectangle.width - width) / 2D, (rectangle.height - height) / 2D);
                graphics2d.rotate(Math.toRadians(90), width / 2D, height / 2D);
                graphics2d.drawImage(image, 0, 0, null);
                graphics2d.dispose();
                ImageIO.write(targetImg, "JPEG", outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }


    private static Rectangle calcRotatedSize(int width, int height, int degree) {
        if (degree < 0) {
            // 负数角度转换为正数角度
            degree += 360;
        }
        if (degree >= 90) {
            if (degree / 90 % 2 == 1) {
                int temp = height;
                //noinspection SuspiciousNameCombination
                height = width;
                width = temp;
            }
            degree = degree % 90;
        }
        double r = Math.sqrt(height * height + width * width) / 2;
        double len = 2 * Math.sin(Math.toRadians(degree) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(degree)) / 2;
        double angel_dalta_width = Math.atan((double) height / width);
        double angel_dalta_height = Math.atan((double) width / height);
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = width + len_dalta_width * 2;
        int des_height = height + len_dalta_height * 2;

        return new Rectangle(des_width, des_height);
    }

    private URL generateSimplePresignedDownloadUrl(String key) {
        // 1 初始化用户身份信息(secretId, secretKey)
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
//
        String secretId = "####"; //用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = "####";///用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket的区域
        // COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-beijing，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
        // 3 生成 cos 客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        // bucket 名需包含 appid
        String bucketName = "#";
//        String key = "余某_20240321033606997_ori.jpg";
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        // 设置签名过期时间(可选), 若未进行设置则默认使用 ClientConfig 中的签名过期时间(1小时)
        // 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        req.setExpiration(expirationDate);

        // 填写本次请求的参数
        // 设定限速值，例如128KB/s
        req.addRequestParameter("x-cos-traffic-limit", "10485760");

        // 填写本次请求的头部。Host 必填
        req.putCustomRequestHeader(Headers.HOST,
                cosclient.getClientConfig().getEndpointBuilder().buildGeneralApiEndpoint(bucketName));
        //req.putCustomRequestHeader("header1", "value1");

        URL url = cosclient.generatePresignedUrl(req);

        cosclient.shutdown();
        return url;
    }



    /**
     * 按照指定的宽高压缩原图
     *
     * @param img
     */
    private String thumbnail(URL img, int destWidth, int destHeight) {
        ByteArrayOutputStream outputStream = null;
        try {
            //读取源图
            BufferedImage BI = ImageIO.read(img);
            double srcWidth = BI.getWidth(); // 源图宽度
            double srcHeight = BI.getHeight(); // 源图高度
            if ((int) srcWidth >= destWidth && (int) srcHeight >= destHeight) {
                outputStream = new ByteArrayOutputStream();
                Image image = BI.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
                BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.setColor(Color.RED);
                g.drawImage(image, 0, 0, null); //绘制处理后的图
                g.dispose();
                ImageIO.write(tag, "JPEG", outputStream);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 按照宽高等比例缩放(缩小或放大)
     *
     * @param img
     */
    private String thumbnail_w_h(URL img, int destWidth, int destHeight) {
        try {
            BufferedImage bi = ImageIO.read(img);

            double srcWidth = bi.getWidth(); // 源图宽度
            double srcHeight = bi.getHeight(); // 源图高度

            double scale = 0.1;//缩放比例因子，大于1表示放大，小于1表示缩小
            if (destWidth > 0 && destHeight > 0) {
                //下面这个缩放比例因子取值很好理解，目标尺寸和原图尺寸的比值越大，
                //表示缩放的比率越小，也就代表图片变形的越小，就取影响图片变形小的比例因子
                scale = destHeight / srcHeight < destWidth / srcWidth ? destHeight / srcHeight : destWidth / srcWidth;
            }
            return thumbnail(img, (int) (srcWidth * scale), (int) (srcHeight * scale));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}