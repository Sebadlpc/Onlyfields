package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.dto.*;
import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para gestionar las suscripciones de los clientes.
 * Expone endpoints para crear, consultar y modificar el estado de las suscripciones.
 */
@RestController
@RequestMapping("/api/v1/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService service;

    /**
     * Crea una nueva suscripción para un cliente a un plan específico.
     * @param dto El DTO con los detalles de la suscripción a crear.
     * @return Un DTO con la información de la suscripción recién creada.
     */
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crear(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSuscripcion(dto));
    }

    /**
     * Obtiene todas las suscripciones de un cliente específico.
     * @param clienteId El ID del cliente.
     * @return Una lista de DTOs con las suscripciones del cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    /**
     * Congela una suscripción activa por un período determinado.
     * @param id El ID de la suscripción a congelar.
     * @param dto El DTO con las fechas de inicio y fin del congelamiento.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/congelar")
    public ResponseEntity<SuscripcionResponseDTO> congelar(@PathVariable Long id, @Valid @RequestBody CongelarRequestDTO dto) {
        return ResponseEntity.ok(service.congelar(id, dto));
    }

    /**
     * Reactiva una suscripción que estaba congelada.
     * @param id El ID de la suscripción a reactivar.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<SuscripcionResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reactivar(id));
    }

    /**
     * Cancela una suscripción.
     * @param id El ID de la suscripción a cancelar.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SuscripcionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    /**
     * Obtiene el historial de cambios de estado de una suscripción.
     * @param id El ID de la suscripción.
     * @return Una lista con el historial de estados.
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstado>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHistorial(id));
    }
}
