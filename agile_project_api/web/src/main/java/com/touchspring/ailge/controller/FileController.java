package com.touchspring.ailge.controller;

import com.touchspring.ailge.config.SystemConfig;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.ailge.component.OfficeConverterComponent;
import com.touchspring.core.mvc.ui.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;


@RestController
@Slf4j
public class FileController {

    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private DocumentConverter converter;  //用于转换
    @Autowired
    private OfficeConverterComponent officeToHtmlUtil;


    @RequestMapping("/upload")
    public ResultData imageUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                                  @RequestHeader(SystemConfig.TOKEN) String token) {
        SysUser user = JWTUtils.unSign(token, SysUser.class);
        String realPath = new File("").getAbsolutePath();

        String name = file.getOriginalFilename();

        if(StringUtils.isEmpty(name)) return ResultData.errorRequest();

        /**
         * 上传文档
         */
        String dateFilePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String canonicalPath = File.separator + "data" + File.separator + dateFilePath + File.separator + user.getAccount() ;
        // 判断文件夹中的文是否存在 如果存在则套一层文件夹
        String dirPath = realPath + canonicalPath;
        File imagesFile = new File(dirPath);
        if (!imagesFile.exists()) {
            imagesFile.mkdirs();
        }
        String fullImagePath = dirPath + File.separator + name;

        File targetFile = new File(fullImagePath);
        String timeMillis = "";
        if (targetFile.exists()) {
            timeMillis =  System.currentTimeMillis() + "";
            fullImagePath = dirPath + File.separator + timeMillis;
            File _file = new File(fullImagePath);
            _file.mkdirs();
            targetFile = new File( fullImagePath + File.separator + name);
        }
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String webPath = canonicalPath + File.separator + timeMillis;

        // 转化pdf
        String prefix = (name.substring(0,name.lastIndexOf("."))).toLowerCase();
        String suffix = (name.substring (name.lastIndexOf(".") + 1)).toLowerCase();
        if (suffix.equals("xls")
                || suffix.equals("xlsx")
                || suffix.equals("doc")
                || suffix.equals("docx")
                || suffix.equals("txt")
                || suffix.equals("ppt")
                || suffix.equals("pptx")) {
            officeToHtmlUtil.converterToPdf(realPath + File.separator + webPath + File.separator + name,
                    realPath + File.separator + webPath + File.separator + prefix + ".pdf");
        }
        return ResultData.ok().putDataValue("path", webPath + File.separator + name);
    }

    @RequestMapping("/file/{path}/{date}/{fileName}")
    public ResponseEntity<FileSystemResource> imageUpload(@PathVariable("path") String path,
                                                          @PathVariable("date") String date,
                                                          @RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        String realPath = new File("").getAbsolutePath();
        File file = new File(realPath + File.separator + path + File.separator + date + File.separator + Objects.requireNonNull(multipartFile.getOriginalFilename()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }



    @RequestMapping(value = "/file/download", method = RequestMethod.GET)
    public void handleFileDownload(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //文件名
        String filePath = request.getParameter("filePath");
        response.setCharacterEncoding("utf-8");
        String _filePath = filePath.replace("\\\\","/");
        try {
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName="
                    + new String(_filePath.substring(_filePath.lastIndexOf("/") + 1, _filePath.length()).getBytes("GBK"), "ISO8859-1"));
            try {
                String realPath = new File("").getAbsolutePath();
                //文件存放位置
                String path = realPath + File.separator + filePath;
                File file = new File(path);
//            file.setReadable(true, false);
                if (!file.exists()) {
                    return ;// 找不到源文件, 则返回-1
                }
                InputStream inputStream = new FileInputStream(file);
                OutputStream os = response.getOutputStream();
                byte[] b = new byte[1024 * 1024];
                int length;
                while ((length = inputStream.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                inputStream.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {

        }
    }

    /**
     * 预览
     * @param sourceFile 输入地址
     * @return .
     */
    @GetMapping("preview")
    public ResultData toPdfFile(String sourceFile) throws OfficeException {
        if (StringUtils.isEmpty(sourceFile)) return ResultData.errorRequest();
        String realPath = new File("").getAbsolutePath();
        String prefix = (sourceFile.substring(0,sourceFile.lastIndexOf("."))).toLowerCase();
        String converterFile = prefix + ".pdf";
        File file = new File(realPath + File.separator + converterFile);//需要转换的文件
        if (file.exists()) {
            return ResultData.ok().putDataValue("previewPath", converterFile);
        }
        // 查找转换文件
        File _file = null;
        try {
            _file = new File(realPath + File.separator + sourceFile);//需要转换的文件
        } catch (Exception e) {
            return ResultData.notFound();
        }

        if (!_file.exists()) return ResultData.notFound();
        officeToHtmlUtil.converterToPdf(realPath + File.separator + sourceFile,
                realPath + File.separator + converterFile);
        return ResultData.ok().putDataValue("previewPath", converterFile);
    }

    /**
     * 预览
     * @param sourceFile 输入地址
     * @return .
     */
    @GetMapping("preview1")
    public ResultData preview1(String sourceFile) throws OfficeException {
        String realPath = new File("").getAbsolutePath();
        File file = new File(realPath + File.separator + sourceFile);//需要转换的文件
        String previewPath = "";
        if (file.exists()) {
            previewPath = sourceFile;
        }
        // 查找转换文件
        return ResultData.ok().putDataValue("previewPath", previewPath);
    }


    @RequestMapping(value = "/file/check", method = RequestMethod.GET)
    public ResultData handleFileCheck(HttpServletRequest request) {
        String realPath = new File("").getAbsolutePath();
        //文件名
        String filePath = request.getParameter("filePath");
        File file = new File(realPath + File.separator + filePath);//需要转换的文件
       return ResultData.ok().putDataValue("check",file.exists());
    }



}
