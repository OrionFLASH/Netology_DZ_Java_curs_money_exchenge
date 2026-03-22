package ru.netology.moneytransfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки сервиса: демо-код подтверждения и каталог файла логов переводов.
 */
@ConfigurationProperties(prefix = "money-transfer")
public class MoneyTransferProperties {

    private final Confirmation confirmation = new Confirmation();
    private final Log log = new Log();

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public Log getLog() {
        return log;
    }

    public static class Confirmation {
        /**
         * Код, который принимается при подтверждении (учебный сценарий без SMS).
         */
        private String demoCode = "0000";

        public String getDemoCode() {
            return demoCode;
        }

        public void setDemoCode(String demoCode) {
            this.demoCode = demoCode;
        }
    }

    public static class Log {
        private String directory = "log";
        private String fileName = "transfers.log";

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
