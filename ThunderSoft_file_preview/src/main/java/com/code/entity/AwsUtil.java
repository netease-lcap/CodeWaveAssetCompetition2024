package com.code.entity;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.code.config.DHS3Config;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhouzz
 */
@Component
public class AwsUtil {

    @Resource
    private DHS3Config dhs3Config;


    public boolean s3FileExist(String objectKey){
        AWSCredentials credentials =new BasicAWSCredentials(dhs3Config.amazonS3AccessKey,dhs3Config.amazonS3SecretKey);
        AmazonS3  amazonS3 = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dhs3Config.amazonS3Address, "")).build();
        return amazonS3.doesObjectExist(dhs3Config.amazonS3Bucket,objectKey);
    }

    public String downloadFile(String objectKey, int expiration){
        DateTime dateTime =null;
        if(expiration> BigDecimal.ZERO.intValue()){
            dateTime = DateUtil.offsetMinute(DateUtil.date(),expiration);
        }
        return downloadFile(objectKey,dateTime);
    }

    public String downloadFile(String objectKey, Date expiration){
        try {
            GeneratePresignedUrlRequest httpRequest = new GeneratePresignedUrlRequest(dhs3Config.amazonS3Bucket, objectKey);
            if (expiration != null) {
                httpRequest.setExpiration(expiration);
            }

            AWSCredentials credentials =new BasicAWSCredentials(dhs3Config.amazonS3AccessKey,dhs3Config.amazonS3SecretKey);
            AmazonS3  amazonS3 = AmazonS3ClientBuilder.standard()
                    .withPathStyleAccessEnabled(true)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dhs3Config.amazonS3Address, "")).build();
            return amazonS3.generatePresignedUrl(httpRequest).toExternalForm();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
