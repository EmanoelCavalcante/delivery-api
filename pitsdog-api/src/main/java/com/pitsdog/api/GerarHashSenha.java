package com.pitsdog.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHashSenha {

    public static void main(String[] args) {
        String senha = "TesteLocal@123";

        String hash = new BCryptPasswordEncoder().encode(senha);

        System.out.println(hash);
    }
}