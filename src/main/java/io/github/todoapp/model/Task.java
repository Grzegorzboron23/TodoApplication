package io.github.todoapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


//TODO: ss

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
 public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message  = "Task description must not be empty")
    private String description;
    @Embedded
    private Audit audit = new Audit();
    private boolean done;
   private LocalDateTime deadline;
   @ManyToOne // many tasks to one group
   @JoinColumn(name="task_group_id")
    private TaskGroup group;


   public Task(String description,LocalDateTime deadline){
       this.description= description;
       this.deadline = deadline;
       this.group = null;
   }

    public Task(String description,LocalDateTime deadline, TaskGroup group){
        this.description= description;
        this.deadline = deadline;
        if(group != null){
            this.group = group;
        }
    }




   public void updateFrom(final Task source){
       description= source.description;
       done=source.done;
       deadline = source.deadline;
       group = source.group;
   }


}
