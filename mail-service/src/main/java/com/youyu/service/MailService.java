package com.youyu.service;

import com.youyu.dto.MailReplyInput;

import javax.mail.MessagingException;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat) throws MessagingException;
    Boolean sendPostReplyNotice(MailReplyInput input) throws MessagingException;
    Boolean sendMomentReplyNotice(MailReplyInput input) throws MessagingException;
}
