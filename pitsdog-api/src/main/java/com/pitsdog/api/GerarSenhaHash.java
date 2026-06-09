
package com.pitsdog.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GerarSenhaHash {

    public static void main(String[] args) {
        String senhaPura = "pitsdog@Pedrinho2026";

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaHash = encoder.encode(senhaPura);

        System.out.println("Senha pura: " + senhaPura);
        System.out.println("Senha hash BCrypt:");
        System.out.println(senhaHash);
    }
}