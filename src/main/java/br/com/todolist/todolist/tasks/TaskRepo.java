package br.com.todolist.todolist.tasks;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TaskRepo extends JpaRepository<Task, UUID>{
  
  List<Task> findByIdUser(UUID idUser);

  Task findTaskById(UUID id);

}
