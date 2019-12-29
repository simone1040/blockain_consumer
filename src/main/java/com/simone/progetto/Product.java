package com.simone.progetto;

import java.io.Serializable;


public class Product implements Serializable{
	
	private String name;
	private double price;
	
	public Product(String name, double price) {
		super();
		this.name = name;
		this.price = price;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getPrice() {
		return price;
	}
	
	
}
