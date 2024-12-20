package ru.vydrenkova.services;

import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;

/**
 * The SockService interface defines the service layer for managing socks inventory in a store.
 * It provides methods for retrieving, adding, removing, updating, and filtering socks,
 * as well as sorting socks based on various criteria.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
public interface SockService {

    /**
     * Retrieves the total quantity of socks based on filtering criteria.
     *
     * @param color           The color of the socks.
     * @param operation       The operation to filter socks (moreThan, lessThan, or equal).
     * @param cottonPercentage The cotton percentage to filter socks.
     * @return An AmountResponse containing the total quantity of socks that match the criteria.
     */
    AmountResponse getSocksAmount(String color, String operation, Integer cottonPercentage);

    /**
     * Adds a new batch of socks to the inventory.
     *
     * @param sockRequest The request containing details of the socks to be added (color, cotton percentage, and amount).
     * @return A SockResponse containing the details of the added socks.
     */
    SockResponse addSocks(SockRequest sockRequest);

    /**
     * Removes a batch of socks from the inventory.
     *
     * @param sockRequest The request containing details of the socks to be removed (color, cotton percentage, and amount).
     * @return A SockResponse containing the details of the removed socks.
     */
    SockResponse removeSocks(SockRequest sockRequest);

    /**
     * Updates the details of socks in the inventory.
     *
     * @param id          The ID of the socks to be updated.
     * @param sockRequest The request containing the updated details of the socks.
     * @return A SockResponse containing the updated details of the socks.
     */
    SockResponse updateSocks(Long id, SockRequest sockRequest);

    /**
     * Retrieves a list of socks filtered by cotton percentage range and sorted by a specified field.
     *
     * @param from      The minimum cotton percentage.
     * @param to        The maximum cotton percentage.
     * @param sortedBy  The field to sort the socks by (color or cotton).
     * @return A SocksList containing the list of filtered and sorted socks.
     */
    SocksList getSocksByFilterSorted(Integer from, Integer to, String sortedBy);
}