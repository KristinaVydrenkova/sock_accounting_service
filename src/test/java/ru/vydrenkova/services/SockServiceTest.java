package ru.vydrenkova.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.exceptions.IllegalAmountException;
import ru.vydrenkova.exceptions.NoSuchSockException;
import ru.vydrenkova.models.Sock;
import ru.vydrenkova.repositories.SockRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SockServiceTest {

    @Autowired
    private SockService sockService;

    @MockBean
    private SockRepository sockRepository;

    private SockRequest sockRequest;
    private Sock sock;

    @BeforeEach
    void setUp() {
        sockRequest = new SockRequest("red", 70, 100);
        sock = Sock.builder()
                .id(1L)
                .color("red")
                .cottonPercentage(70)
                .amount(100)
                .build();
    }

    @Test
    void testGetSocksAmount() {
        when(sockRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(sock));

        AmountResponse response = sockService.getSocksAmount("red", "equal", 70);

        assertEquals(100, response.getAmount());
    }

    @Test
    void testAddSocks() {
        when(sockRepository.findByColorAndCottonPercentage("red", 70))
                .thenReturn(Optional.empty());
        when(sockRepository.save(any(Sock.class)))
                .thenReturn(sock);

        SockResponse response = sockService.addSocks(sockRequest);

        assertEquals("red", response.getColor());
        assertEquals(70, response.getCottonPercentage());
        assertEquals(100, response.getAmount());
    }

    @Test
    void testRemoveSocks_Success() {
        sock.setAmount(150);
        when(sockRepository.findByColorAndCottonPercentage("red", 70))
                .thenReturn(Optional.of(sock));
        when(sockRepository.save(any(Sock.class)))
                .thenReturn(sock);

        SockResponse response = sockService.removeSocks(sockRequest);

        assertEquals(50, response.getAmount());
    }

    @Test
    void testRemoveSocks_IllegalAmount() {
        sock.setAmount(50);
        when(sockRepository.findByColorAndCottonPercentage("red", 70))
                .thenReturn(Optional.of(sock));

        assertThrows(IllegalAmountException.class, () -> sockService.removeSocks(sockRequest));
    }

    @Test
    void testRemoveSocks_NoSuchSock() {
        when(sockRepository.findByColorAndCottonPercentage("red", 70))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchSockException.class, () -> sockService.removeSocks(sockRequest));
    }

    @Test
    void testUpdateSocks() {
        when(sockRepository.findById(1L))
                .thenReturn(Optional.of(sock));
        when(sockRepository.save(any(Sock.class)))
                .thenReturn(sock);

        SockResponse response = sockService.updateSocks(1L, sockRequest);

        assertEquals("red", response.getColor());
        assertEquals(70, response.getCottonPercentage());
        assertEquals(100, response.getAmount());
    }

    @Test
    void testGetSocksByFilterSorted() {
        when(sockRepository.findAll(any(Specification.class),any(Sort.class)))
                .thenReturn(List.of(sock));

        SocksList response = sockService.getSocksByFilterSorted(50, 80, "color");

        assertEquals(1, response.getSockList().size());
        assertEquals("red", response.getSockList().get(0).getColor());
    }
}
