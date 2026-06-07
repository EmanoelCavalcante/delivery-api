package com.pitsdog.api.pagamento.dto;

import com.pitsdog.api.pagamento.enums.ProvedorPagamento;
import com.pitsdog.api.pagamento.enums.StatusPagamento;
import com.pitsdog.api.pedido.enums.FormaPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PagamentoResponseDTO {

    private Long id;

    private Long pedidoId;

    private Integer numeroPedido;

    private ProvedorPagamento provedor;

    private FormaPagamento formaPagamento;

    private StatusPagamento statusPagamento;

    private BigDecimal valor;

    private String pixQrCode;

    private String pixCopiaECola;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private LocalDateTime pagoEm;

    private LocalDateTime expiradoEm;

    private LocalDateTime canceladoEm;
}
