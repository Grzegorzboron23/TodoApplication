package io.github.todoapp.logic;



import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import io.github.todoapp.model.projection.GroupReadModel;
import io.github.todoapp.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskGroupService {

    private TaskRepository taskRepository;
    private  TaskGroupRepository repository;
    TaskGroupService(final TaskGroupRepository repository, TaskRepository taskRepository){
        this.repository = repository;
        this.taskRepository=taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
       TaskGroup result = repository.save(source.toGroup());
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
