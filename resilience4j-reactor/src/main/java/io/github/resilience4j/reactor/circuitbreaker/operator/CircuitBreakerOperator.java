/*
 * Copyright 2018 Julien Hoarau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.resilience4j.reactor.circuitbreaker.operator;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.FluxResilience;
import io.github.resilience4j.reactor.MonoResilience;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

/**
 * A Reactor operator which wraps a reactive type in a circuit breaker.
 *
 * @param <T> the value type of the upstream and downstream
 */
public class CircuitBreakerOperator<T> implements UnaryOperator<Publisher<T>> {

    private final CircuitBreaker circuitBreaker;

    private CircuitBreakerOperator(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    /**
     * Creates a CircuitBreakerOperator.
     *
     * @param <T>            the value type of the upstream and downstream
     * @param circuitBreaker the circuit breaker
     * @return a CircuitBreakerOperator
     */
    public static <T> CircuitBreakerOperator<T> of(CircuitBreaker circuitBreaker) {
        return new CircuitBreakerOperator<>(circuitBreaker);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        if (publisher instanceof Mono) {
            return MonoResilience
                    .onAssembly(new MonoCircuitBreaker<>((Mono<? extends T>) publisher, circuitBreaker));
        } else if (publisher instanceof Flux) {
            return FluxResilience
                    .onAssembly(new FluxCircuitBreaker<>((Flux<? extends T>) publisher, circuitBreaker));
        }

        throw new IllegalStateException("Publisher of type <" + publisher.getClass().getSimpleName()
                + "> are not supported by this operator");
    }
}
