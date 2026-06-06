package com.pitsdog.api.notificacao.service;

import org.springframework.stereotype.Service;

@Service
public class WhatsAppLogService implements WhatsAppService {

    @Override
    public void enviarMensagem(String telefone, String mensagem) {
        System.out.println("========== WHATSAPP DEBUG ==========");
        System.out.println("Telefone: " + telefone);
        System.out.println("Mensagem:");
        System.out.println(mensagem);
        System.out.println("====================================");
    }
}