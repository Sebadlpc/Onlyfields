package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanRepository planRepository;

    @GetMapping
    public ResponseEntity<List<Plan>> listarTodos() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerDetalle(@PathVariable Long id) {
        return planRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}