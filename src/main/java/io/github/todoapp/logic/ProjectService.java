package io.github.todoapp.logic;

import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.*;
import io.github.todoapp.model.projection.GroupReadModel;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    private List<Project> readAll(){
        return repository.findAll();

    }

    public Project save(final Project toSave){
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!config.getTemplate().isAllowMultipleTasks() &&
        taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }

         TaskGroup result  = repository.findById(projectId)
                 .map(project -> {
                     var targetGroup = new TaskGroup();
                     targetGroup.setDescription(project.getDescription());
                     targetGroup.setTasks(
                             project.getSteps().stream()
                                     .map(step -> new Task(
                                             step.getDescription(),
                                             deadline.plusDays(step.getDaysToDeadline())))
                                             .collect(Collectors.toSet())
                     );
                     targetGroup.setProject(project);
                     return taskGroupRepository.save(targetGroup);
                 }).orElseThrow(() -> new IllegalArgumentException("Project with given id not Found")) ;
        return new GroupReadModel(result);
    }
}
