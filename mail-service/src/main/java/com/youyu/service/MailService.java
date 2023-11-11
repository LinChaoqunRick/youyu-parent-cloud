package com.youyu.service;

import com.youyu.dto.mail.MailReplyInput;
import com.youyu.dto.moment.MomentCommentListOutput;

import javax.mail.MessagingException;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat) throws MessagingException;
    Boolean sendPostReplyNotice(MailReplyInput input) throws MessagingException;
    Boolean sendMomentCommentNotice(MailReplyInput input) throws MessagingException;
    Boolean sendMomentCommentMailNotice(MomentCommentListOutput detail);
}
