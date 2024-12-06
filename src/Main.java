import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    // Метод для копирования файла с использованием потоков
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        // Создаем переменную для входного и выходного потока
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            // создание входного потока для чтения из исходного файла
            // создание выходного потока для записи в целевой файл
            byte[] buffer = new byte[1024]; // Буфер для хранения данных, считываемых из файла
            int length; // Переменная для хранения количества считанных байтов
            while ((length = is.read(buffer)) > 0) { // Чтение данных из входного потока
                os.write(buffer, 0, length); // Запись данных в выходной поток
            }
        } catch (IOException exception){
            exception.printStackTrace(); // Вывод стектрейса
        }
    }

    // Метод для последовательного копирования файлов
    public static void sequentialCopy() {
        // Создание файлов для исходного и целевого
        File sFile1 = new File("src\\sFile1.txt");
        File dFile1 = new File("src\\dFile1.txt");
        File sFile2 = new File("src\\sFile2.txt");
        File dFile2 = new File("src\\dFile2.txt");

        long startTime = System.nanoTime(); // Запоминаем время начала копирования
        try {
            // Копирование файлов
            copyFileUsingStream(sFile1, dFile1);
            copyFileUsingStream(sFile2, dFile2);
        } catch (IOException e) {
            e.printStackTrace(); // Вывод стектрейса
        }
        long endTime = System.nanoTime(); // Запоминаем время окончания копирования
        // Выводим время, затраченное на последовательное копирование
        System.out.println("Время последовательного копирования: " + (endTime - startTime) + " нс");
    }

    // Метод для параллельного копирования файлов
    public static void parallelCopy() {
        // Объявление файлов для исходного и целевого
        File sFile1 = new File("src\\sFile1.txt");
        File dFile1 = new File("src\\dFile1.txt");
        File sFile2 = new File("src\\sFile2.txt");
        File dFile2 = new File("src\\dFile2.txt");

        // Создаем пул потоков с 2 потоками
        ExecutorService executor = Executors.newFixedThreadPool(2);
        long startTime = System.nanoTime(); // Запоминаем время начала копирования

        try {
            // Запускаем таски копирования параллельно
            executor.invokeAll(List.of(
                    Executors.callable(() -> { // Первая таска для копирования sFile1
                        try {
                            copyFileUsingStream(sFile1, dFile1);
                        } catch (IOException e) {
                            throw new RuntimeException(e); // Вывод стектрейса
                        }
                    }),
                    Executors.callable(() -> { // Вторая таска для копирования sFile2
                        try {
                            copyFileUsingStream(sFile2, dFile2);
                        } catch (IOException e) {
                            throw new RuntimeException(e); // Вывод стектрейса
                        }
                    })
            ));
        } catch (InterruptedException e) { // Если поток был прерван
            e.printStackTrace(); // Вывод стектрейса
        } finally {
            executor.shutdown(); // Закрытие пула
        }

        long endTime = System.nanoTime(); // Запоминаем время окончания копирования
        // Выводим время, затраченное на параллельное копирование
        System.out.println("Время параллельного копирования: " + (endTime - startTime) + " нс");
    }

    public static void main(String[] args) { // Копирование одного файла
        File sourceFile = new File("src\\source.txt"); // Исходный файл
        File destFile = new File("src\\destination.txt"); // Целевой файл
        long copyStartTime = System.nanoTime(); // Запоминаем время начала копирования

        try {
            copyFileUsingStream(sourceFile, destFile); // Копируем файл
            long copyEndTime = System.nanoTime(); // Запоминаем время окончания копирования
            System.out.println("Файл успешно скопирован. Время копирования: " + (copyEndTime - copyStartTime) + " нс");
        } catch (IOException e) {
            e.printStackTrace(); // Вывод стектрейса
        }

        sequentialCopy(); // Последовательное копирование
        parallelCopy(); // Параллельное копирование
    }
}