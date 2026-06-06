package com.pitsdog.api.notificacao.service;

public interface WhatsAppService {

    void enviarMensagem(String telefone, String mensagem);
}
