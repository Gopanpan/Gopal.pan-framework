package com.github.sylphlike.framework.notice.email;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);


    public static Boolean sendEmail(EmailBo emailBo){
        LOGGER.info("【FW-utils】邮件发送,入参[{}] ",emailBo);

        boolean sendFlag;
        Session session;
        Transport transport = null;
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.host", emailBo.getSmtpHost());

            session = Session.getInstance(properties);
            transport = session.getTransport();
            transport.connect(emailBo.getSendEmailAccount(),emailBo.getSendEmailPassword());


            MimeMessage message = new MimeMessage(session);
            message.setHeader("X-Priority", "3");

            if(StringUtils.isEmpty(emailBo.getSendEmailNickname())){
                message.setFrom(new InternetAddress(emailBo.getSendEmailAccount()));
            }else{
                message.setFrom(new InternetAddress( MimeUtility.encodeWord(emailBo.getSendEmailNickname()) + " <"+emailBo.getSendEmailAccount()+">"));
            }




            InternetAddress[] iaToList = InternetAddress.parse(emailBo.getReceiveEmailAccount());
            message.setRecipients(Message.RecipientType.TO, iaToList);
            if(StringUtils.isNotEmpty(emailBo.getCarbonCopyAccount())){
                InternetAddress[] carbonAccount = InternetAddress.parse(emailBo.getCarbonCopyAccount());
                message.setRecipients(Message.RecipientType.CC, carbonAccount);
            }
            if(StringUtils.isNotEmpty(emailBo.getBlindCarbonCopyAccount())){
                InternetAddress[] blindCarbonAccount = InternetAddress.parse(emailBo.getBlindCarbonCopyAccount());
                message.setRecipients(Message.RecipientType.BCC, blindCarbonAccount);
            }

            message.setSubject(emailBo.getSubject());
            message.setSentDate(new Date());

            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            if(EmailContentEnum.HTML.getCode().equals(emailBo.getContentType())){

                if(!StringUtils.isEmpty(emailBo.getContentImage())){
                    MimeBodyPart image = new MimeBodyPart();
                    DataHandler dh = new DataHandler(new FileDataSource(emailBo.getContentImage()));
                    image.setDataHandler(dh);
                    // 为"节点"设置一个唯一编号（在文本"节点"将引用该ID）
                    image.setContentID("mailTestPic");
                    contentPart.setContent(emailBo.getContent() +"<img src='cid:mailTestPic'/>", "text/html;charset=UTF-8");
                    multipart.addBodyPart(image);
                }else {
                    contentPart.setContent(emailBo.getContent(), "text/html;charset=UTF-8");
                }




            }else {
                contentPart.setContent(emailBo.getContent(),"text/plain;charset=UTF-8");
            }
            multipart.addBodyPart(contentPart);

            if(!StringUtils.isEmpty(emailBo.getAttachmentAddress())){
                File attachment = new File(emailBo.getAttachmentAddress());
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            message.saveChanges();
            transport.sendMessage(message, message.getAllRecipients());
            sendFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("【FW-utils】邮件发送,系统异常", e);
            sendFlag = false;
        }finally {
            try {
                if(transport != null)
                    transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }
        LOGGER.info("【FW-utils】邮件发送,出参[{}] ",sendFlag);

        return sendFlag;
    }



}
