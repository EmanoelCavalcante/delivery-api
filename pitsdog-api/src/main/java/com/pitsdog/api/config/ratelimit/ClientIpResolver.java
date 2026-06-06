package com.pitsdog.api.config.ratelimit;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

    public String resolver(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if(forwardedFor != null && !forwardedFor.isBlank()){
            String[] ips = forwardedFor.split(",");

            return ips[ips.length - 1].trim();
        }
        return request.getRemoteAddr();
    }
}
