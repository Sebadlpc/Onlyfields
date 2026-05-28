package com.fullstack.inventario.service;

import com.fullstack.inventario.dto.MovimientoStockDTO;
import com.fullstack.inventario.dto.ProductoRequestDTO;
import com.fullstack.inventario.model.Categoria;
import com.fullstack.inventario.model.MovimientoStock;
import com.fullstack.inventario.model.Producto;
import com.fullstack.inventario.repository.CategoriaRepository;
import com.fullstack.inventario.repository.MovimientoStockRepository;
import com.fullstack.inventario.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que encapsula la lógica de negocio para la gestión del inventario.
 * Se comunica con los repositorios para acceder y modificar los datos.
 */
@Service
@RequiredArgsConstructor
public class InventarioService {

    // Inyección de dependencias de los repositorios.
    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Registra un nuevo producto en la base de datos.
     * @param dto DTO con los datos del producto.
     * @return El producto guardado con su ID.
     * @throws EntityNotFoundException si la categoría especificada no existe.
     */
    @Transactional
    public Producto registrarProducto(ProductoRequestDTO dto) {
        // Busca la categoría por ID o lanza una excepción si no se encuentra.
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // Construye el objeto Producto a partir del DTO.
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .categoria(categoria)
                .precioVenta(dto.getPrecioVenta())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .codigoBarras(dto.getCodigoBarras())
                .build();

        // Guarda el nuevo producto y lo retorna.
        return productoRepository.save(producto);
    }

    /**
     * Actualiza un producto existente.
     * @param id El ID del producto a actualizar.
     * @param dto DTO con los nuevos datos del producto.
     * @return El producto actualizado.
     * @throws EntityNotFoundException si el producto o la categoría no existen.
     */
    @Transactional
    public Producto actualizarProducto(Long id, ProductoRequestDTO dto) {
        // Busca el producto por ID o lanza una excepción.
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        // Busca la nueva categoría o lanza una excepción.
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // Actualiza los campos del producto.
        producto.setNombre(dto.getNombre());
        producto.setCategoria(categoria);
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCodigoBarras(dto.getCodigoBarras());

        // Guarda los cambios y retorna el producto.
        return productoRepository.save(producto);
    }

    /**
     * Realiza una entrada o salida de stock para un producto y registra el movimiento.
     * Este método es transaccional y utiliza un bloqueo pesimista para evitar condiciones de carrera.
     * @param productoId El ID del producto.
     * @param dto DTO con los detalles del movimiento (tipo, cantidad, referencia).
     * @throws EntityNotFoundException si el producto no existe.
     * @throws IllegalArgumentException si el tipo de movimiento es inválido o si no hay stock suficiente.
     */
    @Transactional
    public void actualizarStock(Long productoId, MovimientoStockDTO dto) {
        // Busca el producto aplicando un bloqueo de escritura para garantizar la consistencia del stock.
        Producto producto = productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoId));

        // Procesa el movimiento según el tipo.
        if ("SALIDA".equalsIgnoreCase(dto.getTipo())) {
            if (producto.getStockActual() < dto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para realizar la salida. Stock actual: " + producto.getStockActual() + ", Cantidad solicitada: " + dto.getCantidad());
            }
            producto.setStockActual(producto.getStockActual() - dto.getCantidad());
        } else if ("ENTRADA".equalsIgnoreCase(dto.getTipo())) {
            producto.setStockActual(producto.getStockActual() + dto.getCantidad());
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no válido: " + dto.getTipo());
        }

        // Guarda el estado actualizado del producto.
        productoRepository.save(producto);

        // Crea y guarda un registro del movimiento de stock.
        MovimientoStock movimiento = MovimientoStock.builder()
                .producto(producto)
                .tipo(dto.getTipo().toUpperCase())
                .cantidad(dto.getCantidad())
                .fechaHora(LocalDateTime.now())
                .referencia(dto.getReferencia())
                .build();

        movimientoRepository.save(movimiento);
    }

    /**
     * Elimina un producto de la base de datos.
     * @param id El ID del producto a eliminar.
     * @throws EntityNotFoundException si el producto no existe.
     */
    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Obtiene todos los productos del inventario.
     * @return Lista de todos los productos.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    /**
     * Busca y devuelve un producto por su ID.
     * @param id El ID del producto.
     * @return El producto encontrado.
     * @throws EntityNotFoundException si el producto no existe.
     */
    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
    }

    /**
     * Obtiene una lista de productos cuyo stock actual es menor o igual a su stock mínimo.
     * @return Lista de productos con bajo stock.
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerAlertasCriticas() {
        return productoRepository.findProductosBajoStockMinimo();
    }

    /**
     * Obtiene el historial de movimientos de un producto, ordenado por fecha descendente.
     * @param productoId El ID del producto.
     * @return Lista de movimientos de stock.
     */
    @Transactional(readOnly = true)
    public List<MovimientoStock> obtenerHistorialMovimientos(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaHoraDesc(productoId);
    }

    /**
     * Obtiene todas las categorías de productos.
     * @return Lista de todas las categorías.
     */
    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}
