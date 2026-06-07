package com.pitsdog.api.pagamento.repository;

import com.pitsdog.api.pagamento.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByPedidoId(Long pedidoId);

    Optional<Pagamento> findByStonePaymentId(String stonePaymentId);

    Optional<Pagamento> findByStoneChargeId(String stoneChargeId);

    Optional<Pagamento> findByStoneQrcodeId(String stoneQrcodeId);

    boolean existsByPedidoId(Long pedidoId);
}
