package com.youyu.service.mail;

import java.util.Map;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat);
    void sendCommentMail(String to, Map<String, String> templateParams) throws Exception;
}
