package org.saikrishna.reactive.learning;

import org.saikrishna.reactive.learning.service.FileReaderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ReactiveFileService {

    public static void main(String[] args) throws FileNotFoundException {
        Path filePath = Paths.get("src/main/resources/", "Sample.txt");
        //generate(filePath);
        create(filePath);
    }

    private static void generate(Path path) {
        fluxGenerated(path)
                .subscribe(System.out::println,
                        System.err::println,
                        () -> System.out.println("Completed"));
    }

    private static void create(Path filePath) throws FileNotFoundException {
        FileReaderService fileReaderService = new FileReaderService(filePath);


        fluxCreated(fileReaderService)
                .subscribe(o -> System.out.println(o),
                        throwable -> System.out.println(throwable.getMessage()),
                        () -> System.out.println("Completed"));


        fileReaderService.readLines();
    }

    private static Flux<String> fluxGenerated(Path filePath) {
        return Flux.generate(
                initializeFileScanner(filePath),
                readFileLines(),
                closeFileScanner());
    }

    private static Flux<String> fluxCreated(FileReaderService fileReaderService) {
        return Flux.create(fileReaderService);
    }

    private static Consumer<Scanner> closeFileScanner() {
        return scanner -> {
            System.out.println("FileStream will be closed");
            scanner.close();
        };
    }

    private static BiFunction<Scanner, SynchronousSink<Object>, Scanner> readFileLines() {
        return (scanner, synchronousSink) -> {
            if (scanner.hasNext()) {
                synchronousSink.next(scanner.nextLine());
            } else {
                synchronousSink.complete();
            }
            return scanner;
        };
    }


    private static Callable initializeFileScanner(Path filePath) {
        return () -> new Scanner(filePath.toFile());
    }
}
