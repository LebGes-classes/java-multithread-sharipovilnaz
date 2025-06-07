package org.example;

import java.util.List;

public class WorkingProcess extends Thread {
    private Worker worker;
    private List<Task> tasks;
    private final int WORK_DAY_HOURS = 8;

    public WorkingProcess(Worker worker) {
        this.worker = worker;
        this.tasks = worker.getTasks();
    }

    @Override
    public void run() {
        int id = worker.getId();
        System.out.println("Работник " + id + " начал работать.");

        while (!tasks.isEmpty()) {
            int workDayHours = 0;

            while (workDayHours < WORK_DAY_HOURS && !tasks.isEmpty()) {
                Task currentTask = tasks.get(0);
                int taskDuration = currentTask.getDuration();

                if (taskDuration > WORK_DAY_HOURS - workDayHours) {
                    int timeToWorkToday = WORK_DAY_HOURS - workDayHours;

                    System.out.println("Работник " + id + ". Выполняет задачу \"" +
                            currentTask.getTaskName() + "\". Время выполнения за сегодня: " +
                            timeToWorkToday + " ч. Осталось: " + (taskDuration - timeToWorkToday) + " ч.");

                    try {
                        Thread.sleep(100 * timeToWorkToday);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    workDayHours += timeToWorkToday;
                    currentTask = new Task(currentTask.getTaskName(), taskDuration - timeToWorkToday,
                            currentTask.getOriginalDuration(), false);

                    synchronized (tasks) {
                        tasks.set(0, currentTask);
                    }
                    worker.setTasks(tasks);

                } else {
                    System.out.println("Работник " + id + ". Выполняет задачу \"" +
                            currentTask.getTaskName() + "\". Время выполнения: " +
                            taskDuration + " ч.");

                    try {
                        Thread.sleep(100 * taskDuration);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    currentTask.setStatus(true);
                    worker.addCompletedTask(currentTask);

                    workDayHours += taskDuration;

                    synchronized (tasks) {
                        tasks.remove(0);
                    }
                    worker.setTasks(tasks);
                }
            }

            int dayIdleTime = WORK_DAY_HOURS - workDayHours;
            worker.addDailyStat(workDayHours, dayIdleTime);
            int efficiency = (workDayHours * 100) / WORK_DAY_HOURS;

            System.out.println("Работник " + id + ". День " + worker.getDaysWorked() +
                    ". Работал: " + workDayHours + " ч. Простой: " + dayIdleTime + " ч." +
                    " Эффективность: " + efficiency + " %");
        }

        System.out.println("Работник " + id + " завершил все задачи.");
    }
}