package org.profitsoft.profitsoft2;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.profitsoft.profitsoft2.database.dto.PhotoDto;
import org.profitsoft.profitsoft2.database.dto.UserDto;
import org.profitsoft.profitsoft2.database.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoService photoService;

    @Test
    void testUploadPhoto() throws Exception {
        File file = new File("src/main/resources/assets/test.jpg");
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] fileBytes = inputStream.readAllBytes();
            MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", fileBytes);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/photos")
                            .file(multipartFile)
                            .param("photoName", "testPhoto")
                            .param("userId", "123")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Photo uploaded successfully"));
        }
    }

    @Test
    void testUpload() throws Exception {
        when(photoService.saveByJson(Mockito.any(MultipartFile.class))).thenReturn(new JSONObject());

        MockMultipartFile jsonFile = new MockMultipartFile("jsonFile", "test.json", MediaType.APPLICATION_JSON_VALUE, "{\"key\": \"value\"}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/photos/upload")
                        .file(jsonFile))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(photoService, Mockito.times(1)).saveByJson(Mockito.any(MultipartFile.class));
    }

    @Test
    public void testListEndpoint() throws Exception {
        int page = 0;
        int size = 10;
        String photoName = "photo1";
        String photoFormat = "jpg";
        String uploadDate = "2022-01-01";

        when(photoService.getPhotosPagination(PageRequest.of(page, size), photoName, photoFormat, uploadDate)).thenReturn(new JSONObject());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/photos/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("photoName", photoName)
                        .param("photoFormat", photoFormat)
                        .param("uploadDate", uploadDate))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void testReportEndpoint() throws Exception {
        String photoName = "photo1";
        String photoFormat = "jpg";
        String uploadDate = "2022-01-01";
        when(photoService.generateReport(photoName, photoFormat, uploadDate)).thenReturn(new byte[0]);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/photos/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("photoName", photoName)
                        .param("photoFormat", photoFormat)
                        .param("uploadDate", uploadDate))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetPhotoAndAssociatedUser() throws Exception {
        int photoId = 1;

        when(photoService.getPhotoAndAssociatedUser(photoId)).thenReturn(new PhotoDto(1,
                "testPhoto", null, null,
                null, null, null,
                new UserDto(2, "testUser", null,
                        null, null, null)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/photos/" + photoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeletePhoto() throws Exception {
        int photoId = 1;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/photos/" + photoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdatePhoto() throws Exception {
        int photoId = 1;
        String photoName = "photo134";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/photos/" + photoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("photoName", photoName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
