package org.profitsoft.profitsoft2.database.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.profitsoft.profitsoft2.database.dto.PhotoDto;
import org.profitsoft.profitsoft2.database.entity.Photo;
import org.profitsoft.profitsoft2.database.entity.User;
import org.profitsoft.profitsoft2.database.repository.PhotoRepository;
import org.profitsoft.profitsoft2.utils.DtoMapper;
import org.profitsoft.profitsoft2.utils.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PhotoService extends DefaultService<Photo, PhotoRepository> {

    private static final UtilService utilService = new UtilService();
    private static final DtoMapper dtoMapper = new DtoMapper();

    /**
     * Saves a photo with the given file, name, description, tags, and user ID.
     *
     * @param file             The multipart file containing the photo data.
     * @param photoName        The name of the photo.
     * @param photoDescription The description of the photo.
     * @param photoTags        The tags associated with the photo.
     * @param userId           The ID of the user uploading the photo.
     */
    public void savePhoto(MultipartFile file, String photoName, String photoDescription, String photoTags, Integer userId) {

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = photoName + extension;
        String filePath = String.format("%d/%s", userId, fileName);

        save(setPhotoFile(
                photoName,
                photoDescription,
                photoTags,
                userId,
                filePath,
                extension.replace(".", "")));

    }

    /**
     * Creates a Photo object with the provided photo details and associated user.
     *
     * @param photoName        The name of the photo.
     * @param photoDescription The description of the photo. Can be null or empty.
     * @param photoTags        The tags associated with the photo. Can be null or empty.
     * @param userId           The ID of the user uploading the photo.
     * @param filePath         The file path of the photo.
     * @param extension        The file extension of the photo.
     * @return The created Photo object.
     */
    private Photo setPhotoFile(String photoName, String photoDescription, String photoTags, Integer userId, String filePath, String extension) {
        Photo photo = new Photo();
        photo.setPhotoName(photoName);
        photo.setPhotoFormat(extension);
        photo.setPhotoPath(filePath);

        if (utilService.isNotEmptyOrNull(photoDescription)) {
            photo.setPhotoDescription(photoDescription);
        }
        if (utilService.isNotEmptyOrNull(photoTags)) {
            photo.setPhotoTags(photoTags);
        }

        photo.setUploadDate(LocalDate.parse(utilService.getCurrentDate()));
        User user = new User();
        user.setId(userId);
        photo.setUser(user);
        return photo;
    }


    public void deletePhoto(Integer id) {
        deleteById(id);
    }

    /**
     * Retrieves a PhotoDto by the given id and maps it to the associated User.
     *
     * @param id The id of the photo to retrieve.
     * @return The PhotoDto associated with the given id, or null if no photo is found.
     */
    public PhotoDto getPhotoAndAssociatedUser(Integer id) {
        return getById(id)
                .map(dtoMapper::mapPhotoToDto)
                .orElse(null);
    }

    /**
     * Updates a photo with the given ID, photo name, photo description, and photo tags.
     *
     * @param id               The ID of the photo to update.
     * @param photoName        The new name of the photo. If null or empty, the existing name will be preserved.
     * @param photoDescription The new description of the photo. If null or empty, the existing description will be preserved.
     * @param photoTags        The new tags of the photo. If null or empty, the existing tags will be preserved.
     */
    public void updatePhoto(Integer id, String photoName, String photoDescription, String photoTags) {
        getById(id)
                .ifPresent(photo -> {
                    if (utilService.isNotEmptyOrNull(photoName)) {
                        photo.setPhotoName(photoName);
                    }
                    if (utilService.isNotEmptyOrNull(photoDescription)) {
                        photo.setPhotoDescription(photoDescription);
                    }
                    if (utilService.isNotEmptyOrNull(photoTags)) {
                        photo.setPhotoTags(photoTags);
                    }
                    jpaRepository.save(photo);
                });
    }

    /**
     * Saves photos from a JSON file.
     *
     * @param jsonFile The multipart file containing the JSON data.
     * @return A JSON object with the total number of photos, the number of valid photos, and the number of invalid photos.
     * If an error occurs, a JSON object with the error message is returned.
     * @throws Exception If the JSON file is empty.
     */
    public JSONObject saveByJson(MultipartFile jsonFile) {
        try {
            List<Photo> rawPhotos = utilService.getParsedJsonList(jsonFile, Photo.class);
            if(!utilService.isNotEmptyOrNull(rawPhotos)) {
                throw new Exception("File is empty");
            }

            int validPhotosCount = 0;
            int invalidPhotosCount = 0;
            for (Photo photo : rawPhotos) {
                if (photo != null) {

                    save(photo);
                    validPhotosCount++;
                } else {
                    invalidPhotosCount++;
                }
            }

            JSONObject jsonResult = new JSONObject();
            jsonResult.put("total", rawPhotos.size());
            jsonResult.put("valid", validPhotosCount);
            jsonResult.put("invalid", invalidPhotosCount);

            return jsonResult;
        } catch (Exception e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }

    /**
     * Retrieves paginated photos based on the specified criteria.
     *
     * @param page        The pageable object specifying the pagination details.
     * @param photoName   The name of the photo. Can be null or empty.
     * @param photoFormat The format of the photo. Can be null or empty.
     * @param uploadDate  The upload date of the photo. Can be null or empty.
     * @return A JSON object containing the list of photos and the total number of pages.
     * If an error occurs, a JSON object with the error message is returned.
     */
    public JSONObject getPhotosPagination(Pageable page, String photoName, String photoFormat, String uploadDate) {
        try {
            Page<Photo> photosPage = jpaRepository.getPhotosPagination(page, photoName, photoFormat, uploadDate);
            Page<PhotoDto> photosDtoPage = photosPage.map(dtoMapper::mapPhotoToShortDto);
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("list", photosDtoPage.getContent());
            jsonResult.put("totalPages", photosDtoPage.getTotalPages());

            return jsonResult;
        } catch (Exception e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }

    /**
     * Generates a report in Excel format based on the given photo filters.
     *
     * @param photoName   The name of the photo. Can be null or empty.
     * @param photoFormat The format of the photo. Can be null or empty.
     * @param uploadDate  The upload date of the photo. Can be null or empty.
     * @return A byte array containing the generated Excel report. If an error occurs, null is returned.
     */
    public byte[] generateReport(String photoName, String photoFormat, String uploadDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Report");

            List<Photo> photos = jpaRepository.getPhotosByFilters(photoName, photoFormat, uploadDate);
            List<PhotoDto> photoDtos = photos.stream().map(dtoMapper::mapPhotoToDto).toList();

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Photo Name");
            headerRow.createCell(1).setCellValue("Photo Format");
            headerRow.createCell(2).setCellValue("Photo Path");
            headerRow.createCell(3).setCellValue("Photo Description");
            headerRow.createCell(4).setCellValue("Photo Tags");
            headerRow.createCell(5).setCellValue("Upload Date");
            headerRow.createCell(6).setCellValue("User Id");

            for (PhotoDto photo : photoDtos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(photo.getPhotoName());
                row.createCell(1).setCellValue(photo.getPhotoFormat());
                row.createCell(2).setCellValue(photo.getPhotoPath());
                row.createCell(3).setCellValue(photo.getPhotoDescription());
                row.createCell(4).setCellValue(photo.getPhotoTags());
                row.createCell(5).setCellValue(photo.getUploadDate().toString());
                row.createCell(6).setCellValue(photo.getUser().getId());
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
