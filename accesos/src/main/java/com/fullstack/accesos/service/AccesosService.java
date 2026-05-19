package com.fullstack.accesos.service;

import com.fullstack.accesos.client.UsuarioClient;
import com.fullstack.accesos.dto.external.UsuarioExternoDTO;
import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.model.TipoAcceso;
import com.fullstack.accesos.repository.QrTokenRepository;
import com.fullstack.accesos.repository.RegistroAccesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccesosService {

    private final QrTokenRepository qrTokenRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final UsuarioClient usuarioClient;

    @Transactional
    public QrToken generarQr(Long clienteId) {
        log.info("[ms-accesos] Consultando estado del usuario externo ID: {}", clienteId);

        UsuarioExternoDTO usuario;
        try {
            // Llamada remota síncrona REAL mediante Feign
            usuario = usuarioClient.obtenerUsuarioPorId(clienteId);
        } catch (feign.FeignException.NotFound e) {
            log.error("[ms-accesos] El usuario ID {} no existe en ms-usuarios", clienteId);
            throw new RuntimeException("El cliente con ID " + clienteId + " no existe en el sistema de usuarios.");
        } catch (Exception e) {
            log.error("[ms-accesos] Error crítico de comunicación con ms-usuarios: {}", e.getMessage());
            throw new RuntimeException("El servicio de verificación de usuarios no está disponible temporalmente.");
        }

        // Si Feign responde pero por alguna razón el objeto llega vacío o nulo
        if (usuario == null) {
            throw new RuntimeException("No se pudo obtener la información del usuario.");
        }

        // CONTROL DEL ESTADO DEL NEGOCIO REAL
        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new RuntimeException("El usuario no se encuentra ACTIVO para ingresar al complejo.");
        }

        // FLUJO LOCAL
        QrToken nuevoToken = QrToken.builder()
                .clienteId(clienteId)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusDays(1))
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
        log.info("[ms-accesos] Buscando usuarios dentro del complejo");

        // 1. Traemos todo el historial de la base de datos
        List<RegistroAcceso> historialCompleto = registroAccesoRepository.findAll();

        // 2. Aquí guardaremos solo los registros de la gente que sigue adentro
        List<RegistroAcceso> usuariosActivos = new java.util.ArrayList<>();

        // 3. Revisamos cada registro uno por uno
        for (RegistroAcceso registro : historialCompleto) {

            // Ignoramos los registros inválidos (ID 0)
            if (registro.getClienteId() == null || registro.getClienteId() == 0) {
                continue;
            }

            // Si el cliente entró y fue PERMITIDO, asumimos temporalmente que está adentro
            if (registro.getTipo() == TipoAcceso.ENTRADA && registro.getResultado() == ResultadoAcceso.PERMITIDO) {
                usuariosActivos.add(registro);
            }
            // Pero si más adelante en el historial vemos que el mismo cliente SALIÓ...
            else if (registro.getTipo() == TipoAcceso.SALIDA) {
                // Lo removemos de la lista de personas que están adentro
                usuariosActivos.removeIf(r -> r.getClienteId().equals(registro.getClienteId()));
            }
        }

        return usuariosActivos;
    }
}