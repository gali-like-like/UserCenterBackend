package com.galilikelike.Utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.Headers;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.UserSpecifiedEndpointBuilder;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
public class ManageCosUtils {

    private static COSClient client = ManageCosUtils.cosClient();

    private static String bucketName = "user-center-1317592623";


    private static COSClient cosClient() {
// 1 初始化用户身份信息（secretId, secretKey）。
// SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = System.getenv("USER_CENTER_TENXUN_SECRET_ID");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = System.getenv("USER_CENTER_TENXUN_SECRET_KEY");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        COSCredentials cred = new AnonymousCOSCredentials();
// 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        Region region = new Region("ap-guangzhou");
        ClientConfig clientConfig = new ClientConfig(region);
// 这里建议设置使用 https 协议
// 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.http);
//  get service 请求会使用这个域名，这个域名不能自定义
        String serviceApiEndpoint = "service.cos.myqcloud.com";
        String cdnEndpoint = "gallilikelike.cn";
        UserSpecifiedEndpointBuilder endPointBuilder = new UserSpecifiedEndpointBuilder(cdnEndpoint, serviceApiEndpoint);
        clientConfig.setEndpointBuilder(endPointBuilder);
        clientConfig.setRegion(region);
// 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    public static String getUrl(String key) {
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        HttpMethodName method = HttpMethodName.GET;
        URL url = client.generatePresignedUrl(bucketName, key, expirationDate, method);
        log.info("本次访问url:{}",url.toString());
        return url.toString();
    }


    public static void upload(MultipartFile file) throws IOException {
        // 指定要上传的文件
// 指定文件将要存放的存储桶
// 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
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
    }

    public static void getGeneraUrl(String key) {
        COSClient cosClient = cosClient();
// 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = "user-center-1317592623";
// 对象键(Key)是对象在存储桶中的唯一标识。详情请参见 [对象键](https://cloud.tencent.com/document/product/436/13324)
// 设置签名过期时间(可选), 若未进行设置则默认使用 ClientConfig 中的签名过期时间(1小时)
// 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        Map<String, String> params = new HashMap<String, String>();

        params.put("sign", "44CzU9OdSyCSpnFP1");
        params.put("t",String.format("%02x",Instant.now().getEpochSecond()));
// 填写本次请求的头部，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的头部
        Map<String, String> headers = new HashMap<String, String>();


// 填写本次请求的参数，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的参数
// 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.GET;
        URL url = cosClient.generatePresignedUrl(bucketName, key, expirationDate, method,headers,params);
        System.out.println(url.toString());
// 确认本进程不再使用 cosClient 实例之后，关闭即可
        cosClient.shutdown();
    }

}
