package io.github.todoapp.controller;

import io.github.todoapp.logic.ProjectService;
import io.github.todoapp.model.Project;
import io.github.todoapp.model.ProjectStep;
import io.github.todoapp.model.projection.ProjectWriteModel;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProjects(Model model){
        var projectToEdit = new ProjectWriteModel();
        model.addAttribute("project", projectToEdit);
        return "projects";
    }

    @PostMapping
    String addProject(
            @ModelAttribute("project")  @Valid ProjectWriteModel current,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()){
            return "projects";
        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects", getProjects());
        model.addAttribute("message", "Dodano projekt!");
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current){
        current.getSteps().add(new ProjectStep());
        return "projects";
    }

    @PostMapping("/{id}")
        String createGroup(
                @ModelAttribute("project") ProjectWriteModel current,
                Model model,
                @PathVariable int id,
                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline
        ){
        try{
            service.createGroup(deadline,id);
            model.addAttribute("message","Dodano Grupe");

        }catch(IllegalStateException | IllegalArgumentException e){
            model.addAttribute("message","Błąd podczas tworzenia grupy");
        }
        return "projects";
        }

    @ModelAttribute("projects")
    List<Project> getProjects(){
        return service.readAll();
    }
}
