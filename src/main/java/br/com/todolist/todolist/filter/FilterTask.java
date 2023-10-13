package br.com.todolist.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.todolist.todolist.user.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTask extends OncePerRequestFilter {

  @Autowired
  private UserRepo query;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    System.out.println(request.getServletPath());

    if (request.getServletPath().startsWith("/task")) {
      var auth = request.getHeader("Authorization");
      System.out.println(auth);

      var encoded = auth.substring("Basic".length()).trim(); // Remover "Basic"
      System.out.println(encoded);

      String authString = new String(Base64.getDecoder().decode(encoded));
      System.out.println(authString);

      // Separar
      String[] credentials = authString.split(":");
      String username = credentials[0];
      String password = credentials[1];
      
      System.out.println(credentials + "\n" + username + "\n" + password);
      if (username.length() == 0) {
        response.sendError(401);
      } else {
        var result = this.query.findByUsername(username);

        if (result == null) {
          response.sendError(401);
        } else {
        }

        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), result.getPassword());

        if (passwordVerify.verified) {
          request.setAttribute("idUser", result.getId());
          filterChain.doFilter(request, response);
        } else {
          response.sendError(401);
        }
      }

    } else {
      filterChain.doFilter(request, response);
    }

  }

}
