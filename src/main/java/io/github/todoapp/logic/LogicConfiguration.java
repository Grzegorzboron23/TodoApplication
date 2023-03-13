package io.github.todoapp.logic;

import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.ProjectRepository;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(final ProjectRepository repository,
                           final TaskGroupRepository taskGroupRepository,
                           final TaskConfigurationProperties config){
        return new ProjectService(repository, taskGroupRepository, config);
    }

    @Bean
    TaskGroupService taskGroupService(final TaskRepository taskRepository,
                                      final TaskGroupRepository taskGroupRepository){
        return new TaskGroupService(taskGroupRepository,taskRepository);
    }

}
