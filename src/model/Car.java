package model;

import static utils.Constants.LINE_SEPARATOR;

public class Car {
    private String id;
    private String make;
    private String model;
    private String year;
    private String type;
    private String availability;

    public Car(String id, String make, String model, String year, String type, String availability) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return String.format("""
                ID: %s
                Make: %s
                Model: %s
                Year: %s
                Type: %s
                Availability: %s
                %s""",id,make,model,year,type,availability,LINE_SEPARATOR);


    }
}
