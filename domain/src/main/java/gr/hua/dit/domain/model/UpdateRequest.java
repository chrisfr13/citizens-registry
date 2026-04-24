package gr.hua.dit.domain.model;

import jakarta.validation.constraints.Pattern;

public class UpdateRequest {

    @Pattern(
        regexp = "^\\d{9}$",
        message = "Το ΑΦΜ πρέπει να αποτελείται από 9 ψηφία"
    )
    private String afm;

    private String address;

    public UpdateRequest() {}

    public UpdateRequest(String afm, String address) {
        this.afm = afm;
        this.address = address;
    }

    public String getAfm() { return afm; }
    public void setAfm(String afm) { this.afm = afm; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
