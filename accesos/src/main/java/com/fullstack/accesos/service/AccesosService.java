package com.fullstack.accesos.service;

import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import com.fullstack.accesos.repository.QrTokenRepository;
import com.fullstack.accesos.repository.RegistroAccesoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccesosService {

    @Autowired
    private QrTokenRepository qrTokenRepository;

    @Autowired
    private RegistroAccesoRepository registroAccesoRepository;

    @Transactional
    public QrToken generarQr(Long clienteId) {
        QrToken nuevoToken = QrToken.builder()
                .clienteId(clienteId)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusDays(1)) // Expira en 24 hrs
                .usado(false)
                .build();
        return qrTokenRepository.save(nuevoToken);
    }

    @Transactional
    public RegistroAcceso validarEntrada(String tokenString) {
        Optional<QrToken> tokenOpt = qrTokenRepository.findByToken(tokenString);

        RegistroAcceso registro = RegistroAcceso.builder()
                .tipo(TipoAcceso.ENTRADA)
                .build();

        if (tokenOpt.isEmpty()) {
            registro.setClienteId(0L); // Generico si el token no existe
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("QR invalido o inexistente");
            return registroAccesoRepository.save(registro);
        }

        QrToken token = tokenOpt.get();
        registro.setClienteId(token.getClienteId());

        if (token.isUsado()) {
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("El QR ya fue utilizado");
        } else if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            registro.setResultado(ResultadoAcceso.DENEGADO);
            registro.setMotivoRechazo("El QR ha expirado");
        } else {
            registro.setResultado(ResultadoAcceso.PERMITIDO);

            // Invalidar QR tras uso exitoso
            token.setUsado(true);
            qrTokenRepository.save(token);
        }

        return registroAccesoRepository.save(registro);
    }

    @Transactional
    public RegistroAcceso registrarSalida(Long clienteId) {
        RegistroAcceso registro = RegistroAcceso.builder()
                .clienteId(clienteId)
                .tipo(TipoAcceso.SALIDA)
                .resultado(ResultadoAcceso.PERMITIDO)
                .build();
        return registroAccesoRepository.save(registro);
    }

    public List<RegistroAcceso> obtenerHistorial() {
        return registroAccesoRepository.findAll();
    }

    public List<RegistroAcceso> obtenerHistorialCliente(Long clienteId) {
        return registroAccesoRepository.findByClienteIdOrderByFechaHoraDesc(clienteId);
    }

    public List<RegistroAcceso> obtenerActivos() {
        // Devuelve temporalmente todos los registros de entrada permitidos
        return registroAccesoRepository.findAll().stream()
                .filter(r -> r.getTipo() == TipoAcceso.ENTRADA && r.getResultado() == ResultadoAcceso.PERMITIDO)
                .toList();
    }
}