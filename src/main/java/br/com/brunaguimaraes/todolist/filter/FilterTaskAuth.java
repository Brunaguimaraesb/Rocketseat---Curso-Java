package br.com.brunaguimaraes.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.brunaguimaraes.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

   @Autowired
   private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //pegar a autenticação (usuario e senha)
        var authorization = request.getHeader("Authorization");

        var authEncoded = authorization.substring("Basica".length()).trim();

        byte[] authDecode = Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecode);

        //["brunaguimaraes","12345"]
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];
        System.out.println(username);
        System.out.println(password);

        //validar usuario
        var user = this.userRepository.findByUsername(username);
        if (user == null){
            response.sendError(401);
        }else {
            //validar senha
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if(passwordVerify.verified) {
                filterChain.doFilter(request, response);
            }else {
                response.sendError(401);
            }
            //segue viagem
        }
    }
}
