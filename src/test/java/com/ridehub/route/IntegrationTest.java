package com.ridehub.route;

import com.ridehub.route.config.AsyncSyncConfiguration;
import com.ridehub.route.config.EmbeddedElasticsearch;
import com.ridehub.route.config.EmbeddedKafka;
import com.ridehub.route.config.EmbeddedRedis;
import com.ridehub.route.config.EmbeddedSQL;
import com.ridehub.route.config.JacksonConfiguration;
import com.ridehub.route.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { MsRouteApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
