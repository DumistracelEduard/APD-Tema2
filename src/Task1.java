import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1 implements Runnable{
    private final int ID;
    private final long orders_size;
    private final int number_threads;
    private final String orders_file;

    public ExecutorService tpe;
    private final long product_size;
    private final String products_file;
    public Task1(int ID, long orders_size, int number_threads,
                 String orders_file, ExecutorService tpe,
                 long product_size, String products_file) {
        this.ID = ID;
        this.orders_size = orders_size;
        this.number_threads = number_threads;
        this.orders_file = orders_file;
        this.tpe = tpe;
        this.product_size = product_size;
        this.products_file = products_file;
    }

    @Override
    public void run() {
        long start = ID * orders_size / number_threads;
        long end = Math.min((ID + 1) * orders_size/number_threads, orders_size);

        try {
            /*verific daca end se afla pe pozita dorita*/
            FileReader fileReader = new FileReader(orders_file);
            BufferedReader br = new BufferedReader(fileReader);
            br.skip(end - 1);
            int line_ch = br.read();
            /* verific daca este \n */
            while (line_ch != 10) {
                end++;
                line_ch = br.read();
            }
            br.close();
            fileReader.close();

            /*verific daca start se afla pe pozita dorita*/
            fileReader = new FileReader(orders_file);
            br = new BufferedReader(fileReader);
            if(start != 0) {
                br.skip(start - 1);
            } else {
                br.skip(start);
            }
            line_ch = br.read();

            /* verific daca are in spate \n */
            while (line_ch != 10 && start != 0) {
                start++;
                line_ch = br.read();
            }
            br.close();
            fileReader.close();

            long contor = end - start;

            fileReader = new FileReader(orders_file);
            br = new BufferedReader(fileReader);
            br.skip(start);
            String line;

            /*citesc linie cu linie si creez task-urile pentru thread-urile de nivel2*/
            while((line = br.readLine()) != null && contor > 0) {
                char[] line_char = line.toCharArray();
                contor -= (line_char.length + 1);
                String[] split = line.split(",");
                if(Integer.parseInt(split[1]) == 0) {
                    continue;
                }
                AtomicInteger number = new AtomicInteger(Integer.parseInt(split[1]));
                Tema2.inQueue.incrementAndGet();
                for(int i = 0; i < number_threads; i++) {
                    Worker worker = new Worker(split[0], tpe, product_size,
                            i, number_threads, products_file, number, Integer.parseInt(split[1]));
                    tpe.submit(worker);
                }

            }

            br.close();
            fileReader.close();
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
    }
}
