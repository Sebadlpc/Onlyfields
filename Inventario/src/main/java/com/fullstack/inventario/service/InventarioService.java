package com.fullstack.inventario.service;

import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.repository.CategoriaRepository;
import com.fullstack.inventario.repository.MovimientoStockRepository;
import com.fullstack.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Producto registrarProducto(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("ProductoNotFoundException: Categoría no encontrada"));

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .categoria(categoria)
                .precioVenta(dto.getPrecioVenta())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .codigoBarras(dto.getCodigoBarras())
                .build();

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoNotFoundException: Producto no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("ProductoNotFoundException: Categoría no encontrada"));

        producto.setNombre(dto.getNombre());
        producto.setCategoria(categoria);
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCodigoBarras(dto.getCodigoBarras());

        return productoRepository.save(producto);
    }

    @Transactional
    public void actualizarStock(Long productoId, MovimientoStockDTO dto) {
        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new RuntimeException("ProductoNotFoundException: Producto no encontrado"));

        if ("SALIDA".equals(dto.getTipo())) {
            if (producto.getStockActual() < dto.getCantidad()) {
                throw new RuntimeException("StockInsuficienteException: Stock insuficiente para realizar la salida");
            }
            producto.setStockActual(producto.getStockActual() - dto.getCantidad());
        } else if ("ENTRADA".equals(dto.getTipo())) {
            producto.setStockActual(producto.getStockActual() + dto.getCantidad());
        }

        productoRepository.save(producto);

        MovimientoStock movimiento = MovimientoStock.builder()
                .producto(producto)
                .tipo(dto.getTipo())
                .cantidad(dto.getCantidad())
                .fechaHora(LocalDateTime.now())
                .referencia(dto.getReferencia())
                .build();

        movimientoRepository.save(movimiento);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("ProductoNotFoundException: Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoNotFoundException: Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerAlertasCriticas() {
        return productoRepository.findProductosBajoStockMinimo();
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> obtenerHistorialMovimientos(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaHoraDesc(productoId);
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}