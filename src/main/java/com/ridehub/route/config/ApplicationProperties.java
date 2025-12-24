package com.ridehub.route.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Ms Route.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final GraphHopper graphHopper = new GraphHopper();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public GraphHopper getGraphHopper() {
        return graphHopper;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class GraphHopper {

        private String apiKey;
        private String baseUrl = "https://graphhopper.com/api/1";
        private Integer connectTimeout = 5000;
        private Integer readTimeout = 10000;
        private Integer queryLimit = 1000;
        private String vehicleProfile = "car";
        private String locale = "en";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Integer getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
        }

        public Integer getQueryLimit() {
            return queryLimit;
        }

        public void setQueryLimit(Integer queryLimit) {
            this.queryLimit = queryLimit;
        }

        public String getVehicleProfile() {
            return vehicleProfile;
        }

        public void setVehicleProfile(String vehicleProfile) {
            this.vehicleProfile = vehicleProfile;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }
    // jhipster-needle-application-properties-property-class
}
