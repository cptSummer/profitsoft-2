package org.profitsoft.profitsoft2.controllers;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.profitsoft.profitsoft2.database.dto.PhotoDto;
import org.profitsoft.profitsoft2.database.service.PhotoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                              @RequestParam("photoName") String photoName,
                                              @RequestParam(value = "photoDescription", required = false) String photoDescription,
                                              @RequestParam(value = "photoTags", required = false) String photoTags,
                                              @RequestParam("userId") Integer userId) {
        try {
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            } else {
                try {
                    photoService.savePhoto(file, photoName, photoDescription, photoTags, userId);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving photo: " + e.getMessage());
                }
                return ResponseEntity.ok("Photo uploaded successfully");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("jsonFile") MultipartFile jsonFile) {
        return photoService.saveByJson(jsonFile).toMap();
    }

    /**
     * Endpoint to list photos with pagination.
     *
     * @param page        The page number (default: 0).
     * @param size        The number of photos per page (default: 10).
     * @param photoName   The name of the photo (optional).
     * @param photoFormat The format of the photo (optional).
     * @param uploadDate  The date the photo was uploaded (optional).
     * @return A map containing the short list of photo details and total number of pages .
     */
    @PostMapping(value = "/_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                    @RequestParam(value = "photoName", required = false) String photoName,
                                    @RequestParam(value = "photoFormat", required = false) String photoFormat,
                                    @RequestParam(value = "uploadDate", required = false) String uploadDate) {
        JSONObject jsonResult = photoService.getPhotosPagination(PageRequest.of(page, size), photoName, photoFormat, uploadDate);
        return jsonResult.toMap();
    }
    /**
     * Endpoint to generate a report based on the provided photo details.
     *
     * @param photoName    The name of the photo.
     * @param photoFormat  The format of the photo.
     * @param uploadDate   The date the photo was uploaded.
     * @return ResponseEntity containing the generated report data.
     */
    @PostMapping("/_report")
    public ResponseEntity<byte[]> report(@RequestParam(value = "photoName", required = false) String photoName,
                                         @RequestParam(value = "photoFormat", required = false) String photoFormat,
                                         @RequestParam(value = "uploadDate", required = false) String uploadDate) {
        byte[] reportData = photoService.generateReport(photoName, photoFormat, uploadDate);
        if (reportData == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        String fileName = "report.xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        photoService.deletePhoto(id);
    }

    @GetMapping("/{id}")
    public PhotoDto getPhotoAndAssociatedUser(@PathVariable("id") Integer id) {
        return photoService.getPhotoAndAssociatedUser(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Integer id,
                       @RequestParam(value = "photoName", required = false) String photoName,
                       @RequestParam(value = "photoDescription", required = false) String photoDescription,
                       @RequestParam(value = "photoTags", required = false) String photoTags) {
        photoService.updatePhoto(id, photoName, photoDescription, photoTags);
    }
}

