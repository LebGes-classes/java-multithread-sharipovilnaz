package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class FileManager {
    private static final String WORKER_SHEET = "Работники";
    private static final String TASKS_SHEET = "Задачи";
    private static final String DAILY_EFFICIENCY_SHEET = "Дневная статистика";

    public static void writeToFile(String filePath, List<Worker> workers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createWorkerSheet(workbook, workers);
            createTaskSheet(workbook, workers);
            createDailyStatsSheet(workbook, workers);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("Данные успешно записаны в файл");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи файла", e);
        }
    }

    private static void createWorkerSheet(Workbook workbook, List<Worker> workers) {
        Sheet sheet = workbook.createSheet(WORKER_SHEET);
        createHeader(sheet, new String[]{"Id", "ФИО", "Общее время работы", "Время простоя", "Кол-во дней"});
        int rowNum = 1;
        for (Worker worker : workers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(worker.getId());
            row.createCell(1).setCellValue(worker.getName());
            row.createCell(2).setCellValue(worker.getTotalWorkTime());
            row.createCell(3).setCellValue(worker.getTotalIdleTime());
            row.createCell(4).setCellValue(worker.getDaysWorked());
        }
        autoSizeColumns(sheet, 5);
    }

    private static void createTaskSheet(Workbook workbook, List<Worker> workers) {
        Sheet sheet = workbook.createSheet(TASKS_SHEET);
        createHeader(sheet, new String[]{"Id работника", "Задача", "Исходная длительность", "Оставшееся время", "Статус"});

        int rowNum = 1;
        for (Worker worker : workers) {
            for (Task task : worker.getTasks()) {
                addTaskToSheet(sheet, rowNum++, worker.getId(), task);
            }
            for (Task task : worker.getCompletedTasks()) {
                addTaskToSheet(sheet, rowNum++, worker.getId(), task);
            }
        }
        autoSizeColumns(sheet, 5);
    }

    private static void addTaskToSheet(Sheet sheet, int rowNum, int workerId, Task task) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(workerId);
        row.createCell(1).setCellValue(task.getTaskName());
        row.createCell(2).setCellValue(task.getOriginalDuration());
        row.createCell(3).setCellValue(task.getDuration());
        row.createCell(4).setCellValue(task.getStatus() ? "Выполнена" : "Не выполнена");
    }

    private static void createDailyStatsSheet(Workbook workbook, List<Worker> workers) {
        Sheet sheet = workbook.createSheet(DAILY_EFFICIENCY_SHEET);
        createHeader(sheet, new String[]{
                "ID", "Имя", "День", "Работал (ч)", "Простой (ч)", "Эффективность (%)"});

        int rowNum = 1;
        for (Worker worker : workers) {
            List<Integer> efficiencies = worker.getDailyEfficiency();
            int daysWorked = worker.getDaysWorked();

            for (int i = 0; i < Math.min(daysWorked, efficiencies.size()); i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(worker.getId());
                row.createCell(1).setCellValue(worker.getName());
                row.createCell(2).setCellValue(i + 1);
                row.createCell(3).setCellValue(worker.getDailyWorkHours().get(i));
                row.createCell(4).setCellValue(worker.getDailyIdleHours().get(i));
                row.createCell(5).setCellValue(efficiencies.get(i));
            }
        }
        autoSizeColumns(sheet, 6);
    }

    private static void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    public static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static List<Worker> readFromFile(String filePath) {
        List<Worker> workers = new ArrayList<>();
        Map<Integer, Worker> workerMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            readWorkerSheet(workbook, workerMap, workers);
            readTasksSheet(workbook, workerMap);
            readDailyStatsSheet(workbook, workerMap);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: " + filePath, e);
        }

        return workers;
    }

    private static void readWorkerSheet(Workbook workbook, Map<Integer, Worker> workerMap, List<Worker> workers) {
        Sheet sheet = workbook.getSheet(WORKER_SHEET);
        if (sheet == null) return;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            int id = safeGetIntValue(row.getCell(0));
            if (id == -1) continue;

            String name = safeGetStringValue(row.getCell(1));
            if (name == null || name.isEmpty()) continue;

            Worker worker = workerMap.computeIfAbsent(id, k -> new Worker(id, name));
            worker.setTotalWorkTime(safeGetIntValue(row.getCell(2)));
            worker.setTotalIdleTime(safeGetIntValue(row.getCell(3)));
            worker.setDaysWorked(safeGetIntValue(row.getCell(4)));

            if (!workers.contains(worker)) {
                workers.add(worker);
            }
        }
    }

    private static void readTasksSheet(Workbook workbook, Map<Integer, Worker> workerMap) {
        Sheet sheet = workbook.getSheet(TASKS_SHEET);
        if (sheet == null) return;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            int workerId = safeGetIntValue(row.getCell(0));
            if (workerId == -1) continue;

            String taskName = safeGetStringValue(row.getCell(1));
            if (taskName == null || taskName.isEmpty()) continue;

            int origDuration = safeGetIntValue(row.getCell(2));
            int currentDuration = safeGetIntValue(row.getCell(3));
            boolean isCompleted = "Выполнена".equals(safeGetStringValue(row.getCell(4)));

            Worker worker = workerMap.get(workerId);
            if (worker == null) continue;

            Task task = new Task(taskName, currentDuration, origDuration, isCompleted);

            if (isCompleted) {
                worker.addCompletedTask(task);
            } else {
                worker.addTask(task);
            }
        }
    }

    private static void readDailyStatsSheet(Workbook workbook, Map<Integer, Worker> workerMap) {
        Sheet sheet = workbook.getSheet(DAILY_EFFICIENCY_SHEET);
        if (sheet == null) return;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            int id = safeGetIntValue(row.getCell(0));
            String name = safeGetStringValue(row.getCell(1));
            int day = safeGetIntValue(row.getCell(2));
            int workHours = safeGetIntValue(row.getCell(3));
            int idleHours = safeGetIntValue(row.getCell(4));

            if (id == -1 || name == null || name.isEmpty()) return;

            Worker worker = workerMap.computeIfAbsent(id, k -> new Worker(id, name));
            worker.addDailyStat(workHours, idleHours);
        }
    }

    private static int safeGetIntValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private static String safeGetStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }


}