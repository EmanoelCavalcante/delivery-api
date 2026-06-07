package com.pitsdog.api.config.ratelimit;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

    public String resolver(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if(forwardedFor != null && !forwardedFor.isBlank()){
            String[] ips = forwardedFor.split(",");

            for (String ip : ips) {
                if (ip != null && !ip.isBlank()) {
                    return ip.trim();
                }
            }
        }

        String realIp = request.getHeader("X-Real-IP");

        if(realIp != null && !realIp.isBlank()){
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
