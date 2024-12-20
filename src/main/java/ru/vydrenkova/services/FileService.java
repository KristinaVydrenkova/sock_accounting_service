package ru.vydrenkova.services;

import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.dto.responses.SocksList;

/**
 * The FileService interface defines the service layer for processing CSV files containing socks data.
 * It provides a method for processing a batch of socks from a CSV file and returning the processed data.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
public interface FileService {

    /**
     * Processes a batch of socks from a CSV file.
     *
     * @param file The CSV file containing the details of the socks to be processed.
     * @return A SocksList containing the list of processed socks.
     */
    SocksList processSocksBatch(MultipartFile file);
}