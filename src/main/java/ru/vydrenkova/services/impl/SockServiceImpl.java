package ru.vydrenkova.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.vydrenkova.dto.requests.SockRequest;
import ru.vydrenkova.dto.responses.AmountResponse;
import ru.vydrenkova.dto.responses.SockResponse;
import ru.vydrenkova.dto.responses.SocksList;
import ru.vydrenkova.exceptions.IllegalAmountException;
import ru.vydrenkova.exceptions.NoSuchSockException;
import ru.vydrenkova.models.Sock;
import ru.vydrenkova.repositories.SockRepository;
import ru.vydrenkova.services.SockService;
import ru.vydrenkova.utils.sorts.SockSort;
import ru.vydrenkova.utils.specifications.SockSpecification;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The SockServiceImpl class is the implementation of the SockService interface.
 * It provides methods for managing socks inventory in a store, including retrieving, adding,
 * removing, updating, and filtering socks, as well as sorting socks based on various criteria.
 *
 * @author Kristina Vydrenkova
 * @version 1.0
 * @since 20.12.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SockServiceImpl implements SockService {
    private final SockRepository sockRepository;

    /**
     * Retrieves the total quantity of socks based on filtering criteria.
     *
     * @param color           The color of the socks.
     * @param operation       The operation to filter socks (moreThan, lessThan, or equal).
     * @param cottonPercentage The cotton percentage to filter socks.
     * @return An AmountResponse containing the total quantity of socks that match the criteria.
     */
    @Override
    public AmountResponse getSocksAmount(String color, String operation, Integer cottonPercentage) {
        log.info("Getting socks amount for color={}, operation={}, cottonPercentage={}", color, operation, cottonPercentage);

        Specification<Sock> spec = Specification
                .where(SockSpecification.hasColor(color))
                .and(SockSpecification.hasCottonPercentage(operation, cottonPercentage));
        int totalAmount = sockRepository.findAll(spec).stream().mapToInt(Sock::getAmount).sum();
        log.info("Found total amount: {}", totalAmount);

        return AmountResponse.builder()
                .amount(totalAmount)
                .build();
    }

    /**
     * Adds a new batch of socks to the inventory.
     *
     * @param sockRequest The request containing details of the socks to be added (color, cotton percentage, and amount).
     * @return A SockResponse containing the details of the added socks.
     */
    @Override
    public SockResponse addSocks(SockRequest sockRequest) {
        log.info("Adding socks: {}", sockRequest);

        Optional<Sock> sockOptional = findSock(sockRequest);
        Sock sock;
        if (sockOptional.isPresent()) {
            sock = sockOptional.get();
            sock.setAmount(sock.getAmount() + sockRequest.getAmount());
        } else {
            sock = createNewSock(sockRequest);
        }
        SockResponse response = SockResponse.toResponse(sockRepository.save(sock));
        log.info("Socks added successfully: {}", response);
        return response;
    }

    /**
     * Removes a batch of socks from the inventory.
     *
     * @param sockRequest The request containing details of the socks to be removed (color, cotton percentage, and amount).
     * @return A SockResponse containing the details of the removed socks.
     * @throws IllegalAmountException if the requested amount exceeds the available amount.
     * @throws NoSuchSockException    if the socks are not found in the inventory.
     */
    @Override
    public SockResponse removeSocks(SockRequest sockRequest) {
        log.info("Removing socks: {}", sockRequest);

        Optional<Sock> sockOptional = findSock(sockRequest);
        if (sockOptional.isPresent()) {
            Sock sock = sockOptional.get();
            if (sock.getAmount() < sockRequest.getAmount()) {
                log.warn("Illegal amount: requested={}, available={}", sockRequest.getAmount(), sock.getAmount());
                throw new IllegalAmountException("Носков на складе меньше.");
            } else if (sock.getAmount().equals(sockRequest.getAmount())) {
                return deleteSock(sock);
            } else {
                return decreaseAmount(sockRequest, sock);
            }
        } else {
            log.warn("No such socks: {}", sockRequest);
            throw new NoSuchSockException("На складе нет таких носков.");
        }
    }

    /**
     * Updates the details of socks in the inventory.
     *
     * @param id          The ID of the socks to be updated.
     * @param sockRequest The request containing the updated details of the socks.
     * @return A SockResponse containing the updated details of the socks.
     * @throws NoSuchSockException if the socks are not found in the inventory.
     */
    @Override
    public SockResponse updateSocks(Long id, SockRequest sockRequest) {
        log.info("Updating socks with id={}, request={}", id, sockRequest);

        Optional<Sock> sock = sockRepository.findById(id);
        if (sock.isPresent()) {
            Sock updatedSock = updateParameters(sockRequest, sock.get());
            log.info("Socks updated successfully: {}", updatedSock);
            return SockResponse.toResponse(updatedSock);
        } else {
            log.warn("No such socks with id={}", id);
            throw new NoSuchSockException("На складе нет носков с id = " + id);
        }
    }

    /**
     * Retrieves a list of socks filtered by cotton percentage range and sorted by a specified field.
     *
     * @param from      The minimum cotton percentage.
     * @param to        The maximum cotton percentage.
     * @param sortedBy  The field to sort the socks by (color or cotton).
     * @return A SocksList containing the list of filtered and sorted socks.
     */
    @Override
    public SocksList getSocksByFilterSorted(Integer from, Integer to, String sortedBy) {
        log.info("Getting socks by filter: from={}, to={}, sortedBy={}", from, to, sortedBy);

        List<Sock> socksList;
        Specification<Sock> specification = SockSpecification.cottonPercentageBetween(from, to);
        if (!Objects.equals(sortedBy, null)) {
            Sort sort = SockSort.byField(sortedBy);
            socksList = sockRepository.findAll(specification, sort);
        } else {
            socksList = sockRepository.findAll(specification);
        }
        log.info("Found socks: {}", socksList);
        return SocksList.builder()
                .sockList(socksList.stream().map(SockResponse::toResponse).toList())
                .build();
    }

    private Optional<Sock> findSock(SockRequest sockRequest) {
        log.debug("Finding sock: {}", sockRequest);
        return sockRepository.findByColorAndCottonPercentage(
                sockRequest.getColor(),
                sockRequest.getCottonPercentage());
    }

    private Sock createNewSock(SockRequest sockRequest) {
        log.debug("Creating new sock: {}", sockRequest);

        return Sock.builder()
                .color(sockRequest.getColor())
                .cottonPercentage(sockRequest.getCottonPercentage())
                .amount(sockRequest.getAmount())
                .build();
    }

    private SockResponse deleteSock(Sock sock) {
        log.info("Deleting sock: {}", sock);

        sockRepository.delete(sock);
        return new SockResponse();
    }

    private SockResponse decreaseAmount(SockRequest sockRequest, Sock sock) {
        log.info("Decreasing amount for sock: {}, request={}", sock, sockRequest);

        sock.setAmount(sock.getAmount() - sockRequest.getAmount());
        sockRepository.save(sock);
        return SockResponse.builder()
                .id(sock.getId())
                .color(sock.getColor())
                .cottonPercentage(sock.getCottonPercentage())
                .amount(sock.getAmount())
                .build();
    }

    private Sock updateParameters(SockRequest sockRequest, Sock s) {
        log.debug("Updating parameters for sock: {}, request={}", s, sockRequest);

        if (!(Objects.equals(null, sockRequest.getColor()))) {
            s.setColor(sockRequest.getColor());
        }
        if (!(Objects.equals(null, sockRequest.getCottonPercentage()))) {
            s.setCottonPercentage(sockRequest.getCottonPercentage());
        }
        if (!(Objects.equals(null, sockRequest.getAmount()))) {
            s.setAmount(sockRequest.getAmount());
        }
        return sockRepository.save(s);
    }

}
