package com.youyu.service.mail;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.tea.TeaConverter;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaPair;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.youyu.config.AliyunMailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class AliyunEmailService {

    private final AliyunMailConfig mailConfig;

    @Value("${aliyun.mail.account-name}")
    private String accountName;

    /**
     * 发送模板邮件
     */
    public void sendTemplateMail(String templateId, String toAddress, Map<String, String> params) throws Exception {
        // 模板设置
        SingleSendMailRequest.SingleSendMailRequestTemplate template = new SingleSendMailRequest.SingleSendMailRequestTemplate();
        template.setTemplateId(templateId);
        // 将 Map 转成 TeaPair[]
        TeaPair[] pairs = params.entrySet().stream()
                .map(e -> new TeaPair(e.getKey(), e.getValue()))
                .toArray(TeaPair[]::new);
        // 使用 TeaConverter 生成 templateData
        Map<String, String> templateTemplateData = TeaConverter.buildMap(pairs);
        template.setTemplateData(templateTemplateData);
        // 邮件设置
        SingleSendMailRequest singleSendMailRequest = new SingleSendMailRequest()
                .setTemplate(template)
                .setToAddress(toAddress)
                .setReplyToAddress(true)
                .setAccountName(accountName)
                .setSubject("您收到了一条新的回复")
                .setAddressType(1);

        RuntimeOptions runtime = new RuntimeOptions();
        Client client = mailConfig.getMailClient();
        // 复制代码运行请自行打印 API 的返回值
        client.singleSendMailWithOptions(singleSendMailRequest, runtime);
    }
}
