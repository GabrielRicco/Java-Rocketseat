package br.com.gabrielricco.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gabrielricco.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    System.out.println("Cheguei no controler");

    var idUser = request.getAttribute("idUser");
    taskModel.setIdUser((UUID) idUser);

    var currentDate = LocalDateTime.now();
    // 13/10/2023 - Current
    // 05/02/1998 - startAt
    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(400).body("A data de início/término deve ser maior que a data atual");
    }

    if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(400).body("A data de término deve ser maior que a data de início");
    }

    var task = this.taskRepository.save(taskModel);

    return ResponseEntity.status(200).body(task);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);

    return tasks;
  }

  @PostMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
    var task = this.taskRepository.findById(id).orElse(null);

    Utils.copyNonNullProperties(taskModel, task);

    return this.taskRepository.save(task);
  }
}