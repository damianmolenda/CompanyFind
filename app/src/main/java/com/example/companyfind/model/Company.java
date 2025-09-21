package com.example.companyfind.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {
    private String name;
    private String nip;
    private String regon;
    private String address;
    private String status;
    private String type;
    private String province;
    private String district;
    private String municipality;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;

    public Company() {}

    protected Company(Parcel in) {
        name = in.readString();
        nip = in.readString();
        regon = in.readString();
        address = in.readString();
        status = in.readString();
        type = in.readString();
        province = in.readString();
        district = in.readString();
        municipality = in.readString();
        city = in.readString();
        postalCode = in.readString();
        street = in.readString();
        houseNumber = in.readString();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }

    public String getRegon() { return regon; }
    public void setRegon(String regon) { this.regon = regon; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            sb.append(street);
            if (houseNumber != null && !houseNumber.isEmpty()) {
                sb.append(" ").append(houseNumber);
            }
            sb.append("\n");
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(postalCode).append(" ");
        }
        if (city != null && !city.isEmpty()) {
            sb.append(city);
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(nip);
        dest.writeString(regon);
        dest.writeString(address);
        dest.writeString(status);
        dest.writeString(type);
        dest.writeString(province);
        dest.writeString(district);
        dest.writeString(municipality);
        dest.writeString(city);
        dest.writeString(postalCode);
        dest.writeString(street);
        dest.writeString(houseNumber);
    }
}
