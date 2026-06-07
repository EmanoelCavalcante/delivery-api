package com.pitsdog.api.loja.service;

import com.pitsdog.api.loja.dto.AtualizarLojaRequestDTO;
import com.pitsdog.api.loja.dto.LojaStatusReponseDTO;
import com.pitsdog.api.loja.enums.EstadoOperacao;
import com.pitsdog.api.loja.entity.LojaConfiguracao;
import com.pitsdog.api.loja.repository.LojaConfiguracaoRepository;
import com.pitsdog.api.pedido.enums.TipoPedido;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LojaService {

    private static final Long CONFIGURACAO_ID = 1L;

    private final LojaConfiguracaoRepository repository;

    public LojaService(LojaConfiguracaoRepository repository) {
        this.repository = repository;
    }

    private EstadoOperacao calcularEstado(LojaConfiguracao configuracao){
        boolean entrega = Boolean.TRUE.equals(configuracao.getAceitaEntrega());
        boolean retirada = Boolean.TRUE.equals(configuracao.getAceitaRetirada());
        boolean mesa = Boolean.TRUE.equals(configuracao.getAceitaMesa());

        if(entrega && retirada && mesa){
            return EstadoOperacao.ABERTA;
        }

        if(!entrega && !retirada && !mesa){
            return EstadoOperacao.FECHADA_TOTALMENTE;
        }

        if(!entrega && retirada && mesa){
            return EstadoOperacao.FECHADA_PARA_ENTREGA;
        }

        if(entrega && !retirada && mesa){
            return EstadoOperacao.FECHADA_PARA_RETIRADA;
        }
        return EstadoOperacao.PERSONALIZADA;
    }

    private LojaStatusReponseDTO converterParaDTO(LojaConfiguracao configuracao){
        LojaStatusReponseDTO dto = new LojaStatusReponseDTO();

        dto.setAceitaEntrega(configuracao.getAceitaEntrega());
        dto.setAceitaRetirada(configuracao.getAceitaRetirada());
        dto.setAceitaMesa(configuracao.getAceitaMesa());
        dto.setAtualizadoEm(configuracao.getAtualizadoEm());

        EstadoOperacao estado = calcularEstado(configuracao);

        dto.setEstadoOperacao(estado);
        dto.setAberta(estado != EstadoOperacao.FECHADA_TOTALMENTE);

        String mensagem = configuracao.getMensagemFechamento();

        if(mensagem == null || mensagem.isBlank()){
            mensagem = estado == EstadoOperacao.ABERTA
                    ? "Estamos recebendo pedidos normalmente"
                    : "No momento, alguns tipos de pedidos estão indisponíveis";
        }

        dto.setMensagem(mensagem);

        return dto;
    }

    @Transactional
    public LojaConfiguracao obterOuCriarConfiguracao(){
        return repository.findById(CONFIGURACAO_ID)
                .orElseGet(() -> {
                    LojaConfiguracao configuracao = new LojaConfiguracao();
                    configuracao.setId(CONFIGURACAO_ID);
                    configuracao.setAceitaEntrega(true);
                    configuracao.setAceitaMesa(true);
                    configuracao.setAceitaRetirada(true);
                    configuracao.setMensagemFechamento(
                            "Estamos recebendo pedidos normalmente"
                    );

                   return repository.save(configuracao);
                });
    }

    @Transactional(readOnly = true)
    public LojaStatusReponseDTO buscarStatus(){
        LojaConfiguracao configuracao = repository.findById(CONFIGURACAO_ID)
                .orElseGet(this::obterOuCriarConfiguracao);

        return converterParaDTO(configuracao);
    }

    @Transactional
    public LojaStatusReponseDTO atualizarStatus(AtualizarLojaRequestDTO dto){
        LojaConfiguracao configuracao = obterOuCriarConfiguracao();

        configuracao.setAceitaEntrega(dto.getAceitaEntrega());
        configuracao.setAceitaRetirada(dto.getAceitaRetirada());
        configuracao.setAceitaMesa(dto.getAceitaMesa());
        configuracao.setMensagemFechamento(dto.getMensagemFechamento());

        LojaConfiguracao configuracaoSalva = repository.save(configuracao);

        return converterParaDTO(configuracaoSalva);
    }

    @Transactional(readOnly = true)
    public void validarRecebimentoPedido(TipoPedido tipoPedido){
        LojaConfiguracao configuracao = repository.findById(CONFIGURACAO_ID)
                .orElseGet(this::obterOuCriarConfiguracao);

        if(tipoPedido == null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tipoPedido é obrigatório"
            );
        }

        boolean permitido = switch (tipoPedido){
            case ENTREGA -> Boolean.TRUE.equals(configuracao.getAceitaEntrega());
            case RETIRADA -> Boolean.TRUE.equals(configuracao.getAceitaRetirada());
            case MESA -> Boolean.TRUE.equals(configuracao.getAceitaMesa());
        };

        if(!permitido){
            String mensagem = configuracao.getMensagemFechamento();

            if(mensagem == null || mensagem.isBlank()){
                mensagem = "No momento não estamos recebendo pedidos deste tipo.";
            }

            throw new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
        }
    }
}
