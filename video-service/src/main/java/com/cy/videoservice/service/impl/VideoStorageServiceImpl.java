package com.cy.videoservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.videoservice.config.VideoStorageProperties;
import com.cy.videoservice.service.VideoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStorageServiceImpl implements VideoStorageService {

    private final VideoStorageProperties properties;

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCode.VALIDATION_ERROR, "请上传有效的视频文件");
        }
        Path root = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
            String original = file.getOriginalFilename();
            String extension = "";
            if (StringUtils.hasText(original) && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }
            String folder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            Path folderPath = root.resolve(folder);
            Files.createDirectories(folderPath);
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;
            Path target = folderPath.resolve(filename);
            file.transferTo(target);
            return folder + "/" + filename;
        } catch (IOException e) {
            log.error("store video file failed", e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "视频保存失败，请稍后重试");
        }
    }

    @Override
    public Resource load(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "文件不存在");
        }
        Path root = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        Path filePath = root.resolve(relativePath).normalize();
        if (!filePath.startsWith(root) || !Files.exists(filePath)) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "文件不存在");
        }
        return new FileSystemResource(filePath);
    }

    @Override
    public void delete(String relativePath) {
        if (!StringUtils.hasText(relativePath) || relativePath.contains("://")) {
            return;
        }
        Path root = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        Path filePath = root.resolve(relativePath).normalize();
        if (!filePath.startsWith(root)) {
            return;
        }
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("delete video file failed: {}", filePath, e);
        }
    }

    @Override
    public String generateCover(String videoRelativePath) {
        if (!StringUtils.hasText(videoRelativePath) || videoRelativePath.contains("://")) {
            return null;
        }
        Path root = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        Path videoPath = root.resolve(videoRelativePath).normalize();
        if (!videoPath.startsWith(root) || !Files.exists(videoPath)) {
            return null;
        }
        Path folderPath = videoPath.getParent();
        if (folderPath == null) {
            return null;
        }
        String originalName = videoPath.getFileName().toString();
        String baseName = originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf('.'))
                : originalName;
        Path coverPath = folderPath.resolve(baseName + ".jpg");
        try (SeekableByteChannel channel = NIOUtils.readableChannel(videoPath.toFile())) {
            FrameGrab grab = FrameGrab.createFrameGrab(channel);
            grab.seekToSecondPrecise(0);
            Picture picture = grab.getNativeFrame();
            if (picture == null) {
                return null;
            }
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            Files.createDirectories(folderPath);
            ImageIO.write(bufferedImage, "jpg", coverPath.toFile());
            return root.relativize(coverPath).toString().replace('\\', '/');
        } catch (Exception ex) {
            log.warn("generate cover failed for {}", videoRelativePath, ex);
            return null;
        }
    }
}
