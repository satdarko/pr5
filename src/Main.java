import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        long startTime = System.currentTimeMillis();
        System.out.println("Task 1: Parallel tasks, first to complete will be shown");

        // task 1
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Task 1 completed";
        }, executor);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            return "Task 2 completed";
        }, executor);

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            return "Task 3 completed";
        }, executor);

        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(task1, task2, task3);
        firstCompleted.thenAccept(result -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("First completed: " + result + " (Time taken: " + duration + " ms)");
        });

        System.out.println("Task 2: Flight booking simulation");
        long bookingStartTime = System.currentTimeMillis();

        // task 2
        CompletableFuture<Boolean> seatAvailability = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return checkSeatAvailability();
        }, executor);

        CompletableFuture<Double> findBestPrice = CompletableFuture.supplyAsync(() -> {
            sleep(1200);
            return getBestPrice();
        }, executor);

        CompletableFuture<Void> bookingProcess = seatAvailability.thenCombine(findBestPrice, (available, price) -> {
            if (available) {
                return bookFlight(price);
            }
            throw new RuntimeException("Seats not available");
        }).thenAccept(status -> {
            long duration = System.currentTimeMillis() - bookingStartTime;
            System.out.println("Booking status: " + status + " (Time taken: " + duration + " ms)");
        });

        CompletableFuture.allOf(firstCompleted, bookingProcess).join();
        executor.shutdown();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean checkSeatAvailability() {
        return true;  // Simulate seat availability check
    }

    private static double getBestPrice() {
        return 250.0;  // simulate best price retrieval
    }

    private static String bookFlight(double price) {
        return "Flight booked at price: " + price;
    }
}
