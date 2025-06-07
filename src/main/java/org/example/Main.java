package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int m = 5;
        List<Worker> workers = FileManager.readFromFile("statistics.xlsx");
        List<WorkingProcess> threads = new ArrayList<>();
        Worker emp1 = new Worker(10, "Ilnaz");
        emp1.addTask(new Task("Подготовиться к экзамену", 30, false));
        workers.add(emp1);
        Worker emp2 = new Worker(15, "Artem");
        emp2.addTask(new Task("Вождение", 20, false));
        workers.add(emp2);
        Worker emp3 = new Worker(20, "Kamil");
        emp3.addTask(new Task("Пойти в парк", 5, false));
        workers.add(emp3);
        Worker emp4 = new Worker(25, "Vlad");
        emp4.addTask(new Task("Поиграть КС2", 3, false));
        workers.add(emp4);
        Worker emp5 = new Worker(30, "Egor");
        emp5.addTask(new Task("Поиграть в Valorant", 15, false));
        workers.add(emp5);
        Worker emp6 = new Worker(50, "Mikhail");
        emp6.addTask(new Task("Качалка", 9, false));
        workers.add(emp6);
        for (Worker worker : workers) {
            WorkingProcess thread = new WorkingProcess(worker);
            thread.start();
            threads.add(thread);
        }
//        WorkingProcess thread = new WorkingProcess(emp6);
//        thread.start();
//        threads.add(thread);
//        thread.join();
        for (WorkingProcess thread : threads) {
            thread.join();
        }

        for (Worker worker : workers) {
            System.out.println(worker);
        }
        FileManager.writeToFile("statistics.xlsx", workers);
    }
}