package br.com.todolist.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepo query;

  @PostMapping("/")
  public ResponseEntity createUser(@RequestBody User usuario) {

    var exists = this.query.findByUsername(usuario.getUsername());

    if (exists != null) {
      System.out.println("USUARIO JÁ EXISTE");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuario já existe!");
    }

    var passwordHashred = BCrypt.withDefaults().hashToString(12, usuario.getPassword().toCharArray());

    usuario.setPassword(passwordHashred);

    this.query.save(usuario);

    System.out.println("USUARIO SALVO COM SUCESSO");
    return ResponseEntity.status(HttpStatus.CREATED).body("Usuario criado com sucesso");

  }

  @GetMapping("/{username}")
  public ResponseEntity usuario(@PathVariable("username") String username) {

    var found = this.query.findByUsername(username);

    if (found == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado!");
    }

    return ResponseEntity.status(HttpStatus.OK).body(found);
  }

}
