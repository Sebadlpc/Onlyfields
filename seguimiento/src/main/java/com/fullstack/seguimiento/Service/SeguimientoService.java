package com.fullstack.seguimiento.Service;

import com.fullstack.seguimiento.Model.FichaCliente;
import com.fullstack.seguimiento.Model.MedicionCorporal;
import com.fullstack.seguimiento.Repository.FichaClienteRepository;
import com.fullstack.seguimiento.Repository.MedicionCorporalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeguimientoService {

    @Autowired
    private FichaClienteRepository fichaRepository;

    @Autowired
    private MedicionCorporalRepository medicionRepository;

    // --- LÓGICA DE FICHAS ---

    @Transactional
    public FichaCliente crearFicha(FichaCliente ficha) {
        if (fichaRepository.existsByClienteId(ficha.getClienteId())) {
            throw new RuntimeException("FichaYaExisteException: El cliente ya tiene una ficha clínica asignada.");
        }

        // Si mandan mediciones iniciales junto con la ficha, las amarramos (Igual que en POS)
        if (ficha.getMediciones() != null && !ficha.getMediciones().isEmpty()) {
            ficha.getMediciones().forEach(m -> m.setFicha(ficha));
        }

        return fichaRepository.save(ficha);
    }

    public FichaCliente obtenerFichaPorCliente(Long clienteId) {
        return fichaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: No se encontró ficha para este cliente."));
    }

    @Transactional
    public FichaCliente actualizarFicha(Long id, FichaCliente fichaActualizada) {
        FichaCliente ficha = fichaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: Ficha no encontrada."));

        // Solo actualizamos lesiones y observaciones (no cambiamos el clienteId ni la fecha de ingreso)
        ficha.setLesiones(fichaActualizada.getLesiones());
        ficha.setObservaciones(fichaActualizada.getObservaciones());

        return fichaRepository.save(ficha);
    }

    @Transactional
    public void eliminarFicha(Long id) {
        if (!fichaRepository.existsById(id)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        // Al eliminar, el orphanRemoval = true en el modelo borrará también las mediciones
        fichaRepository.deleteById(id);
    }

    // --- LÓGICA DE MEDICIONES ---

    @Transactional
    public MedicionCorporal agregarMedicion(Long fichaId, MedicionCorporal medicion) {
        FichaCliente ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new RuntimeException("FichaNotFoundException: Ficha no encontrada para asignar la medición."));

        medicion.setFicha(ficha);

        // El cálculo del IMC se hará automáticamente por tu @PrePersist en el modelo antes de guardar
        return medicionRepository.save(medicion);
    }

    public List<MedicionCorporal> obtenerHistorialMediciones(Long fichaId) {
        if (!fichaRepository.existsById(fichaId)) {
            throw new RuntimeException("FichaNotFoundException: Ficha no encontrada.");
        }
        return medicionRepository.findByFichaIdOrderByFechaDesc(fichaId);
    }
}
