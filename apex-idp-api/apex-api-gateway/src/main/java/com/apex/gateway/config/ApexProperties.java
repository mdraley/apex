package com.apex.gateway.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apex")
public class ApexProperties {
    private Jwt jwt = new Jwt();
    private Storage storage = new Storage();
    private Processing processing = new Processing();
    private Integration integration = new Integration();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }
    public Processing getProcessing() { return processing; }
    public void setProcessing(Processing processing) { this.processing = processing; }
    public Integration getIntegration() { return integration; }
    public void setIntegration(Integration integration) { this.integration = integration; }

    public static class Jwt {
        private String secret;
        private Integer accessTokenExpiration;
        private Integer refreshTokenExpiration;
        private Integer expiration; // legacy
        private Integer refreshExpiration; // legacy
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public Integer getAccessTokenExpiration() { return accessTokenExpiration; }
        public void setAccessTokenExpiration(Integer accessTokenExpiration) { this.accessTokenExpiration = accessTokenExpiration; }
        public Integer getRefreshTokenExpiration() { return refreshTokenExpiration; }
        public void setRefreshTokenExpiration(Integer refreshTokenExpiration) { this.refreshTokenExpiration = refreshTokenExpiration; }
        public Integer getExpiration() { return expiration; }
        public void setExpiration(Integer expiration) { this.expiration = expiration; }
        public Integer getRefreshExpiration() { return refreshExpiration; }
        public void setRefreshExpiration(Integer refreshExpiration) { this.refreshExpiration = refreshExpiration; }
    }

    public static class Storage {
        private String type; // local|minio|s3
        private Local local = new Local();
        private Minio minio = new Minio();
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Local getLocal() { return local; }
        public void setLocal(Local local) { this.local = local; }
        public Minio getMinio() { return minio; }
        public void setMinio(Minio minio) { this.minio = minio; }
        public static class Local {
            private String basePath;
            public String getBasePath() { return basePath; }
            public void setBasePath(String basePath) { this.basePath = basePath; }
        }
        public static class Minio {
            private String endpoint;
            private String accessKey;
            private String secretKey;
            private String bucketName;
            public String getEndpoint() { return endpoint; }
            public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
            public String getAccessKey() { return accessKey; }
            public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
            public String getSecretKey() { return secretKey; }
            public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
            public String getBucketName() { return bucketName; }
            public void setBucketName(String bucketName) { this.bucketName = bucketName; }
        }
    }

    public static class Processing {
        private Long maxFileSize;
        private List<String> allowedTypes;
        private Ocr ocr = new Ocr();
        public Long getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; }
        public List<String> getAllowedTypes() { return allowedTypes; }
        public void setAllowedTypes(List<String> allowedTypes) { this.allowedTypes = allowedTypes; }
        public Ocr getOcr() { return ocr; }
        public void setOcr(Ocr ocr) { this.ocr = ocr; }
        public static class Ocr {
            private boolean enabled;
            private String serviceUrl;
            private Double confidenceThreshold;
            private Tesseract tesseract = new Tesseract();
            private Layoutlm layoutlm = new Layoutlm();
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            public String getServiceUrl() { return serviceUrl; }
            public void setServiceUrl(String serviceUrl) { this.serviceUrl = serviceUrl; }
            public Double getConfidenceThreshold() { return confidenceThreshold; }
            public void setConfidenceThreshold(Double confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
            public Tesseract getTesseract() { return tesseract; }
            public void setTesseract(Tesseract tesseract) { this.tesseract = tesseract; }
            public Layoutlm getLayoutlm() { return layoutlm; }
            public void setLayoutlm(Layoutlm layoutlm) { this.layoutlm = layoutlm; }
            public static class Tesseract {
                private boolean enabled;
                private String language;
                private Integer dpi;
                public boolean isEnabled() { return enabled; }
                public void setEnabled(boolean enabled) { this.enabled = enabled; }
                public String getLanguage() { return language; }
                public void setLanguage(String language) { this.language = language; }
                public Integer getDpi() { return dpi; }
                public void setDpi(Integer dpi) { this.dpi = dpi; }
            }
            public static class Layoutlm {
                private boolean enabled;
                private String modelPath;
                public boolean isEnabled() { return enabled; }
                public void setEnabled(boolean enabled) { this.enabled = enabled; }
                public String getModelPath() { return modelPath; }
                public void setModelPath(String modelPath) { this.modelPath = modelPath; }
            }
        }
    }

    public static class Integration {
        private Cpsi cpsi = new Cpsi();
        private Eclinicalworks eclinicalworks = new Eclinicalworks();
        public Cpsi getCpsi() { return cpsi; }
        public void setCpsi(Cpsi cpsi) { this.cpsi = cpsi; }
        public Eclinicalworks getEclinicalworks() { return eclinicalworks; }
        public void setEclinicalworks(Eclinicalworks eclinicalworks) { this.eclinicalworks = eclinicalworks; }
        public static class Cpsi {
            private boolean enabled;
            private String endpoint;
            private String apiKey;
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            public String getEndpoint() { return endpoint; }
            public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
            public String getApiKey() { return apiKey; }
            public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        }
        public static class Eclinicalworks {
            private boolean enabled;
            private String fhirEndpoint;
            private String clientId;
            private String clientSecret;
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            public String getFhirEndpoint() { return fhirEndpoint; }
            public void setFhirEndpoint(String fhirEndpoint) { this.fhirEndpoint = fhirEndpoint; }
            public String getClientId() { return clientId; }
            public void setClientId(String clientId) { this.clientId = clientId; }
            public String getClientSecret() { return clientSecret; }
            public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        }
    }
}
