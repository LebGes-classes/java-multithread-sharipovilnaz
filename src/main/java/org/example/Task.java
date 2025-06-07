package org.example;

public class Task {
    private String taskName;
    private int duration;
    private final int originalDuration;
    private boolean status;

    // Конструктор для новых задач
    public Task(String taskName, int duration, boolean status) {
        this.taskName = taskName;
        this.duration = duration;
        this.status = status;
        this.originalDuration = duration;
    }

    public Task(String taskName, int duration, int originalDuration, boolean status) {
        this.taskName = taskName;
        this.duration = duration;
        this.status = status;
        this.originalDuration = originalDuration;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getDuration() {
        return duration;
    }

    public int getOriginalDuration() {
        return originalDuration;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "Задача='" + taskName + '\'' +
                ", длительность=" + duration +
                ", оригинал=" + originalDuration +
                ", статус=" + status +
                '}';
    }
}