package com.fullstack.inventario.Service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> listarPorProducto(Long productoId) {
        // Retorna el historial inmutable solicitado por ms-reportes[cite: 1]
        return movimientoRepository.findByProductoIdOrderByFechaHoraDesc(productoId);
    }
}
