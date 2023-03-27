package io.github.todoapp.logic;



import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.*;
import io.github.todoapp.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group exist")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException() {
        // given
        // tworzy automatycznie klase i nadpisuje metody
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);

        TaskConfigurationProperties mockConfig = configurationReturning(false);

        var toTest = new ProjectService( null,  mockGroupRepository, mockConfig,null);

            // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(),0));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok and no projects for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {

        TaskConfigurationProperties mockConfig = configurationReturning(true);

        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        var toTest = new ProjectService( mockRepository, null, mockConfig,null);

        // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(),0));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configured to allow just 1 group no groups no projects")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_noProjects_throwsIllegalArgumentException() {
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        TaskGroupRepository mockGroupRepository =groupRepositoryReturning(false);


        TaskConfigurationProperties mockConfig = configurationReturning(true);



        var toTest = new ProjectService( mockRepository, null, mockConfig,null);

        // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(),0));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("should create new group from project")
    void createGroup_configurationOk_existingProject_createAnsSavesGroup(){
        //given
        var today = LocalDate.now().atStartOfDay();
        // and
        var project  = projectWith("bar", Set.of(-1,-2));
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(project)
        );

        InMemoryGroupRepository inMemoryGroupRepo =InMemoryGroupRepository();
        var serviceWithInMemoRepo = dummyGroupService(inMemoryGroupRepo);
        int countBeforeCall = inMemoryGroupRepo.count();

        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under tes
        var toTest = new ProjectService( mockRepository,  inMemoryGroupRepo, mockConfig,serviceWithInMemoRepo);

       GroupReadModel result = toTest.createGroup(today, 1);




       assertThat(result.getDescription()).isEqualTo("bar");
       assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));





        assertThat(countBeforeCall)
                .isEqualTo(inMemoryGroupRepo.count());

    }
    private InMemoryGroupRepository InMemoryGroupRepository(){
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements  TaskGroupRepository {

            private Map<Integer, TaskGroup> map = new HashMap<>();
            private int index = 0;

            public int count(){
                return map.values().size();
            }

            @Override
            public List<TaskGroup> findAll() {
                return new ArrayList<>(map.values());
            }

            @Override
            public Optional<TaskGroup> findById(Integer id) {
                return Optional.ofNullable(map.get(id));
            }

            @Override
            public TaskGroup save(TaskGroup entity) {
                if (entity.getId() == 0) {
                    try {
                     var field = TaskGroup.class.getDeclaredField("id");
                     field.setAccessible(true);
                     field.set(entity,++index);
                    } catch (IllegalAccessException |  NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    map.put(++index, entity);
                }
                return entity;
            }

            @Override
            public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {

                return map.values().stream().filter(group -> !group.isDone())
                        .anyMatch(group ->group.getProject() != null && group.getProject().getId()== projectId);
            }
    }

    private Project projectWith(String projectdescription, Set <Integer> daysToDeadline){

       List <ProjectStep> steps =daysToDeadline.stream()
               .map(days -> {
                   var step = mock(ProjectStep.class);
                   when(step.getDescription()).thenReturn("foo");
                   when(step.getDaysToDeadline()).thenReturn(days);
                   return step;
               }).collect(Collectors.toList());
       var result = mock(Project.class);
       when(result.getDescription()).thenReturn(projectdescription);
       when(result.getSteps()).thenReturn((Set<ProjectStep>) steps);

        return result;
    }


    private TaskGroupRepository groupRepositoryReturning(final boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private static TaskConfigurationProperties configurationReturning(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);

        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private TaskGroupService dummyGroupService(final InMemoryGroupRepository inMemoryGroupRepository){
        return new TaskGroupService(inMemoryGroupRepository, null);
    }


}