package ru.vydrenkova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;

import javax.validation.constraints.Pattern;

/**
 * The SockAccountingApi interface defines the REST API endpoints for managing socks inventory in a store.
 * It provides methods for adding, removing, updating, and retrieving socks, as well as uploading batches of socks
 * via CSV files and filtering socks based on various criteria.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
@Schema(description = "Service for managing socks inventory in a store")
@RequestMapping("/api/socks")
public interface SockAccountingApi {

    /**
     * Registers the arrival of socks in the inventory.
     *
     * @param sockRequest The request containing details of the socks to be added (color, cotton percentage, and amount).
     * @return A ResponseEntity containing the details of the added socks.
     */
    @Operation(summary = "Register the arrival of socks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks arrival registered successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SockResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/income")
    ResponseEntity<SockResponse> addSocks(@RequestBody SockRequest sockRequest);


    /**
     * Registers the release of socks from the inventory.
     *
     * @param sockRequest The request containing details of the socks to be removed (color, cotton percentage, and amount).
     * @return A ResponseEntity containing the details of the removed socks.
     */
    @Operation(summary = "Register the release of socks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks release registered successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SockResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Socks not found", content = @Content)
    })
    @PostMapping("/outcome")
    ResponseEntity<SockResponse> removeSocks(@RequestBody SockRequest sockRequest);


    /**
     * Retrieves the total quantity of socks based on filtering criteria.
     *
     * @param color     The color of the socks.
     * @param operation The operation to filter socks (moreThan, lessThan, or equal).
     * @param cotton    The cotton percentage to filter socks.
     * @return A ResponseEntity containing the total quantity of socks that match the criteria.
     */
    @Operation(summary = "Get the total quantity of socks with filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total quantity of socks retrieved successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AmountResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @GetMapping
    ResponseEntity<AmountResponse> getSocks(@Parameter(description = "The color of the socks") @RequestParam String color,
                                            @Parameter(description = "The operation to filter socks (moreThan, lessThan, or equal)")
                                            @RequestParam @Pattern(
                                                    regexp = "moreThan|lessThan|equal",
                                                    message = "Invalid operation value")
                                            String operation,
                                            @Parameter(description = "The cotton percentage of the socks") @RequestParam Integer cotton);

    /**
     * Updates the details of socks in the inventory.
     *
     * @param id          The ID of the socks to be updated.
     * @param sockRequest The request containing the updated details of the socks.
     * @return A ResponseEntity containing the updated details of the socks.
     */
    @Operation(summary = "Update the details of socks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks details updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SockResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Socks not found", content = @Content)
    })
    @PutMapping("/{id}")
    ResponseEntity<SockResponse> updateSock(@Parameter(description = "The ID of the socks") @PathVariable("id") Long id,
                                            @RequestBody SockRequest sockRequest);


    /**
     * Uploads a batch of socks from a CSV file.
     *
     * @param file The CSV file containing the details of the socks to be uploaded.
     * @return A ResponseEntity containing the list of uploaded socks.
     */
    @Operation(summary = "Upload a batch of socks from a CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SocksList.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping(value = "/batch", consumes = "multipart/form-data")
    ResponseEntity<SocksList> uploadFile(@RequestParam("file") @Parameter(description = "The CSV file for uploading socks",
            content = @Content(mediaType = "text/csv")) MultipartFile file);

    /**
     * Retrieves a list of socks filtered by cotton percentage range and sorted by a specified field.
     *
     * @param from      The minimum cotton percentage.
     * @param to        The maximum cotton percentage.
     * @param sortedBy  The field to sort the socks by (color or cotton).
     * @return A ResponseEntity containing the list of filtered and sorted socks.
     */
    @Operation(summary = "Get a list of socks filtered by cotton percentage range and sorted by a specified field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of socks retrieved and sorted successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AmountResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @GetMapping("/filter-by-cotton")
    ResponseEntity<SocksList> getSocksSorted(@Parameter(description = "The minimum cotton percentage") @RequestParam Integer from,
                                             @Parameter(description = "The maximum cotton percentage") @RequestParam Integer to,
                                             @Parameter(description = "The field to sort the socks by (color or cotton)") @RequestParam(required = false)
                                             @Pattern(
                                                     regexp = "color|cotton",
                                                     message = "Invalid sorting field value") String sortedBy);
}