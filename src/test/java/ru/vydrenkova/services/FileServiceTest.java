package ru.vydrenkova.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.exceptions.EmptyFileException;
import ru.vydrenkova.exceptions.FileReadingException;
import ru.vydrenkova.exceptions.WrongFormatException;
import ru.vydrenkova.exceptions.WrongHeadersException;
import ru.vydrenkova.models.Sock;
import ru.vydrenkova.repositories.SockRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private SockRepository sockRepository;

    private MultipartFile validFile;
    private MultipartFile emptyFile;
    private MultipartFile wrongFormatFile;
    private MultipartFile wrongHeadersFile;

    @BeforeEach
    void setUp() throws IOException {
        String validCsvContent = "color,cottonPercentage,amount\nred,70,100\nblue,80,50";
        validFile = new MockMultipartFile("validFile.csv", "validFile.csv", "text/csv", validCsvContent.getBytes());

        emptyFile = new MockMultipartFile("emptyFile.csv", "emptyFile.csv", "text/csv", new byte[0]);

        String wrongFormatContent = "color,cottonPercentage,amount\nred,70,100\nblue,80,50";
        wrongFormatFile = new MockMultipartFile("wrongFormatFile.txt", "wrongFormatFile.txt", "text/plain", wrongFormatContent.getBytes());

        String wrongHeadersContent = "wrongHeader1,wrongHeader2,wrongHeader3\nred,70,100\nblue,80,50";
        wrongHeadersFile = new MockMultipartFile("wrongHeadersFile.csv", "wrongHeadersFile.csv", "text/csv", wrongHeadersContent.getBytes());
    }

    @Test
    void testProcessSocksBatch_ValidFile() {
        when(sockRepository.saveAll(anyList())).thenReturn(List.of(
                Sock.builder().id(1L).color("red").cottonPercentage(70).amount(100).build(),
                Sock.builder().id(2L).color("blue").cottonPercentage(80).amount(50).build()
        ));

        SocksList result = fileService.processSocksBatch(validFile);

        assertEquals(2, result.getSockList().size());
        assertEquals("red", result.getSockList().get(0).getColor());
        assertEquals("blue", result.getSockList().get(1).getColor());
    }

    @Test
    void testProcessSocksBatch_EmptyFile() {
        assertThrows(EmptyFileException.class, () -> fileService.processSocksBatch(emptyFile));
    }

    @Test
    void testProcessSocksBatch_WrongFormatFile() {
        assertThrows(WrongFormatException.class, () -> fileService.processSocksBatch(wrongFormatFile));
    }

    @Test
    void testProcessSocksBatch_WrongHeadersFile() {
        assertThrows(WrongHeadersException.class, () -> fileService.processSocksBatch(wrongHeadersFile));
    }

    @Test
    void testProcessSocksBatch_IOException() throws IOException {
        MultipartFile fileWithIOException = Mockito.mock(MultipartFile.class);
        when(fileWithIOException.getOriginalFilename()).thenReturn("fileWithIOException.csv");
        when(fileWithIOException.isEmpty()).thenReturn(false);
        when(fileWithIOException.getInputStream()).thenThrow(new IOException("Test IOException"));

        assertThrows(FileReadingException.class, () -> fileService.processSocksBatch(fileWithIOException));
    }
}
