package io.github.todoapp.controller;





import io.github.todoapp.logic.TaskGroupService;
import io.github.todoapp.model.ProjectStep;
import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskRepository;
import io.github.todoapp.model.projection.GroupReadModel;
import io.github.todoapp.model.projection.GroupWriteModel;
import io.github.todoapp.model.projection.ProjectWriteModel;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@Controller
@RequestMapping("/groups")
public class TaskGroupController {
    private final TaskRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;


    public TaskGroupController(TaskRepository repository, TaskGroupService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model){
        model.addAttribute("group" , new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(@ModelAttribute("group")  @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()){
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group", new ProjectWriteModel());
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Dodano grupe!");
        return "groups";
    }


    @PostMapping(params = "addTask" , produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") ProjectWriteModel current){
        current.getSteps().add(new ProjectStep());
        return "groups";
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        logger.warn("Exposing all the tasks!");
        return ResponseEntity.ok(service.readAll());
    }

    @ResponseBody
    @Transactional
    @PatchMapping("/{id}")
    public  ResponseEntity<?> toggleGroup(@PathVariable int id){
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE , consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(service.createGroup(toCreate));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups(){
        return service.readAll();
    }
}
