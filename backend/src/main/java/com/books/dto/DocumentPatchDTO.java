package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DocumentPatchDTO {

    public static class TitlePatchDTO {
        @NotBlank(message = "Value is mandatory")
        @Size(max = 255, message = "Maximum length: 255")
        private String value;

        public TitlePatchDTO() {}

        public TitlePatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SubtitlePatchDTO {
        @Size(max = 255, message = "Maximum length: 255")
        private String value;

        public SubtitlePatchDTO() {}

        public SubtitlePatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class DocumentTypePatchDTO {
        @NotNull(message = "Value is mandatory")
        private String value;

        public DocumentTypePatchDTO() {}

        public DocumentTypePatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class IsbnPatchDTO {
        @Size(max = 20, message = "Maximum length: 20")
        private String value;

        public IsbnPatchDTO() {}

        public IsbnPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class PublisherPatchDTO {
        @Size(max = 255, message = "Maximum length: 255")
        private String value;

        public PublisherPatchDTO() {}

        public PublisherPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class PublicationYearPatchDTO {
        @PositiveOrZero(message = "Must be a positive integer or zero")
        @Max(value = 2100, message = "Year must not exceed current year + 1")
        private Integer value;

        public PublicationYearPatchDTO() {}

        public PublicationYearPatchDTO(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public static class LanguagePatchDTO {
        @Size(max = 50, message = "Maximum length: 50")
        private String value;

        public LanguagePatchDTO() {}

        public LanguagePatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class DescriptionPatchDTO {
        private String value;

        public DescriptionPatchDTO() {}

        public DescriptionPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class CoverUrlPatchDTO {
        @Size(max = 500, message = "Maximum length: 500")
        private String value;

        public CoverUrlPatchDTO() {}

        public CoverUrlPatchDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class CreatedAtPatchDTO {
        private LocalDateTime value;

        public CreatedAtPatchDTO() {}

        public CreatedAtPatchDTO(LocalDateTime value) {
            this.value = value;
        }

        public LocalDateTime getValue() {
            return value;
        }

        public void setValue(LocalDateTime value) {
            this.value = value;
        }
    }
}
