package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.ObjectNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AvatarService implements ExceptionService {

    @Value("${students.avatar.dir.path}")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Method uploadAvatar with student iD {} and avatar file {} invoked", studentId, avatarFile.getOriginalFilename());
        Student student = studentService.findStudent(studentId);
        String fileName = avatarFile.getOriginalFilename();
        if (fileName == null) {
            logger.error("File creation error: original filename is null");
            throw new ObjectNotFoundException(studentId, Avatar.class);
        }
        String extension = getExtension(fileName);
        Path filePath = Path.of(avatarsDir, studentId + "." + extension);
        Files.createDirectories(filePath.getParent());

        Files.write(filePath, avatarFile.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Avatar avatar = findAvatar(studentId);
        avatar.setFilePath(filePath.toString());
        avatar.setStudent(student);
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setData(avatarFile.getBytes());

        logger.debug("Avatar {} successful upload", avatar);
        avatarRepository.save(avatar);
    }

    public String getExtension(String fileName) {
        logger.info("Method getExtension with file name {} invoked", fileName);
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            logger.error("Invalid file name: {}", fileName);
            throw new IllegalArgumentException("Неверное имя файла" + fileName);
        }
        String extension = fileName.substring(dotIndex + 1);
        logger.debug("File extension {} was successfully received", extension);
        return extension;
    }

    public Avatar findAvatar(Long studentId) {
        logger.info("Method findAvatar with student ID {} invoked", studentId);
        Optional<Avatar> avatarOpt = avatarRepository.findByStudentId(studentId);
        if (avatarOpt.isPresent()) {
            logger.debug("Avatar found for student ID {}", studentId);
        } else {
            logger.warn("No avatar found for student ID {}, creating new Avatar", studentId);
        }
        return avatarOpt.orElseGet(Avatar::new);
    }

    public byte[] generateDataForDataBase(Path filePath) throws IOException {
        logger.info("Method generateDataForDataBase with file path {} invoked", filePath);
        BufferedImage image = ImageIO.read(filePath.toFile());
        if (image == null) {
            logger.error("Failed read image from {}", filePath);
            throw new FileNotFoundException("Invalid image file ");
        }
        logger.debug("Image read successfully from {}", filePath);

        int width = 100;
        int height = (int) ((double) image.getHeight() / image.getWidth() * width);
        logger.debug("Calculated preview size: width = {}, height = {}", width, height);

        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = preview.createGraphics();
        graphics2D.drawImage(image, 0, 0, 100, height, null);
        graphics2D.dispose();
        logger.debug("Preview image created");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String extension = getExtension(filePath.getFileName().toString());
            logger.info("Writing preview image as {}", extension);
            boolean writeSuccess =  ImageIO.write(preview, extension, baos);
            if (writeSuccess) {
                logger.info("Preview image successfully written as {}", extension);
            } else {
                logger.warn("Failed to write preview image as {}", extension);
            }

            logger.debug("Preview image successfully written, size = {} bytes", baos.size());
            return baos.toByteArray();
        }
    }

    public List<Avatar> findAllAvatars(Integer pageNumber, Integer pageSize) {
        logger.info("Method findAllAvatars with page number {} and page size {} invoked", pageNumber, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Avatar> avatars = avatarRepository.findAll(pageRequest).getContent();
        logger.debug("Number of all avatars with page number {} " +
                "and page size {} have been found successfully: {}", pageNumber, pageSize, avatars.size());
        return avatars;
    }
}
