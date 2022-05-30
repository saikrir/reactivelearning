package org.saikrishna.reactive.learning;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class StockObserver {

    public static void main(String[] args) throws InterruptedException {

        final CountDownLatch stockQuoteLatch = new CountDownLatch(1);

        float initialPrice = 100.0f;
        AtomicReference<Float> priceRef = new AtomicReference<>();
        priceRef.set(initialPrice);
        Flux.interval(Duration.ofSeconds(1))
                .map(aLong -> {
                    float variation = new Double(Math.ceil(Math.random() * 10) - 5).floatValue();
                    System.out.println("Price Variation $" + variation);
                    float value = variation + priceRef.get();
                    priceRef.set(value);
                    return value;
                })
                .subscribe(new Subscriber<Float>() {

                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.subscription = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Float aFloat) {

                        if (aFloat < initialPrice - 10 || aFloat > initialPrice + 10) {
                            this.subscription.cancel();
                            System.out.println("Subscription cancelled on prices $" + aFloat);
                            stockQuoteLatch.countDown();
                            return;
                        }
                        System.out.println("New Stock Price  $" + aFloat);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error occurred " + t);
                        stockQuoteLatch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed ");
                        stockQuoteLatch.countDown();
                    }
                });

        stockQuoteLatch.await();
    }

}
