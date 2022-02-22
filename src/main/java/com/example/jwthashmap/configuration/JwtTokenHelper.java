package com.example.jwthashmap.configuration;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Component
public class JwtTokenHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expires_in}")
    private int expireIn;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private Claims getAllClaimsFromToken(String token){
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }



    public String getUsernameFromToken(String token){
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e){
            username = null;
        }

        return username;
    }

    public Date getIssuedAtDateFromToken(String token){
        Date issuedAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issuedAt = claims.getIssuedAt();
        } catch (Exception e){
            issuedAt = null;
        }
        return issuedAt;
    }

    private Date getExpirationDate(String token){
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expireDate = claims.getExpiration();
        } catch (Exception e){
            expireDate = null;
        }
        return expireDate;
    }

    public String getToken(HttpServletRequest request){
        String authHeader = getAuthHeaderFromHeader(request);
        if( authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request){
        return request.getHeader("Authorization");
    }


    private Date generateExpirationDate(){
        return new Date(new Date().getTime() + expireIn * 1000L);
    }


    public String generateToken(String username) throws InvalidKeySpecException, NoSuchAlgorithmException {

            return Jwts.builder()
                    .setIssuer(appName)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(generateExpirationDate())
                    .signWith(SIGNATURE_ALGORITHM, secretKey)
                    .compact();
    }

    public boolean isTokenExpired(String token){
        Date expireDate = getExpirationDate(token);
        return expireDate.before(new Date());
    }



    public Boolean validateToken (String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username != null && username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

}
