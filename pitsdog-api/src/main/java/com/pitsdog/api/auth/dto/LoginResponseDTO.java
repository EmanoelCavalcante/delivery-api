package com.pitsdog.api.auth.dto;

public class LoginResponseDTO {

    private String token;
    private String tipo;
    private long expirEm;

    public LoginResponseDTO(String token, String tipo, long expirEm) {
        this.token = token;
        this.tipo = tipo;
        this.expirEm = expirEm;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getExpirEm() {
        return expirEm;
    }

    public void setExpirEm(long expirEm) {
        this.expirEm = expirEm;
    }
}
