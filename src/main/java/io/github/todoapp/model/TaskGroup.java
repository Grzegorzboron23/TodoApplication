package io.github.todoapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "task_groups")
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message  = "Task description must not be empty")
    private String description;
    @OneToMany(fetch =FetchType.LAZY, mappedBy = "group")
    private Set<Task> tasks;

    @ManyToOne
    @JoinColumn(name ="project_id")
    private Project project;

    private boolean done;

    public TaskGroup(){

    }




}