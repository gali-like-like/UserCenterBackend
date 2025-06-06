package com.galilikelike.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.UserSpecifiedEndpointBuilder;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.DateUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * GeneratePresignedUrlDemo展示了生成预签名的下载链接与上传连接的使用示例.
 * 用于可将生成的连接分发给移动端或者他人, 即可实现在签名有效期内上传或者下载文件.
 */
public class CosUtils {
    private static String secretId = System.getenv("USER_CENTER_TENXUN_SECRET_ID");;
    private static String secretKey = System.getenv("USER_CENTER_TENXUN_SECRET_KEY");;
    private static String bucketName = "user-center-1317592623";
    private static String region = "ap-guangzhou";
    private static COSClient cosClient = createCli();

    private static COSClient createCli() {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 3 生成cos客户端
        clientConfig.setHttpProtocol(HttpProtocol.http);
        String serviceApiEndpoint = "service.cos.myqcloud.com";
        String cdnEndpoint = "cos.gallilikelike.cn";
        UserSpecifiedEndpointBuilder endPointBuilder = new UserSpecifiedEndpointBuilder(cdnEndpoint, serviceApiEndpoint);
        clientConfig.setEndpointBuilder(endPointBuilder);
        COSClient cosclient = new COSClient(cred, clientConfig);
        return cosclient;
    }

    // 获取下载的预签名连接
    private static void generateSimplePresignedDownloadUrl() {
        String key = "aaa.txt";
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        // 设置签名过期时间(可选), 若未进行设置则默认使用ClientConfig中的签名过期时间(1小时)
        // 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        req.setExpiration(expirationDate);

//        // 填写本次请求的参数
//        req.addRequestParameter("sign", "44CzU9OdSyCSpnFP1");
//        req.addRequestParameter("t", String.format("%02x",Instant.now().getEpochSecond()));
        // 填写本次请求的头部。Host 头部会自动补全，不需要填写
        req.putCustomRequestHeader("header1", "value1");

        URL url = cosClient.generatePresignedUrl(req);
        System.out.println(url.toString());
    }

    // 获取预签名的下载链接, 并设置返回的content-type, cache-control等http头
    public static String generatePresignedDownloadUrlWithOverrideResponseHeader(String key) {
        COSClient client = createCli();
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        // 设置下载时返回的http头
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        String responseContentType = "image/png";
        String responseContentLanguage = "zh-CN";
        String responseCacheControl = "no-cache";
        String cacheExpireStr = DateUtils.formatRFC822Date(new Date(System.currentTimeMillis() + 24 * 3600 * 1000));
        responseHeaders.setContentType(responseContentType);
        responseHeaders.setContentLanguage(responseContentLanguage);
        responseHeaders.setCacheControl(responseCacheControl);
        responseHeaders.setExpires(cacheExpireStr);

        req.setResponseHeaders(responseHeaders);
        // 设置签名过期时间(可选), 若未进行设置则默认使用ClientConfig中的签名过期时间(1小时)
//        // 填写本次请求的头部。Host 头部会自动补全，不需要填写
//        req.putCustomRequestHeader("host", "user-center-1317592623.cos.ap-guangzhou.myqcloud.com");

        URL url = cosClient.generatePresignedUrl(req,false);
        System.out.println(url.toString());
        client.shutdown();
        return url.toString();
    }

    public static void upload(MultipartFile file) throws IOException {
        // 指定要上传的文件
// 指定文件将要存放的存储桶
// 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        COSClient client = createCli();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        String originalFilename = file.getOriginalFilename().toLowerCase();
        String key = "headers/" + UUIDUntils.randomUUID() + originalFilename; // 存储路径+uuid+后缀名
        // 3. 通过输入流直接上传到COS
        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                file.getInputStream(),
                metadata
        );
        PutObjectResult putObjectResult = client.putObject(request);
        client.shutdown();
    }


    // 生成预签名的上传连接
    private static void generatePresignedUploadUrl(String key) {
        Date expirationTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        // 填写本次请求的 header。Host 头部会自动补全，只需填入其他头部
        Map<String, String> headers = new HashMap<String,String>();
        // 填写本次请求的 params。
        Map<String, String> params = new HashMap<String,String>();

        URL url = cosClient.generatePresignedUrl(bucketName, key, expirationTime, HttpMethodName.PUT, headers, params);
        System.out.println(url.toString());
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            // 写入要上传的数据
            out.write("This text uploaded as object.");
            out.close();
            int responseCode = connection.getResponseCode();
            System.out.println("Service returned response code " + responseCode);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
