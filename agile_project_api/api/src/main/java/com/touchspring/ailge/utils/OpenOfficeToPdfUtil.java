//package com.touchspring.ailge.utils;
//
//import java.io.*;
//import java.net.ConnectException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import com.artofsolving.jodconverter.DocumentConverter;
//import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
//import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
//import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
//import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
//import com.touchspring.ailge.config.OpenOfficeConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class OpenOfficeToPdfUtil {
//
//    private static Logger logger = LoggerFactory.getLogger(OpenOfficeToPdfUtil.class);
//
//    private static OpenOfficeToPdfUtil openOfficeToPdfUtil;
//
//    @Autowired
//    private OpenOfficeConfig openOfficeConfig;
//
//    public int office2PDF(String sourceFile, String destFile) {
//        try {
//            File inputFile = new File(sourceFile);
//            if (!inputFile.exists()) {
//                return -1;// 找不到源文件, 则返回-1
//            }
//
//            // 如果目标路径不存在, 则新建该路径
//            File outputFile = new File(destFile);
//            if (!outputFile.getParentFile().exists()) {
//                outputFile.getParentFile().mkdirs();
//            }
//
//            String openOfficeHOME = openOfficeConfig.getHome();
//
////             如果从文件中读取的URL地址最后一个字符不是 '\'，则添加'\'
//            if (openOfficeHOME.charAt(openOfficeHOME.length() - 1) != '/') {
//                openOfficeHOME += "/";
//            }
//            /**
//             * todo 优化启动OpenOffice的服务
//             */
//            // 启动OpenOffice的服务
////            String command = openOfficeHOME
////                    + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard &";
////            logger.info("command:"+command);
////            Process pro = Runtime.getRuntime().exec(command);
//            // connect to an OpenOffice.org instance running on port 8100
//            OpenOfficeConnection connection = new SocketOpenOfficeConnection(
//                    "127.0.0.1", 8100);
//            connection.connect();
//
//            // convert
//            DocumentConverter converter = new OpenOfficeDocumentConverter(
//                    connection);
//            converter.convert(inputFile, outputFile);
//
//            // close the connection
//            connection.disconnect();
//            // 关闭OpenOffice服务的进程
////            pro.destroy();
//            logger.info("pdf转换成功");
//            return 0;
//        } catch (ConnectException e) {
//            logger.info("OpenOfficeToPdfUtil"+e);
//            e.printStackTrace();
//        } catch (IOException e) {
//            logger.info("OpenOfficeToPdfUtil"+e);
//            e.printStackTrace();
//        }
//        return 1;
//    }
//
//    /**
//     * 转换文件成pdf
//     *
//     * @param fromFileInputStream:
//     * @throws IOException
//     */
//    public String file2pdf(InputStream fromFileInputStream, String toFilePath, String type) throws IOException {
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String timesuffix = sdf.format(date);
//        String docFileName = null;
//        String htmFileName = null;
//        if("doc".equals(type)){
//            docFileName = "doc_" + timesuffix + ".doc";
//            htmFileName = "doc_" + timesuffix + ".pdf";
//        }else if("docx".equals(type)){
//            docFileName = "docx_" + timesuffix + ".docx";
//            htmFileName = "docx_" + timesuffix + ".pdf";
//        }else if("xls".equals(type)){
//            docFileName = "xls_" + timesuffix + ".xls";
//            htmFileName = "xls_" + timesuffix + ".pdf";
//        }else if("ppt".equals(type)){
//            docFileName = "ppt_" + timesuffix + ".ppt";
//            htmFileName = "ppt_" + timesuffix + ".pdf";
//        }else{
//            return null;
//        }
//
//        File htmlOutputFile = new File(toFilePath + File.separatorChar + htmFileName);
//        File docInputFile = new File(toFilePath + File.separatorChar + docFileName);
//        if (htmlOutputFile.exists())
//            htmlOutputFile.delete();
//        htmlOutputFile.createNewFile();
//        if (docInputFile.exists())
//            docInputFile.delete();
//        docInputFile.createNewFile();
//        /**
//         * 由fromFileInputStream构建输入文件
//         */
//        try {
//            OutputStream os = new FileOutputStream(docInputFile);
//            int bytesRead = 0;
//            byte[] buffer = new byte[1024 * 8];
//            while ((bytesRead = fromFileInputStream.read(buffer)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//
//            os.close();
//            fromFileInputStream.close();
//        } catch (IOException e) {
//        }
//
//        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
//        try {
//            connection.connect();
//        } catch (ConnectException e) {
//            System.err.println("文件转换出错，请检查OpenOffice服务是否启动。");
//        }
//        // convert
////        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
//        DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
//        converter.convert(docInputFile, htmlOutputFile);
//        connection.disconnect();
//        // 转换完之后删除word文件
//        docInputFile.delete();
//        return htmFileName;
//    }
//
//    /**
//     * 获取OpenOfficeToPdfUtil实例
//     */
//    public static synchronized OpenOfficeToPdfUtil getDoc2HtmlUtilinstance() {
//        if (openOfficeToPdfUtil == null) {
//            openOfficeToPdfUtil = new OpenOfficeToPdfUtil();
//        }
//        return openOfficeToPdfUtil;
//    }
//
//    public static void main(String[] args) throws IOException {
//
//        OpenOfficeToPdfUtil doc2HtmlUtilinstance = getDoc2HtmlUtilinstance();
////        doc2HtmlUtilinstance.office2PDF("C:/Users/Admin/Desktop/部署试例.docx", "C:/Users/Admin/Desktop");
//
//        File file = null;
//        FileInputStream fileInputStream = null;
//
//        file = new File("C:/Users/Admin/Desktop/excel导入模板.xlsx");
//        fileInputStream = new FileInputStream(file);
//        doc2HtmlUtilinstance.file2pdf(fileInputStream, "C:/Users/Admin/Desktop", "xls");
//    }
//
//}
