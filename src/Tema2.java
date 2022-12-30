import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static StringBuilder folder; //order.txt
    public static long order_size;
    public static StringBuilder folder2; //order_products.txt
    public static long order_products_size;
    public static Integer threads_number;
    public static FileWriter fileWriter;
    public static FileWriter fileWriter2;

    public static AtomicInteger inQueue;

    public static void main(String[] args) throws IOException, InterruptedException {
        /*citesc input-ul*/
        folder = new StringBuilder(args[0]);
        threads_number = Integer.parseInt(args[1]);
        folder.append("/");
        folder2 = new StringBuilder(folder);

        folder.append("orders.txt");
        folder2.append("order_products.txt");

        /*dimensiunea fisierelor*/
        order_size = Files.size(Paths.get(folder.toString()));
        order_products_size = Files.size(Paths.get(folder2.toString()));

        /*creez thread-urile*/
        Thread[] threads1 = new Thread[threads_number];
        ExecutorService tpe = Executors.newFixedThreadPool(threads_number);
        inQueue = new AtomicInteger(0);

        /*deschid fisierele in care realizez scrierea*/
        fileWriter = new FileWriter("orders_out.txt");
        fileWriter2 = new FileWriter("order_products_out.txt");

        /*thread-uri de nivel 1*/
        for(int i = 0; i < threads_number; i++) {
            threads1[i] = new Thread(new Task1(i, order_size, threads_number,
                    folder.toString(), tpe, order_products_size,
                    folder2.toString()));
            threads1[i].start();
        }
        for(int i = 0; i < threads_number; i++) {
            try {
                threads1[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*astept pana cand toate thread-urile de nivel 2 termina*/
        tpe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        /*inchid fisierele*/
        fileWriter.close();
        fileWriter2.close();
    }
}
