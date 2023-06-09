package io.github.todoapp.logic;

import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskGroupServiceTest {
    @Test
    @DisplayName("should throw IllegalStateException when undone  task")
    void toggleGroup_undoneTask_throwIllegalStateException(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);

        //system under test
        var toTest = new TaskGroupService(null,mockTaskRepository);

        //when
        var exception = catchThrowable(() -> toTest.toggleGroup(1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone");

    }


    @Test
    @DisplayName("should throw when no group")
    void toggleGroup_wrongId_throwsIllegalArgumentException(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);

        //and
        var mockRepository = mock(TaskGroupRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        // system under test
        var toTest = new TaskGroupService(mockRepository,mockTaskRepository);

        //when
        var exception = catchThrowable(() -> toTest.toggleGroup(1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should toggle group")
    void toggleGroup_worksAsExpected(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);

        //and
        var group = new TaskGroup();
        var beforeToggle = group.isDone();

        //and


        var mockRepository = mock(TaskGroupRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(group));

        // system under test
        var toTest = new TaskGroupService(mockRepository,mockTaskRepository);

        //when
       toTest.toggleGroup(0);

        //then
        assertThat(group.isDone()).isEqualTo(!beforeToggle);
    }

   private TaskRepository taskRepositoryReturning(boolean result){
       //given
       var mockTaskRepository = mock(TaskRepository.class);
       when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
       return mockTaskRepository;
   }



}
