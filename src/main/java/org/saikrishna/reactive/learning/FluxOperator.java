package org.saikrishna.reactive.learning;

import com.github.javafaker.Faker;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public class FluxOperator {

    public static void main(String[] args) {
        Faker faker = Faker.instance();
        Consumer<Object> printConsumer = o -> System.out.println(o);
        Flux<String> nameFlex = Flux.generate(synchronousSink -> synchronousSink.next(faker.address().country()));
        Flux<Object> fluxUntilIndia = nameFlex.handle((country, synchronousSink) -> {
            synchronousSink.next(country);
            if (country.equalsIgnoreCase("Kuwait")) {
                synchronousSink.complete();
            }
        });

        fluxUntilIndia.subscribe(printConsumer);
    }
}
