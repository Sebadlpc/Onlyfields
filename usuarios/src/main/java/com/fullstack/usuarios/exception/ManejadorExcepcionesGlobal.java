package com.fullstack.usuarios.exception;

import com.fullstack.usuarios.dto.ErrorRespuestaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Manejador de excepciones global para toda la aplicación.
 * Captura excepciones específicas y las convierte en respuestas HTTP estructuradas y consistentes.
 */
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    private ResponseEntity<ErrorRespuestaDTO> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ErrorRespuestaDTO errorDto = new ErrorRespuestaDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDto, status);
    }

    @ExceptionHandler({UsuarioNoEncontradoException.class, RolNoEncontradoException.class})
    public ResponseEntity<ErrorRespuestaDTO> handleNotFoundException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorRespuestaDTO> handleUnauthorizedException(CredencialesInvalidasException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({EmailYaRegistradoException.class, RolConUsuariosException.class})
    public ResponseEntity<ErrorRespuestaDTO> handleConflictException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Un manejador genérico para cualquier otra excepción no capturada.
    // Esto evita que el cliente reciba un error HTML de Spring por defecto.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuestaDTO> handleGenericException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
