package io.github.todoapp.model.projection;

import io.github.todoapp.model.Project;
import io.github.todoapp.model.TaskGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupWriteModel {
    @NotBlank(message = "Task description must not be empty")
    private String description;
    @Valid
    private List<GroupTaskWriteModel> tasks = new ArrayList<>();

    public GroupWriteModel(){
        tasks.add(new GroupTaskWriteModel());
    }

    public TaskGroup toGroup(Project project){
        var result = new TaskGroup();
        result.setDescription(description);
        result.setTasks(
                tasks.stream().map(source -> source.toTask(result))
                        .collect(Collectors.toSet())
        );
        result.setProject(project);
        return result;

    }
}
