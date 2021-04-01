package com.touchspring.ailge.service.impl.agile;

import com.touchspring.ailge.dto.AttachmentDTO;
import com.touchspring.ailge.enums.MessageModuleEnum;
import com.touchspring.ailge.service.agile.IMailService;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 邮箱 服务实现类
 */
@Service
public class IMailServiceImpl implements IMailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送邮件的地址
     */
    @Value("${spring.mail.from}")
    private String fromUserMail;

    /**
     * 发送文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(fromUserMail);
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);
        //发送邮件
        mailSender.send(message);
    }

    /**
     * 发送html
     */
    public void sendMessageMail(String to, String title, String content, String url, String projectName, String milestoneName, String taskName,
                                String reviewer, String confirmer, String estEndTime, String type,
                                String circleName, String circleRoleName, String circleRoleUserName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromUserMail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(title);
        } catch (MessagingException e) {
            e.printStackTrace();
            return;
        }

        // 启用html
        try {
            mimeMessageHelper.setText(this.getTextStyle(content, url, projectName, milestoneName, taskName,
                    reviewer, confirmer, estEndTime, type,
                    circleName, circleRoleName, circleRoleUserName).toString(), true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // 发送邮件
        mailSender.send(mimeMessage);
    }

    /**
     * 组装html内容格式
     */
    private StringBuilder getTextStyle(String content, String url, String projectName, String milestoneName, String taskName,
                                       String reviewer, String confirmer, String estEndTime, String type,
                                       String circleName, String circleRoleName, String circleRoleUserName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body><p>").append(content).append("</p>");

        sb.append("<table border=\"1\" width=\"500px\" cellspacing=\"0\" cellpadding=\"0\" rules=\"rows\">");
        sb.append("<tr bgcolor=\"#C0C0C0\" align=\"Left\"><th colspan=\"2\">SSDT-敏捷管理系统</th></tr>");

        if (MessageModuleEnum.CIRCLE_ROLE_CHANGE.getType().equals(type)) {
            sb.append("<tr><td width=\"100px\"><b>圈子名称：</b></td><td>").append(circleName).append("</td></tr>");
            sb.append("<tr bgcolor=\"#C0C0C0\"><td width=\"100px\"><b>角色名称：</b></td><td>").append(circleRoleName).append("</td></tr>");
            sb.append("<tr><td width=\"100px\"><b>角色人员：</b></td><td>").append(circleRoleUserName).append("</td></tr>");
        } else {
            sb.append("<tr><td width=\"100px\"><b>项目名：</b></td><td>").append(projectName).append("</td></tr>");
            if (MessageModuleEnum.TASK_WILL_DELAY.getType().equals(type)) {
                sb.append("<tr bgcolor=\"#C0C0C0\"><td width=\"100px\"><b>里程碑：</b></td><td>").append(milestoneName).append("</td></tr>");
                sb.append("<tr><td width=\"100px\"><b>任务名：</b></td><td>").append(taskName).append("</td></tr>");
                sb.append("<tr bgcolor=\"#C0C0C0\"><td width=\"100px\"><b>责任人：</b></td><td>").append(reviewer).append("</td></tr>");
                sb.append("<tr><td width=\"100px\"><b>审核人：</b></td><td>").append(confirmer).append("</td></tr>");
            }
            sb.append("<tr bgcolor=\"#C0C0C0\"><td width=\"100px\"><b>截止时间：</b></td><td>").append(estEndTime).append("</td></tr>");
        }

        sb.append("</table>");

        sb.append("<div class=\"div\"><p>请登录").append("<a href=\"").append(url).append("\">SSDT-敏捷管理系统").append("</a>处理</p></div>");
        sb.append("</body>");
        sb.append("<style type=\"text/css\"> .div a{color:#0033FF}</style>");
        sb.append("</html>");
        return sb;
    }

    /**
     * 发送附件邮件
     */
    @Override
    public void sendAttendedFileMail(String to, String userName, List<AttachmentDTO> attachmentDTOList, String url, String projectName) throws Exception {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // multipart模式
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            mimeMessageHelper.setFrom(fromUserMail);
            mimeMessageHelper.setTo(to); //收件人
            mimeMessageHelper.setSubject("项目上线-关联任务");
        } catch (MessagingException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body><p>亲爱的").append(userName).append("，</p>");
        sb.append("<p>").append(projectName).append("项目上线，附件为与您相关的任务清单，请查收！</p>");
        sb.append("<div class=\"div\"><p>请登录").append("<a href=\"").append(url).append("\">SSDT-敏捷管理系统").append("</a>处理</p></div>");
        sb.append("</body>");
        sb.append("<style type=\"text/css\"> .div a{color:#0033FF}</style>");
        sb.append("</html>");

        // 启用html
        mimeMessageHelper.setText(sb.toString(), true);

        // 设置附件
        HSSFWorkbook workbook = null;
        InputStream in = null;
        try {
            workbook = this.excelDownload(attachmentDTOList);
            //临时缓冲区
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //创建临时文件
            workbook.write(out);
            byte[] bookByteAry = out.toByteArray();
            in = new ByteArrayInputStream(bookByteAry);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (workbook != null && in != null)
            mimeMessageHelper.addAttachment(projectName + ".xls", new ByteArrayResource(IOUtils.toByteArray(in)), "application/vnd.ms-excel;charset=UTF-8");

        // 发送邮件
        mailSender.send(mimeMessage);
    }


    private HSSFWorkbook excelDownload(List<AttachmentDTO> attachmentDTOList) {
        //声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        //生成一个表格，设置表格名称为"学生表"
        HSSFSheet sheet = workbook.createSheet("任务表");

        //创建第一行表头
        HSSFRow headrow = sheet.createRow(0);

        int columnIndex = 0;
        headrow.createCell(columnIndex).setCellValue("里程碑名称");
        headrow.createCell(++columnIndex).setCellValue("父任务名称");
        headrow.createCell(++columnIndex).setCellValue("任务名称");
        headrow.createCell(++columnIndex).setCellValue("负责人");
        headrow.createCell(++columnIndex).setCellValue("审核人");
        headrow.createCell(++columnIndex).setCellValue("截止日期");

        for (int i = 0; i < attachmentDTOList.size(); i++) {
            AttachmentDTO attachmentDTO = attachmentDTOList.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(attachmentDTO.getMilestoneName());
            row.getCell(++columnIndex).setCellValue(attachmentDTO.getParentTaskName());
            row.getCell(++columnIndex).setCellValue(attachmentDTO.getTaskName());
            row.getCell(++columnIndex).setCellValue(attachmentDTO.getReviewUser());
            row.getCell(++columnIndex).setCellValue(attachmentDTO.getConfirmUser());
            row.getCell(++columnIndex).setCellValue(attachmentDTO.getEstEndTime().toString());
        }

        return workbook;
    }


}
