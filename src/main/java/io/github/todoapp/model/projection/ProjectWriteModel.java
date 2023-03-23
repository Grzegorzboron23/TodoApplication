package io.github.todoapp.model.projection;

import io.github.todoapp.model.Project;
import io.github.todoapp.model.ProjectStep;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.patterns.PerObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Getter
@Setter
public class ProjectWriteModel {
    @NotBlank(message = "Projects description must not be empty")
    private String description;

    @Valid
    private List<ProjectStep> steps = new ArrayList<>();

    public ProjectWriteModel(){
        steps.add(new ProjectStep());
    }

    public Project toProject(){
        var result = new Project();
        result.setDescription(description);
        steps.forEach(step -> step.setProject(result));
        result.setSteps(new HashSet<>(steps));
        return result;
    }


}
