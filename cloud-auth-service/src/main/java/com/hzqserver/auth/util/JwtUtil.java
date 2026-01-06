package com.hzqserver.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT工具类
 * 提供JWT令牌的生成、解析、验证等功能
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration; // 秒
    
    /**
     * 获取签名密钥
     * 
     * @return 用于JWT签名的密钥
     */
    private SecretKey getSigningKey() {
        // 如果配置文件中没有指定密钥，则生成一个安全的密钥
        if (secret == null || secret.isEmpty()) {
            // 使用HS512算法推荐的安全密钥
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        // 确保密钥长度符合HS512要求
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 生成JWT令牌
     * 
     * @param username 用户名
     * @param roles 用户角色列表
     * @return JWT令牌字符串
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从JWT令牌中提取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    /**
     * 从JWT令牌中提取用户角色列表
     * 
     * @param token JWT令牌
     * @return 用户角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (List<String>) claims.get("roles");
    }
    
    /**
     * 验证JWT令牌是否有效
     * 
     * @param token JWT令牌
     * @return 令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}