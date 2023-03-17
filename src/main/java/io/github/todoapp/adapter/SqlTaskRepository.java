package io.github.todoapp.adapter;

import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


//Comunicate with database
// na podstawie nazwy metody stworzy zapytanie find all = select from tabela etc.
// example where to save tasks instead of tasks localhost:8080/todos @RepositoryRestResource(path = "todos", collectionResourceRel = "todos")
@Repository
 interface SqlTaskRepository extends TaskRepository,JpaRepository <Task,Integer> {
    List<Task> findByDone(@Param("state") boolean done);

    @Override
    @Query(nativeQuery = true, value ="select count(*) >0 from tasks where id=?1")
    boolean existsById(@Param("id")Integer id);


    @Override
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);


   @Override
   List<Task> findAllByGroup_Id(Integer groupId);
}
