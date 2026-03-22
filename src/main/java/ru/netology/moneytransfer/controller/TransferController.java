package ru.netology.moneytransfer.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.moneytransfer.dto.ConfirmRequestDto;
import ru.netology.moneytransfer.dto.OperationIdResponseDto;
import ru.netology.moneytransfer.dto.TransferRequestDto;
import ru.netology.moneytransfer.service.TransferService;

/**
 * REST-контроллер эндпоинтов /transfer и /confirmOperation (FRONT).
 */
@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<OperationIdResponseDto> transfer(@Valid @RequestBody TransferRequestDto body) {
        return ResponseEntity.ok(transferService.initiateTransfer(body));
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<OperationIdResponseDto> confirm(@Valid @RequestBody ConfirmRequestDto body) {
        return ResponseEntity.ok(transferService.confirmOperation(body));
    }
}
