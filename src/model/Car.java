package model;

import static utils.Constants.LINE_SEPARATOR;

public class Car {
    private String id;
    private String make;
    private String model;
    private String year;
    private String type;

    public Car(String id, String make, String model, String year, String type) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
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

    @Override
    public String toString() {
        return String.format("""
                %s
                Make: %s
                Model: %s
                Year: %s
                Type: %s
                """,LINE_SEPARATOR,make,model,year,type);


    }
}
