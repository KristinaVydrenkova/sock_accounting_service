package ru.vydrenkova.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.exceptions.EmptyFileException;
import ru.vydrenkova.exceptions.FileReadingException;
import ru.vydrenkova.exceptions.WrongFormatException;
import ru.vydrenkova.exceptions.WrongHeadersException;
import ru.vydrenkova.models.Sock;
import ru.vydrenkova.repositories.SockRepository;
import ru.vydrenkova.services.FileService;
import ru.vydrenkova.utils.constraints.Constraints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * The FileServiceImpl class is the implementation of the FileService interface.
 * It provides a method for processing a batch of socks from a CSV file and returning the processed data.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final SockRepository sockRepository;

    /**
     * Processes a batch of socks from a CSV file.
     *
     * @param file The CSV file containing the details of the socks to be processed.
     * @return A SocksList containing the list of processed socks.
     * @throws EmptyFileException    if the file is empty.
     * @throws WrongFormatException  if the file format is incorrect.
     * @throws WrongHeadersException if the file headers are incorrect.
     * @throws FileReadingException  if an error occurs while reading the file.
     */
    @Override
    public SocksList processSocksBatch(MultipartFile file) {
        log.info("Processing socks batch from file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.warn("File is empty: {}", file.getOriginalFilename());
            throw new EmptyFileException("Файл пустой.");
        }
        if (!checkFormat(file)) {
            log.warn("File has wrong format: {}", file.getOriginalFilename());
            throw new WrongFormatException("Некорректный формат данных.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            if (!checkHeaders(csvParser.getHeaderNames())) {
                log.warn("File has wrong headers: {}", csvParser.getHeaderNames());
                throw new WrongHeadersException("В файле неправильные заголовки.");
            }
            List<Sock> savedSocks = sockRepository.saveAll(
                    csvParser.stream().map(this::processRecord).toList());
            log.info("Successfully processed and saved {} socks from file: {}", savedSocks.size(), file.getOriginalFilename());
            return SocksList.builder()
                    .sockList(savedSocks.stream().map(SockResponse::toResponse).toList())
                    .build();
        } catch (IOException e) {
            log.error("Error reading file: {}", file.getOriginalFilename(), e);
            throw new FileReadingException("Ошибка при чтении файла.");
        }
    }

    private boolean checkFormat(MultipartFile file){
        String fileName = file.getOriginalFilename();
        boolean isValid = !Objects.equals(fileName,null) && fileName.endsWith(Constraints.FORMAT);
        log.debug("File format check result: {} for file: {}", isValid, fileName);
        return isValid;
    }

    private boolean checkHeaders(List<String> headers){
        boolean isValid = headers.size() == Constraints.HEADERS_AMOUNT &&
                headers.contains(Constraints.COLOR_CSV_HEADER_NAME) &&
                headers.contains(Constraints.COTTON_PERCENTAGE_CSV_HEADER_NAME) &&
                headers.contains(Constraints.AMOUNT_CSV_HEADER_NAME);
        log.debug("Headers check result: {} for headers: {}", isValid, headers);
        return isValid;
    }

    private Sock processRecord(CSVRecord csvRecord){
        log.debug("Processing CSV record: {}", csvRecord);
        String color = csvRecord.get(Constraints.COLOR_CSV_HEADER_NAME);
        Integer cottonPercentage = Integer.parseInt(csvRecord.get(Constraints.COTTON_PERCENTAGE_CSV_HEADER_NAME));
        Integer amount = Integer.parseInt(csvRecord.get(Constraints.AMOUNT_CSV_HEADER_NAME));

        return Sock.builder()
                .color(color)
                .cottonPercentage(cottonPercentage)
                .amount(amount)
                .build();
    }
}
