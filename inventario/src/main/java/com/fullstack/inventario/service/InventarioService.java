package com.fullstack.inventario.service;

import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.repository.CategoriaRepository;
import com.fullstack.inventario.repository.MovimientoStockRepository;
import com.fullstack.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;


    @Transactional
    public Producto crearProducto(Producto producto) {
        log.info("[ms-inventario] Creando nuevo producto: {}", producto.getNombre());
        producto.setEstado("ACTIVO");
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        log.info("[ms-inventario] Listando todos los productos");
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        log.info("[ms-inventario] Actualizando producto ID: {}", id);
        Producto existente = obtenerProductoPorId(id);

        existente.setNombre(productoActualizado.getNombre());
        existente.setPrecioVenta(productoActualizado.getPrecioVenta());
        existente.setStockMinimo(productoActualizado.getStockMinimo());
        existente.setCodigoBarras(productoActualizado.getCodigoBarras());
        existente.setCategoria(productoActualizado.getCategoria());

        return productoRepository.save(existente);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        log.info("[ms-inventario] Aplicando baja lógica al producto ID: {}", id);
        Producto producto = obtenerProductoPorId(id);
        producto.setEstado("INACTIVO"); //
        productoRepository.save(producto);
    }


    @Transactional
    public Producto actualizarStock(Long productoId, MovimientoStock movimiento) {
        log.info("[ms-inventario] Procesando movimiento de stock para producto ID: {}", productoId);
        Producto producto = obtenerProductoPorId(productoId);

        String tipo = movimiento.getTipo().toUpperCase();

        if (tipo.equals("SALIDA") || tipo.equals("MERMA")) { //
            if (producto.getStockActual() < movimiento.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStockActual()); //
            }
            producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
        } else if (tipo.equals("ENTRADA") || tipo.equals("AJUSTE")) { //
            producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
        }

        movimiento.setProductoId(productoId);
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setTipo(tipo);
        movimientoRepository.save(movimiento);

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerAlertas() {
        log.info("[ms-inventario] Consultando productos bajo stock mínimo");
        return productoRepository.findProductosBajoStockMinimo(); //
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> obtenerMovimientosPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaHoraDesc(productoId);
    }


    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}