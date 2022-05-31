package org.saikrishna.reactive.learning.service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.FluxSink;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.function.Consumer;

@AllArgsConstructor
public class FileReaderService implements Consumer<FluxSink<String>> {

    private FluxSink<String> fluxSink;

    private final Scanner scanner;

    public FileReaderService(Path path) throws FileNotFoundException {
        scanner = new Scanner(path.toFile());
    }


    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.fluxSink = stringFluxSink;
    }


    public void readLines() {
        while (scanner.hasNext()) {
            this.fluxSink.next(scanner.nextLine());
        }
        this.fluxSink.complete();
        this.scanner.close();
        System.out.println("FileStream Closed");
    }
}
