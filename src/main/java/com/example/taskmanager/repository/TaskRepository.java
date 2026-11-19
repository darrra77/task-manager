package com.example.taskmanager.repository;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countGroupedByStatus();

    @Query("""
       SELECT t
       FROM Task t
       WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Task> findAllByOwner(User owner);

    @Query("""
       SELECT t FROM Task t
       WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Task> searchByKeyword(@Param("keyword") String keyword);
    List<Task> findByStatus(Status status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByStatusAndPriority(Status status, Priority priority);
}