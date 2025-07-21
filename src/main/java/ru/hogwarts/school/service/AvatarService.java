package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
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


@Service
@Transactional
public class AvatarService implements ExceptionService {

    @Value("${students.avatar.dir.path}")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentService.findStudent(studentId);
        String fileName = avatarFile.getOriginalFilename();
        if (fileName == null) {
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

        avatarRepository.save(avatar);
    }

    public String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
           throw new IllegalArgumentException("Неверное имя файла" + fileName);
        }
        return fileName.substring(dotIndex + 1);
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
    }

    public byte[] generateDataForDataBase(Path filePath) throws IOException {
        BufferedImage image = ImageIO.read(filePath.toFile());
        int width = 100;
        int height = (int) ((double) image.getHeight() / image.getWidth() * width);

        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = preview.createGraphics();
        graphics2D.drawImage(image, 0, 0, 100, height, null);
        graphics2D.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public List<Avatar> findAllAvatars(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber  - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
