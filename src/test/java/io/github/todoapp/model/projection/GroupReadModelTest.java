package io.github.todoapp.model.projection;

import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupReadModelTest {
    @Test
    @DisplayName("should create null deadline for group when ni task deadlines")
    void constructor_noDeadlines_createsNullDeadline(){
        //given
        var source = new TaskGroup();
        source.setDescription("foo");
        source.setTasks(Set.of(new Task("bar" , null)));


        //when
        var result = new GroupReadModel(source);


        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline",null);


    }
}
