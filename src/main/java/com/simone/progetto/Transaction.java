package com.simone.progetto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id_client;
	private Product product;
	private Integer quantity;
	private long timestamp;

	public String getStringToHash(){
		return this.id_client.toString() + this.product.getName()
				+ this.product.getPrice() + this.quantity.toString();
	}
}
