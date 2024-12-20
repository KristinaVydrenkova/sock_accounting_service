package ru.vydrenkova.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.services.FileService;
import ru.vydrenkova.services.SockService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SockController.class)
class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @MockBean
    private FileService fileService;

    private SockRequest sockRequest;
    private SockResponse sockResponse;
    private AmountResponse amountResponse;
    private SocksList socksList;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        sockRequest = new SockRequest("red", 70, 100);
        sockResponse = new SockResponse(1L, "red", 70, 100);
        amountResponse = new AmountResponse(100);
        socksList = new SocksList(List.of(sockResponse));
    }

    @Test
    void testAddSocks() throws Exception {
        when(sockService.addSocks(any(SockRequest.class))).thenReturn(sockResponse);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"red\",\"cottonPercentage\":70,\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.color").value("red"))
                .andExpect(jsonPath("$.cottonPercentage").value(70))
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void testRemoveSocks() throws Exception {
        when(sockService.removeSocks(any(SockRequest.class))).thenReturn(sockResponse);

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"red\",\"cottonPercentage\":70,\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.color").value("red"))
                .andExpect(jsonPath("$.cottonPercentage").value(70))
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void testGetSocks() throws Exception {
        when(sockService.getSocksAmount("red", "equal", 70)).thenReturn(amountResponse);

        // Выполнение теста
        mockMvc.perform(get("/api/socks")
                        .param("color", "red")
                        .param("operation", "equal")
                        .param("cotton", "70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100));
    }

//    @Test
//    void testUpdateSock() throws Exception {
//        when(sockService.updateSocks(1L, any(SockRequest.class))).thenReturn(sockResponse);
//
//        mockMvc.perform(put("/api/socks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"color\":\"red\",\"cottonPercentage\":70,\"amount\":100}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.color").value("red"))
//                .andExpect(jsonPath("$.cottonPercentage").value(70))
//                .andExpect(jsonPath("$.amount").value(100));
//    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "color,cottonPercentage,amount\nred,70,100".getBytes());
        when(fileService.processSocksBatch(any(MultipartFile.class))).thenReturn(socksList);

        mockMvc.perform(multipart("/api/socks/batch").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sockList[0].id").value(1))
                .andExpect(jsonPath("$.sockList[0].color").value("red"))
                .andExpect(jsonPath("$.sockList[0].cottonPercentage").value(70))
                .andExpect(jsonPath("$.sockList[0].amount").value(100));
    }

    @Test
    void testGetSocksSorted() throws Exception {
        when(sockService.getSocksByFilterSorted(50, 80, "color")).thenReturn(socksList);

        mockMvc.perform(get("/api/socks/filter-by-cotton")
                        .param("from", "50")
                        .param("to", "80")
                        .param("sortedBy", "color"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sockList[0].id").value(1))
                .andExpect(jsonPath("$.sockList[0].color").value("red"))
                .andExpect(jsonPath("$.sockList[0].cottonPercentage").value(70))
                .andExpect(jsonPath("$.sockList[0].amount").value(100));
    }
}
