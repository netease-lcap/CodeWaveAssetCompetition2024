package com.fdddf.wechat;

import com.fdddf.wechat.eml.EmlAttachment;
import com.fdddf.wechat.eml.ParsedEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmlUtil {
    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * Parse an EML file and return a ParsedEmail object
     * @param emlContent The content of the EML file
     * @return ParsedEmail
     */
    public static ParsedEmail parseEml(String emlContent)
    {
        try {
            InputStream emlStream = new ByteArrayInputStream(emlContent.getBytes());

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session, emlStream);

            // Create the ParsedEmail object
            ParsedEmail parsedEmail = new ParsedEmail();
            parsedEmail.subject = message.getSubject();

            // Set the sender (From)
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                parsedEmail.from = ((InternetAddress) fromAddresses[0]).getAddress();
            }

            // Set the recipients (To)
            Address[] toAddresses = message.getRecipients(Message.RecipientType.TO);
            List<String> toList = new ArrayList<>();
            if (toAddresses != null) {
                for (Address address : toAddresses) {
                    toList.add(((InternetAddress) address).getAddress());
                }
            }
            parsedEmail.toRecipients = toList;
            parsedEmail.sentDate = message.getSentDate()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Initialize list for attachments
            List<EmlAttachment> attachments = new ArrayList<>();

            // Check if the message has multipart content (for attachments)
            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();

                // Iterate over all parts
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    // If it's an attachment
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        String fileName = bodyPart.getFileName();
                        String mimeType = bodyPart.getContentType();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] content = outputStream.toByteArray();

                        EmlAttachment attachment = new EmlAttachment(fileName, mimeType, content);
                        attachments.add(attachment);
                    } else if (bodyPart.isMimeType("text/plain")) {
                        // If it's the message body
                        parsedEmail.body = bodyPart.getContent().toString();
                    }
                }
            } else if (message.isMimeType("text/plain")) {
                // If the content is simple text
                parsedEmail.body = message.getContent().toString();
            }
            parsedEmail.setAttachments(attachments);
            return parsedEmail;

        } catch (Exception e) {
            LCAP_LOGGER.error("Error parsing EML file: " + e.getMessage());
            return null;
        }
    }

}
