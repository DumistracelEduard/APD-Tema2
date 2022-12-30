import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable{
    public String id_comanda;
    public ExecutorService tpe;
    private final long product_size;
    private final int ID;
    private final int number_threads;
    private final String products_file;
    public AtomicInteger number;
    private final int number_product;

    public Worker(String id_comanda, ExecutorService tpe,
                  long product_size, int ID, int number_threads, String products_file,
                  AtomicInteger number, int number_product) {
        this.id_comanda = id_comanda;
        this.tpe = tpe;
        this.product_size = product_size;
        this.ID = ID;
        this.number_threads = number_threads;
        this.products_file = products_file;
        this.number = number;
        this.number_product = number_product;
    }

    @Override
    public void run() {
        long start = ID * product_size / number_threads;
        long end = Math.min((ID + 1) * product_size/number_threads, product_size);
        try {
            /*verific daca end se afla pe pozita dorita*/
            FileReader fileReader = new FileReader(products_file);
            BufferedReader br = new BufferedReader(fileReader);
            br.skip(end - 1);
            int line_ch = br.read();
            while (line_ch != 10) {
                end++;
                line_ch = br.read();
            }
            br.close();
            fileReader.close();

            /*verific daca start se afla pe pozita dorita*/
            fileReader = new FileReader(products_file);
            br = new BufferedReader(fileReader);
            if(start != 0) {
                br.skip(start - 1);
            } else {
                br.skip(start);
            }
            line_ch = br.read();
            while (line_ch != 10 && start != 0) {
                start++;
                line_ch = br.read();
            }
            br.close();
            fileReader.close();

            long contor = end - start;

            fileReader = new FileReader(products_file);
            br = new BufferedReader(fileReader);
            br.skip(start);
            String line;

            /*caut id-ul comenzii in bucata alocata de citit*/
            while((line = br.readLine()) != null && contor > 0) {
                char[] line_char = line.toCharArray();
                contor -= (line_char.length + 1);

                String[] split = line.split(",");
                if(split[0].equals(id_comanda)) {
                    int data = number.decrementAndGet();
                    synchronized (this) {
                        Tema2.fileWriter2.write(split[0]+","+split[1]+",shipped\n");
                    }
                    if(data == 0) {
                        synchronized (this) {
                            Tema2.fileWriter.write(id_comanda + "," + number_product + ",shipped\n");
                        }
                    }
                }
            }
            int data = Tema2.inQueue.decrementAndGet();
            if(data == 0) {
                tpe.shutdown();
            }
            br.close();
            fileReader.close();
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }

    }
}
