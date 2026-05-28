package com.fullstack.reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// El nombre "ms-accesos" debe coincidir con el nombre del servicio en docker-compose.yml
@FeignClient(name = "ms-accesos", url = "${ms.accesos.url:http://ms-accesos:8086}")
public interface AccesosClient {

    // Asumo que ms-accesos tendrá un endpoint para registrar un QR válido
    @PostMapping("/api/v1/accesos/registrar-qr")
    void registrarQrValido(@RequestBody QrReservaDTO qr);

    // DTO anidado para la comunicación
    record QrReservaDTO(String qrCode, Long reservaId, Long canchaId) {}
}
