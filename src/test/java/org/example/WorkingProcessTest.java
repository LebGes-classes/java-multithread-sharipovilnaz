package org.example;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WorkingProcessTest {
    @Test
    void testAddTaskAndGetTasks() {
        Worker worker = new Worker(1, "Иван");
        Task task = new Task("Задача", 10, false);
        worker.addTask(task);

        assertEquals(1, worker.getTasks().size());
        assertEquals("Задача", worker.getTasks().get(0).getTaskName());
    }

    @Test
    void testDailyStats() {
        Worker worker = new Worker(1, "Иван");

        worker.addDailyStat(8, 0); // день 1
        worker.addDailyStat(6, 2); // день 2

        assertEquals(2, worker.getDaysWorked());
        assertEquals(14, worker.getTotalWorkTime());
        assertEquals(100, worker.getDailyEfficiency().get(0));
        assertEquals(75, worker.getDailyEfficiency().get(1));
    }

    @Test
    void testTotalIdleTimeAndDaysWorked() {
        Worker worker = new Worker(1, "Иван");

        worker.addDailyStat(7, 1); // день 1
        worker.addDailyStat(5, 3); // день 2

        assertEquals(2, worker.getDaysWorked());
        assertEquals(12, worker.getTotalWorkTime());
        assertEquals(4, worker.getTotalIdleTime());
    }

    @Test
    void testTaskDurationUpdate() {
        Task task = new Task("Задача", 10, false);
        task = new Task(task.getTaskName(), task.getDuration() - 4, false);

        assertEquals(6, task.getDuration());
    }

    @Test
    void testTaskCompletion() {
        Task task = new Task("Задача", 10, false);
        task.setStatus(true);

        assertTrue(task.getStatus());
    }


    @Test
    void testWorkerCompletesTaskOverMultipleDays() throws InterruptedException {
        Worker worker = new Worker(1, "Иван");
        worker.addTask(new Task("Долгая задача", 10, false));

        WorkingProcess process = new WorkingProcess(worker);
        process.start();
        process.join();

        assertEquals(2, worker.getDaysWorked());
        assertEquals(10, worker.getTotalWorkTime());
        assertEquals(6, worker.getTotalIdleTime());
    }

    @Test
    void testWorkerFinishesAllTasks() throws InterruptedException {
        Worker worker = new Worker(1, "Иван");
        worker.addTask(new Task("Задача 1", 5, false));
        worker.addTask(new Task("Задача 2", 3, false));

        WorkingProcess process = new WorkingProcess(worker);
        process.start();
        process.join();

        assertTrue(worker.getTasks().isEmpty());
        assertEquals(2, worker.getCompletedTasks().size());
    }

}