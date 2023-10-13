package br.com.todolist.todolist.tasks;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.todolist.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class TaskController {

  @Autowired
  private TaskRepo query;

  @PostMapping
  public ResponseEntity createTask(@RequestBody Task tarefa, HttpServletRequest request) {

    LocalDateTime current = LocalDateTime.now();

    if (current.isAfter(tarefa.getStartAt()) || current.isAfter(tarefa.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser posterior a data atual");
    }

    if (tarefa.getStartAt().isAfter(tarefa.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser anterior a data final");
    }

    tarefa.setIdUser((UUID) request.getAttribute("idUser"));
    var task = this.query.save(tarefa);

    return ResponseEntity.status(HttpStatus.CREATED).body(task);

  }

  @GetMapping
  public ResponseEntity list(HttpServletRequest req) {
    var result = this.query.findByIdUser((UUID) req.getAttribute("idUser"));

    return ResponseEntity.status(HttpStatus.FOUND).body(result);

  }

  @PutMapping("/{id}")
  public ResponseEntity alter(@RequestBody Task tarefa, HttpServletRequest req, @PathVariable UUID id) {
    var values = this.query.findTaskById(id);
    
    if(values == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
    } else if (!values.getIdUser().equals(req.getAttribute("idUser"))){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario não tem permissão para alterar esta tarefa");
    }

    Utils.copyNonNullProperties(tarefa, values);

    var result = this.query.save(values);

    return ResponseEntity.status(HttpStatus.OK).body(result);

  }


}
