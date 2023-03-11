package io.github.todoapp.model.projection;

import io.github.todoapp.model.TaskGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class GroupWriteModel {
    private String desciption;
    private Set<GroupTaskWriteModel> tasks;

    public TaskGroup toGroup(){
        var result = new TaskGroup();
        result.setDescription(desciption);
        result.setTasks(
                tasks.stream().map(GroupTaskWriteModel::toTask)
                        .collect(Collectors.toSet())
        );
        return result;

    }
}
