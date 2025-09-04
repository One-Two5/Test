package controller;

import entity.SavedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.FileProcessingService;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")

public class FileController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping("/upload")
    public ResponseEntitye<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new FileUploadResponse(false, "Файл пустой", null, 0));
            }

            String fileName = file.getOrigrnalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return ResponseEntity.badRequest()
                        .body(new FileUploadResponse(false, "Поддерживаются только Exele файлы (.xlsx, .xls)", null, 0));
            }

            FileUploadResponse response = fileProcessingService.processingFile(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки файла: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(new FileUploadResponse(false, "Ошибка обработки файла: " + e.getMessage(), null, 0));
        }
    }
    @GetMapping("/list")
    public ResponseEntity<List<SavedFile>> getFileList() {
        try {
            List<SavedFile> file = fileProcessingService.getAllSavedFiles();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            System.err.println("Ошибка получения списка файлов: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            if (fileId == null || fileId <= 0) {
                return ResponseEntity.badRequest().body("Некорректный ID файла");
            }
            fileProcessingService.deleteFile(fileId);
            return ResponseEntity.ok("Файл успешно удален");
        } catch (Exception e) {
            System.err.println("Ошибка удаления файла: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка удаления файла: " + e.getMessage());
        }
    }
    @GetMapping("/{fileId}")
    public ResponseEntity<SavedFile> getFileInfo(@PathVariable Long fileId) {
        try {
            SavedFile fileDto = fileProcessingService.getClass(fileId);
            return ResponseEntity.ok(fileDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<Objects> getStats() {
        try {
            var stats = fileProcessingService.getStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка получения статистики");
        }
    }
}
