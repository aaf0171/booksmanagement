package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ItemPatchDTO {

    public static class BarcodePatchDTO {
        @NotBlank(message = "Value is mandatory")
        @Size(max = 255, message = "Maximum length: 255")
        private String value;

        public BarcodePatchDTO() {}

        public BarcodePatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class StatusPatchDTO {
        @NotNull(message = "Value is mandatory")
        private String value;

        public StatusPatchDTO() {}

        public StatusPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class LocationPatchDTO {
        @Size(max = 255, message = "Maximum length: 255")
        private String value;

        public LocationPatchDTO() {}

        public LocationPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class AcquisitionDatePatchDTO {
        private LocalDate value;

        public AcquisitionDatePatchDTO() {}

        public AcquisitionDatePatchDTO(LocalDate value) {
            this.value = value;
        }

        public LocalDate getValue() {
            return value;
        }

        public void setValue(LocalDate value) {
            this.value = value;
        }
    }
}
