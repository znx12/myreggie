package com.example.reggie_take_out.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    /**
     * 文件的上传
     *
     * @param file 参数名要和前端保持一致
     */
    //上传路径：
    /*
    *在application.yml中配置
    reggie:
  		path: C:\MR.Li\reggie_img\
  	*/
    @Value("${reggie.path}")
    private String path;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("file:" + file);

        //获取上传的原始文件名
        String originalFilename = file.getOriginalFilename();

        //获取上传的原始文件名的后缀名(.jpg,.pmg等)
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //字符串拼接
        String fileName = UUID.randomUUID() + suffix;

        //判断path目录存不存在
        File basepath = new File(path);
        if (!basepath.exists()) {
            //不存在则创建该目录
            basepath.mkdirs();
        }

        File file_name = new File(path + fileName);
        try {
            //路径转移,File dest为目的路径
            file.transferTo(file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //响应内容为上传文件的fileName
        return R.success(fileName);
    }

    /**
     * 文件的下载
     *
     * @param response
     * @param name     :需要下载的文件名
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fileInputStream = null;

        ServletOutputStream outputStream = null;

        File file = new File(path + name);

        try {
            //创建一个输入流来读取下载的文件内容
            fileInputStream = new FileInputStream(file);

            //通过输出流将文件内容显示到浏览器
            outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1024];
//            while ((fileInputStream.read(bytes)) != -1) {
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
