package io.github.todoapp.logic;

import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.*;
import io.github.todoapp.model.projection.GroupReadModel;
import io.github.todoapp.model.projection.GroupTaskWriteModel;
import io.github.todoapp.model.projection.GroupWriteModel;
import io.github.todoapp.model.projection.ProjectWriteModel;
import lombok.AllArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;



    public List<Project> readAll(){
        return repository.findAll();

    }

    public Project save(final ProjectWriteModel toSave){
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!config.getTemplate().isAllowMultipleTasks() &&
        taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }

         GroupReadModel result  = repository.findById(projectId)
                 .map(project -> {
                     var targetGroup = new GroupWriteModel();
                     targetGroup.setDescription(project.getDescription());
                     targetGroup.setTasks(
                             project.getSteps().stream()
                                     .map(projectStep  -> {
                                         var task = new GroupTaskWriteModel();
                                         task.setDescription(projectStep.getDescription());
                                         task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                         return task;
                                        }
                                     ).collect(Collectors.toSet())
                     );
                    return taskGroupService.createGroup(targetGroup, project);
                 }).orElseThrow(() -> new IllegalArgumentException("Project with given id not Found")) ;
        return result;
    }
}
