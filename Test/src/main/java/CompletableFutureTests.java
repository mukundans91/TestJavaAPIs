import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class CompletableFutureTests {
    ExecutorService executorService = Executors.newFixedThreadPool(1, Executors.defaultThreadFactory());
    int count = 0;
    AtomicInteger secondCount = new AtomicInteger(0);

    int max = 10;

    public static void main(String[] args) {

        CompletableFutureTests tests = new CompletableFutureTests();

        List<CompletableFuture<String>> futures = new ArrayList<>();
        while (true){
            if(tests.getSecondCount() < tests.max) {
                CompletableFuture<String> data = tests.getData("Complete NonBlock " + tests.secondCount, tests.executorService);
                data.thenAccept(System.out::println);
                futures.add(data);
                tests.increaseSecondCount(1);
            } else {
                /*
                //blocking call
                futures.forEach(f -> {
                   try {
                       System.out.println(f.get());
                   } catch (InterruptedException | ExecutionException e) {
                       e.printStackTrace();
                   }
               });
               futures.clear();
               tests.secondCount = 0;
                 */
                if(futures.size() > 0) {
                    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
                    futures.clear();
                    allOf.thenRun(tests::clearSecondCount)
                            .exceptionally(r -> {
                                System.out.println(r.getLocalizedMessage());
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tests.clearSecondCount();
                                return null;
                            });
                }
            }

        }
    }



    private int getSecondCount() {
        return secondCount.get();
    }

    private void increaseSecondCount(int delta) {
        secondCount.addAndGet(delta);
    }

    private void clearSecondCount() {
        secondCount.set(0);
    }

    private void completeBlock() {
        CompletableFutureTests tests = new CompletableFutureTests();
        CompletableFuture<String> data = tests.getDataRun("Complete Block " + count++);
        data.thenAccept(System.out::println);
    }

    private void completeNever() {
        CompletableFutureTests tests = new CompletableFutureTests();
        CompletableFuture<String> data = tests.getDataNever("Complete Never");
        data.thenAccept(System.out::println);
    }

    private void completeNonBlock() {

//        if(secondCount < max) {
//            CompletableFuture<String> data = getData(secondCount + " Complete NonBlock " + count++, executorService);
//            data.thenApply(r -> decreaseSecondCount(r));
//            secondCount++;
//        }
    }

    private Void decreaseSecondCount(String r) {
        System.out.println(r);
//        secondCount--;
        return null;
    }

    private CompletableFuture<String> getDataNever(String some_data) {
        CompletableFuture<String> promise = new CompletableFuture<>();
        return promise;
    }

    private CompletableFuture<String> getDataRun(String some_data) {
        CompletableFuture<String> promise = new CompletableFuture<>();

        new Thread(
                () -> {
                    try {
                        Thread.sleep(1000);
                        promise.complete(some_data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        promise.completeExceptionally(e);
                    }
                }).run();
        return promise;

    }


    private CompletableFuture<String> getData(String some_data, ExecutorService executorService) {
        System.out.println("batch number " + secondCount);
        CompletableFuture<String> promise = new CompletableFuture<>();

        try {
            executorService.submit(
                    () -> {
                        try {
                            Random random = new Random();
                            if(random.nextInt(200) < 10) {
                                throw new Exception("Random exception " + some_data);
                            }
                            Thread.sleep(1000);
//                            System.out.println(System.currentTimeMillis());
//                            System.out.println(Thread.currentThread().getName());
//                            System.out.println(some_data);
                            promise.complete(some_data);
                        } catch (Exception e) {
                            promise.completeExceptionally(e);
                        }
                    });
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        return promise;

    }

}
