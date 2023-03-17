package io.github.todoapp.model.projection;

import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskGroup;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupReadModel {
    private int id;
    private String description;

    /**
     * Deadline from the latest task in group.
     */
    private LocalDateTime deadline;
    private Set<GroupTaskReadModel> tasks;

    public GroupReadModel(TaskGroup source){
        id = source.getId();
        description =source.getDescription();
        source.getTasks().stream().map(Task::getDeadline)
                .max(LocalDateTime::compareTo).ifPresent(date -> deadline = date);

        tasks=source.getTasks().stream()
                .map(GroupTaskReadModel::new)
                .collect(Collectors.toSet());
    }
}
