package org.test.jakarta.hello.dto;

public class ProductDto {
    private Long id;
    private String name;

    public double getPrice() {
        return price;
    }

    public ProductDto(Long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private double price;
}
