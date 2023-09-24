package com.qimu.qiapibackend.controller;

import cn.hutool.core.io.FileUtil;
import com.qimu.qiapibackend.common.BaseResponse;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.common.ResultUtils;
import com.qimu.qiapibackend.constant.FileConstant;
import com.qimu.qiapibackend.manager.CosManager;
import com.qimu.qiapibackend.model.enums.FileUploadBizEnum;
import com.qimu.qiapibackend.model.enums.ImageStatusEnum;
import com.qimu.qiapibackend.model.file.UploadFileRequest;
import com.qimu.qiapibackend.model.vo.ImageVo;
import com.qimu.qiapibackend.model.vo.UserVO;
import com.qimu.qiapibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件接口
 *
 * @author qimu
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    final long ONE_M = 1024 * 1024L;
    @Resource
    private UserService userService;
    @Resource
    private CosManager cosManager;


    /**
     * 上传文件
     *
     * @param multipartFile     多部分文件
     * @param uploadFileRequest 上传文件请求
     * @param request           请求
     * @return {@link BaseResponse}<{@link ImageVo}>
     */
    @PostMapping("/upload")
    public BaseResponse<ImageVo> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        ImageVo imageVo = new ImageVo();
        if (fileUploadBizEnum == null) {
            return uploadError(imageVo, multipartFile, "上传失败,情重试.");
        }
        String result = validFile(multipartFile, fileUploadBizEnum);
        if (!"success".equals(result)) {
            return uploadError(imageVo, multipartFile, result);
        }
        UserVO loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;

        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            imageVo.setName(multipartFile.getOriginalFilename());
            imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
            imageVo.setStatus(ImageStatusEnum.SUCCESS.getValue());
            imageVo.setUrl(FileConstant.COS_HOST + filepath);
            // 返回可访问地址
            return ResultUtils.success(imageVo);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            return uploadError(imageVo, multipartFile, "上传失败,情重试");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    private BaseResponse<ImageVo> uploadError(ImageVo imageVo, MultipartFile multipartFile, String message) {
        imageVo.setName(multipartFile.getOriginalFilename());
        imageVo.setUid(RandomStringUtils.randomAlphanumeric(8));
        imageVo.setStatus(ImageStatusEnum.ERROR.getValue());
        return ResultUtils.error(imageVo, ErrorCode.OPERATION_ERROR, message);
    }

    /**
     * 有效文件
     * 校验文件
     *
     * @param fileUploadBizEnum 业务类型
     * @param multipartFile     多部分文件
     */
    private String validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                return "文件大小不能超过 1M";
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp","jiff").contains(fileSuffix)) {
                return "文件类型错误";
            }
        }
        return "success";
    }
}
