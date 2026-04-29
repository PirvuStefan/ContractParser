package org.example.contractparser;

public enum ContractField {

    // From ID card
    NAME("ɛ", "Name"),
    SERIES("ɜ", "Series"),
    NUMBER("ɝ", "Number"),
    CNP("ɞ", "CNP"),
    ISSUED_BY("ɟ", "Issued By"),
    ADDRESS("ɠ", "Address"),
    VALIDITY("ɣ", "Validity"),

    // Birth info
    BIRTH_DATE("ȕ", "Birth Date"),
    BIRTH_PLACE("Ȣ", "Birth Place"),
    BIRTH_COUNTRY("Ȥ", "Birth Country"),

    // Address details
    ADDRESS_COUNTY("Ƚ", "Address County"),
    ADDRESS_CITY("ʦ", "Address City"),
    ADDRESS_STREET("Ɋ", "Address Street"),
    ADDRESS_NUMBER("ʡ", "Address Number"),
    ADDRESS_BLOC("ʠ", "Address Bloc"),
    ADDRESS_SCARA("ʢ", "Address Scara"),
    ADDRESS_ETAJ("ʣ", "Address Etaj"),
    ADDRESS_APARTMENT("ʤ", "Address Apartment"),

    // Filled in by the app
    REGISTRATION_NUMBER("ɔ", "Registration Number"),
    TODAY_DATE("ɖ", "Today's Date"),
    HIRE_DATE("ɐ", "Hire Date"),
    PHONE("ɕ", "Phone"),
    LOCATION("ɘ", "Location"),
    CITY("ə", "City"),
    SALARY("ɥ", "Salary");

    private final String placeholder;
    private final String label;

    ContractField(String placeholder, String label) {
        this.placeholder = placeholder;
        this.label = label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getLabel() {
        return label;
    }
}