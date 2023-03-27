package io.github.todoapp.model.projection;


import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupTaskWriteModel {
    @NotBlank(message = "Task description must not be empty")
    private String description;
    private LocalDateTime deadline;

    public Task toTask(TaskGroup group){
       return new Task(description,deadline, group);
    }
}
