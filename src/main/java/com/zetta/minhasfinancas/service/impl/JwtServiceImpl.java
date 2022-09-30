package com.zetta.minhasfinancas.service.impl;

import com.zetta.minhasfinancas.model.entity.Usuario;
import com.zetta.minhasfinancas.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.key-signature}")
    private String signatureKey;

    @Override
    public String gerarToken(Usuario usuario) {
        long expLong = Long.valueOf(expiration);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expLong);
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        String expirationTimeToken = dataHoraExpiracao.toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        String token = Jwts.builder()
                .setExpiration(data)
                .setSubject(usuario.getEmail())
                .claim("nome", usuario.getNome())
                .claim("horaExpiracao", expirationTimeToken)
                .signWith(SignatureAlgorithm.HS512, signatureKey)
                .compact();
        return token;
    }

    @Override
    public Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(signatureKey)
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenValido(String token) {
        try {
            Claims claim = obterClaims(token);
            Date dataEx = claim.getExpiration();
            LocalDateTime expirationDate = dataEx.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            boolean isExpired = LocalDateTime.now().isAfter(expirationDate);
            return !isExpired;
        }
        catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String obterLoginUsuario(String token) {
        Claims claim = obterClaims(token);
        return claim.getSubject();
    }
}
