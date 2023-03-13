package io.github.todoapp.controller;



import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@RestController
public class TaskController {
    private final TaskRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController( final TaskRepository repository) {
        this.repository = repository;
    }


    @GetMapping(value ="/tasks", params = {"!sort","!page","!size"})
    ResponseEntity<List<Task>> readAllTasks(){

        logger.warn("Exposing all the tasks!");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping(value ="/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent()); // bez build bo nic nie zmieniamy tylko odczytujemy
    }

    // Request body bo bierzmy ciało taska
    // toUpdate nazwa taska
    // valid bo chcemy zeby przeszedl walidacje czyli jak np nie da description
    @PutMapping("tasks/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task ->{
                task.updateFrom(toUpdate);
        repository.save(task);
    });

        return ResponseEntity.noContent().build(); // build zeby zbudowac mamy dostac no content
    }
    @Transactional
    @PatchMapping("tasks/{id}")
    public  ResponseEntity<?> toggleTask(@PathVariable int id){
        if(repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }


    @GetMapping("tasks/{id}") // optional
    ResponseEntity<Task>  readTask(@PathVariable int id){
        return repository.findById(id).
                map(task->ResponseEntity.ok(task))
                .orElse( ResponseEntity.notFound().build());
    }
    @PostMapping("/tasks")
    ResponseEntity<Task> createTask( @RequestBody @Valid Task toCreate) {
       Task result = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }
}
