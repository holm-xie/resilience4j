/*
 * Copyright 2017 Bohdan Storozhuk
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
package io.github.resilience4j.ratelimiter.autoconfigure;

import io.github.resilience4j.prometheus.RateLimiterExports;
import io.github.resilience4j.prometheus.collectors.RateLimiterMetricsCollector;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for resilience4j-metrics.
 */
@Configuration
@AutoConfigureAfter(value = RateLimiterAutoConfiguration.class)
@ConditionalOnClass(RateLimiterMetricsCollector.class)
public class RateLimiterPrometheusAutoConfiguration {

    @ConditionalOnProperty(value = "resilience4j.ratelimiter.metrics.use_legacy_collector", havingValue = "true")
    @Bean
    public RateLimiterExports legacyRateLimiterPrometheusCollector(RateLimiterRegistry rateLimiterRegistry){
        RateLimiterExports collector = RateLimiterExports.ofRateLimiterRegistry(rateLimiterRegistry);
        collector.register();
        return collector;
    }

    @Bean
    @ConditionalOnProperty(
        value = "resilience4j.ratelimiter.metrics.use_legacy_collector",
        havingValue = "false",
        matchIfMissing = true
    )
    public RateLimiterMetricsCollector rateLimiterPrometheusCollector(RateLimiterRegistry rateLimiterRegistry) {
        RateLimiterMetricsCollector collector = RateLimiterMetricsCollector.ofRateLimiterRegistry(rateLimiterRegistry);
        collector.register();
        return collector;
    }
}
