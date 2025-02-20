/*
 * Copyright 2021 vitasystems GmbH and Hannover Medical School.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehrbase.application.config.cache;

import org.ehrbase.cache.CacheOptions;
import org.ehrbase.service.KnowledgeCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Configuration} for EhCache using JCache.
 *
 * @author Renaud Subiger
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching
public class CacheConfiguration {

  @Bean
  public CacheOptions cacheOptions(CacheProperties properties) {
    var options = new CacheOptions();
    options.setPreBuildQueries(properties.isPreBuildQueries());
    options.setPreBuildQueriesDepth(properties.getPreBuildQueriesDepth());
    return options;
  }

  @Bean
  @ConditionalOnProperty(prefix = "cache", name = "init-on-startup", havingValue = "true")
  public CacheInitializer cacheInitializer(KnowledgeCacheService knowledgeCacheService) {
    return new CacheInitializer(knowledgeCacheService);
  }
}
