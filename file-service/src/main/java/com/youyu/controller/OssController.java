package com.youyu.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.youyu.annotation.Log;
import com.youyu.config.OssProperties;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.factory.OssClientFactory;
import com.youyu.result.ResponseResult;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@RefreshScope
@Data
@RestController
@RequestMapping("/oss")
public class OssController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private OssClientFactory ossClientFactory;

    /**
     * 服务端签名后直传
     *
     * @return
     */
    @RequestMapping("/policy")
    @Log(title = "文件上传(policy)", type = LogType.UPLOAD)
    public ResponseResult<Map<String, String>> policy(@RequestParam(defaultValue = "post/images") String base) {
        // date = new Date();
        // int first = date.getYear();

        String format = new SimpleDateFormat("yyyy/MMdd").format(new Date());
        String dir = base + "/" + format + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = new LinkedHashMap<>();
        // 创建OSSClient实例。
        OSS ossClient = ossClientFactory.getClient();
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap.put("OSSAccessKeyId", ossProperties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            /*respMap.put("key", dir);*/ // 文件路径+文件名，前端确定
            respMap.put("host", ossProperties.getHost());
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return ResponseResult.success(respMap);
    }

    /**
     * STS临时授权访问
     *
     * @return
     */
    @RequestMapping("/sts")
    @Log(title = "文件上传(sts)", type = LogType.UPLOAD)
    public ResponseResult<AssumeRoleResponse.Credentials> sts() {
        AssumeRoleResponse.Credentials credentials = null;
        String roleSessionName = "SessionTest";
        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket上传文件。
        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的exampledir目录。
        String policy = "{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:PutObject\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:youyu-source/*\" \n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {
            // regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
            String regionId = "";
            // 添加endpoint。
            DefaultProfile.addEndpoint("", "", "Sts", ossProperties.getEndPoint());
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, ossProperties.getAccessKeyIdRAM(), ossProperties.getAccessKeySecretRAM());
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(ossProperties.getRoleArn());
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            final AssumeRoleResponse response = client.getAcsResponse(request);
            credentials = response.getCredentials();
        } catch (ClientException e) {
            e.printStackTrace();
            throw new SystemException(ResultCode.OTHER_ERROR_SILENT.getCode(), e.getErrMsg());
        }
        return ResponseResult.success(credentials);
    }
}
