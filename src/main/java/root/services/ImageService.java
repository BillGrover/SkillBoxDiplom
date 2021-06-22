package root.services;

import org.apache.commons.lang.RandomStringUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import root.dto.ErrorsDto;
import root.dto.responses.MainResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.path}")
    private String uploadPath;
    private StringBuilder path;
    @Value("${maxFileSize}")
    private long maxFileSize;
    private final int SUB_DIR_NAME_LENGTH = 3;
    private final int SUBDIRECTORY_AMOUNT = 3;
    @Value("${avatar.width}")
    private int avatarWidth;
    @Value("${avatar.height}")
    private int avatarHeight;
    @Value("${picture.width}")
    private int pictureWidth;
    @Value("${picture.height}")
    private int pictureHeight;
    @Value("${avatar.path}")
    private String avatarPath;

    /**
     * Метод проверяет корректность файла и сохраняет его.
     *
     * @param imageFile файл.
     * @return адрес вида /папка/папка/имяФайла.txt
     */
    public ResponseEntity<?> store(MultipartFile imageFile) {
        String error = getError(imageFile);
        if (error != null)
            return badRequest(error);

        path = new StringBuilder(uploadPath);
        String responsePath = "File not found";
        try {
            BufferedImage bufferedImage = trimImage(imageFile);

            path = updatePath(path, 0);
            Files.createDirectories(Paths.get(path.toString()));
            responsePath = path
                    .append("/")
                    .append(UUID.randomUUID().toString())
                    .append(".")
                    .append(imageFile.getOriginalFilename())
                    .toString();
            Path path = Paths.get(responsePath).toAbsolutePath();

//            imageFile.transferTo(path.toFile());
            ImageIO.write(bufferedImage, getFileExtension(imageFile), new File(String.valueOf(path)));

        } catch (IOException e) {
            System.out.println("Ошибка сохранения файла\n" + e.getMessage());
        }
        return ResponseEntity.ok(responsePath);
    }

    private BufferedImage trimImage(MultipartFile file) throws IOException {
        BufferedImage img = convertToBufferedImage(file);
        if (img.getHeight() > pictureHeight || img.getWidth() > pictureWidth)
            return resizeImage(img, pictureWidth, pictureHeight);
        return img;
    }

    public String getError(MultipartFile file) {
        if (file == null)
            return ("File not found");
        if (!getFileExtension(file).equals("jpg") && !getFileExtension(file).equals("png"))
            return ("Допускаются только форматы jpg и png.");
        if (file.getSize() > maxFileSize)
            return ("Размер файла превышает допустимый размер (" + maxFileSize / 1024 + ") kB.");
        return null;
    }

    /**
     * Метод создает вложенные друг в друга папки и даёт им рандомные названия.
     *
     * @param directoryPath             начальная папка.
     * @param currentSubdirectoryAmount количество уровней вложенности папок.
     * @return путь до конечной папки.
     */
    private StringBuilder updatePath(StringBuilder directoryPath, int currentSubdirectoryAmount) {
        if (currentSubdirectoryAmount < SUBDIRECTORY_AMOUNT) {
            currentSubdirectoryAmount++;
            directoryPath = updatePath(
                    directoryPath
                            .append("/")
                            .append(RandomStringUtils.random(SUB_DIR_NAME_LENGTH, true, false)),
                    currentSubdirectoryAmount);
        }
        return directoryPath;
    }

    private ResponseEntity<?> badRequest(String errorText) {
        ErrorsDto error = new ErrorsDto();
        error.setImage(errorText);
        return new ResponseEntity<>(
                new MainResponse(false, error),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Метод определяет расширение файла и возвращает его в виде строки.
     *
     * @param file файл.
     * @return расширение файла.
     */
    private static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else {
            return "";
        }
    }

    /**
     * Метод сохраняет аватарки на диске.
     * Если у юзера уже была аватарка, то новая сохранится в ту же папку (переменная oldPath).
     * Если у юзера не было аватарки, создастся новая папка с рандомным названием.
     *
     * @param mpFile  аватарка в виде MultipartFile
     * @param oldPath путь к текущей аватарке юзера. null, если она не установлена.
     * @return путь к новой аватарке
     */
    public String uploadAvatar(MultipartFile mpFile, String oldPath) {
        String resultPath = null;
        try {
            BufferedImage bufferedImage = convertToBufferedImage(mpFile);
            BufferedImage resizedImage = resizeImage(bufferedImage, avatarWidth, avatarHeight);
            String fileExtension = getFileExtension(mpFile);

            if (oldPath == null || oldPath.equals("")) {
                resultPath = createNewPath(mpFile);
            } else {
                File oldPathDirectory = new File(oldPath);
                if (oldPathDirectory.exists())
                    resultPath = oldPath + "/" + mpFile.getOriginalFilename();
                else
                    resultPath = createNewPath(mpFile);
            }

            ImageIO.write(resizedImage, fileExtension, new File(resultPath));

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении аватарки");
            e.printStackTrace();
        }

        return "/" + resultPath;
    }

    /**
     * Метод возвращает путь, состоящий из папки с рандомным названием и имени передаваемого в метод файла.
     * @param mpFile
     * @return
     * @throws IOException
     */
    private String createNewPath(MultipartFile mpFile) throws IOException {
        String resultPath;
        String userDirectory = RandomStringUtils.random(5, true, true);
        resultPath = avatarPath + "/" + userDirectory + "/" + mpFile.getOriginalFilename();
        Files.createDirectories(Paths.get(avatarPath + "/" + userDirectory + "/"));
        return resultPath;
    }

    /**
     * Метод уменьшает разрешение картинки до нужного (устанавливается в property.yaml)
     *
     * @param originalImage исходный файл картинки
     * @param targetWidth   требуемая ширина
     * @param targetHeight  требуемая высота
     * @return картинка нужных размеров в виде BufferedImage
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, targetWidth, targetHeight);
    }

    /**
     * Метод конвертирует MultipartFile в BufferedImage
     *
     * @param mpFile MultipartFile
     * @return BufferedImage
     * @throws IOException пробрасывает исключение выше, если конвертация не удалась
     */
    private BufferedImage convertToBufferedImage(MultipartFile mpFile) throws IOException {
        return ImageIO.read(mpFile.getInputStream());
    }
}