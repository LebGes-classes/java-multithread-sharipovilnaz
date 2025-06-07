package org.example;

import java.util.ArrayList;
import java.util.List;

public class Worker {
    private int id;
    private String name;
    private List<Task> tasks;
    private List<Task> completedTasks;
    private int totalWorkTime;
    private int totalIdleTime;
    private int daysWorked;
    private List<Integer> dailyEfficiency;
    private List<Integer> dailyWorkHours;
    private List<Integer> dailyIdleHours;

    public Worker(int id, String name) {
        this.id = id;
        this.name = name;
        this.tasks = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.totalWorkTime = 0;
        this.totalIdleTime = 0;
        this.daysWorked = 0;
        this.dailyEfficiency = new ArrayList<>();
        this.dailyWorkHours = new ArrayList<>();
        this.dailyIdleHours = new ArrayList<>();
    }

    public synchronized void addDailyStat(int workHours, int idleHours) {
        daysWorked++;
        totalWorkTime += workHours;
        totalIdleTime += idleHours;

        dailyWorkHours.add(workHours);
        dailyIdleHours.add(idleHours);
        dailyEfficiency.add((workHours * 100) / 8); // Эффективность за день
    }

    public synchronized void addTask(Task task) {
        tasks.add(task);
    }

    public synchronized void addCompletedTask(Task task) {
        completedTasks.add(task);
    }

    public synchronized void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public synchronized List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public synchronized List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized int getTotalWorkTime() {
        return totalWorkTime;
    }

    public synchronized int getTotalIdleTime() {
        return totalIdleTime;
    }

    public synchronized int getDaysWorked() {
        return daysWorked;
    }

    public synchronized List<Integer> getDailyEfficiency() {
        return new ArrayList<>(dailyEfficiency);
    }

    public synchronized List<Integer> getDailyWorkHours() {
        return new ArrayList<>(dailyWorkHours);
    }

    public synchronized List<Integer> getDailyIdleHours() {
        return new ArrayList<>(dailyIdleHours);
    }

    public synchronized void setTotalWorkTime(int time) {
        this.totalWorkTime = time;
    }

    public synchronized void setTotalIdleTime(int time) {
        this.totalIdleTime = time;
    }

    public synchronized void setDaysWorked(int days) {
        this.daysWorked = days;
    }

    public synchronized double calculateOverallEfficiency() {
        if (daysWorked == 0) return 0.0;
        return ((double) totalWorkTime / (daysWorked * 8)) * 100;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalWorkTime=" + totalWorkTime +
                ", totalIdleTime=" + totalIdleTime +
                ", daysWorked=" + daysWorked +
                ", efficiency=" + String.format("%.2f", calculateOverallEfficiency()) + "%}";
    }
}