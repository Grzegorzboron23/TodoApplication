package io.github.todoapp.logic;



import io.github.todoapp.model.Project;
import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import io.github.todoapp.model.projection.GroupReadModel;
import io.github.todoapp.model.projection.GroupWriteModel;



import java.util.List;
import java.util.stream.Collectors;


public class TaskGroupService {

    private TaskRepository taskRepository;
    private  TaskGroupRepository repository;
    TaskGroupService(final TaskGroupRepository repository, TaskRepository taskRepository){
        this.repository = repository;
        this.taskRepository=taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
       return createGroup(source, null);
    }

    public GroupReadModel createGroup(GroupWriteModel source, final Project project){
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll()
                .stream().map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done alle the tasks first");
        }
        TaskGroup result = repository.findById(groupId).orElseThrow(
                () -> new IllegalArgumentException("Task group with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);
    }

}
