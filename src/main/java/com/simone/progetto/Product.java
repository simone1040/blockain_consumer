package com.simone.progetto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class Product implements Serializable{
	private String name;
	private double price;
}
