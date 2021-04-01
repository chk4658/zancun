package com.touchspring.ailge.service.agile;

import com.touchspring.ailge.dto.AttachmentDTO;

import java.util.List;

/**
 * 邮件
 */
public interface IMailService {

    /**
     * 发送文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMail(String to, String subject, String content);

    void sendAttendedFileMail(String to, String userName, List<AttachmentDTO> attachmentDTOList, String url, String projectName) throws Exception;

    void sendMessageMail(String to, String title, String content, String url, String projectName, String milestoneName, String taskName,
                         String reviewer, String confirmer, String estEndTime, String type, String circleName, String circleRoleName, String circleRoleUserName);
}
