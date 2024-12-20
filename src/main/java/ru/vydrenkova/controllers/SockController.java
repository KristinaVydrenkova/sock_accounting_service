package ru.vydrenkova.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.api.SockAccountingApi;
import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.services.FileService;
import ru.vydrenkova.services.SockService;

/**
 * The SockController class is a REST controller that implements the SockAccountingApi interface.
 * It provides endpoints for managing socks inventory in a store, including adding, removing, updating,
 * and retrieving socks, as well as uploading batches of socks via CSV files and filtering socks based on various criteria.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class SockController implements SockAccountingApi {

    private final SockService sockService;
    private final FileService fileService;


    /**
     * Registers the arrival of socks in the inventory.
     *
     * @param sockRequest The request containing details of the socks to be added (color, cotton percentage, and amount).
     * @return A ResponseEntity containing the details of the added socks.
     */
    @Override
    public ResponseEntity<SockResponse> addSocks(SockRequest sockRequest) {
        log.info("Received request to add socks: {}", sockRequest);
        SockResponse response = sockService.addSocks(sockRequest);
        log.info("Socks added successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Registers the release of socks from the inventory.
     *
     * @param sockRequest The request containing details of the socks to be removed (color, cotton percentage, and amount).
     * @return A ResponseEntity containing the details of the removed socks.
     */
    @Override
    public ResponseEntity<SockResponse> removeSocks(SockRequest sockRequest) {
        log.info("Received request to remove socks: {}", sockRequest);
        SockResponse response = sockService.removeSocks(sockRequest);
        log.info("Socks removed successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the total quantity of socks based on filtering criteria.
     *
     * @param color     The color of the socks.
     * @param operation The operation to filter socks (moreThan, lessThan, or equal).
     * @param cotton    The cotton percentage to filter socks.
     * @return A ResponseEntity containing the total quantity of socks that match the criteria.
     */
    @Override
    public ResponseEntity<AmountResponse> getSocks(String color, String operation, Integer cotton) {
        log.info("Received request to get socks amount for color={}, operation={}, cotton={}", color, operation, cotton);
        AmountResponse response = sockService.getSocksAmount(color, operation, cotton);
        log.info("Socks amount retrieved successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the details of socks in the inventory.
     *
     * @param id          The ID of the socks to be updated.
     * @param sockRequest The request containing the updated details of the socks.
     * @return A ResponseEntity containing the updated details of the socks.
     */
    @Override
    public ResponseEntity<SockResponse> updateSock(Long id, SockRequest sockRequest) {
        log.info("Received request to update sock with id={}, request={}", id, sockRequest);
        SockResponse response = sockService.updateSocks(id, sockRequest);
        log.info("Sock updated successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Uploads a batch of socks from a CSV file.
     *
     * @param file The CSV file containing the details of the socks to be uploaded.
     * @return A ResponseEntity containing the list of uploaded socks.
     */
    @Override
    public ResponseEntity<SocksList> uploadFile(MultipartFile file) {
        log.info("Received request to upload file: {}", file.getOriginalFilename());
        SocksList response = fileService.processSocksBatch(file);
        log.info("File processed successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of socks filtered by cotton percentage range and sorted by a specified field.
     *
     * @param from      The minimum cotton percentage.
     * @param to        The maximum cotton percentage.
     * @param sortedBy  The field to sort the socks by (color or cotton).
     * @return A ResponseEntity containing the list of filtered and sorted socks.
     */
    @Override
    public ResponseEntity<SocksList> getSocksSorted(Integer from, Integer to, String sortedBy) {
        log.info("Received request to get socks sorted by filter: from={}, to={}, sortedBy={}", from, to, sortedBy);
        SocksList response = sockService.getSocksByFilterSorted(from, to, sortedBy);
        log.info("Socks sorted by filter retrieved successfully: {}", response);
        return ResponseEntity.ok(response);
    }
}