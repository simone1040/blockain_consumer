package com.simone.progetto;

import java.io.Serializable;

public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id_client;
	private Product product;
	private Integer quantity;
	private long timestamp;
	transient private String hash;
	
	public Transaction(Integer id_client, Product product, Integer quantity, long timestamp) {
		super();
		this.id_client = id_client;
		this.product = product;
		this.quantity = quantity;
		this.timestamp = timestamp;
	}

	public Integer getId_client() {
		return id_client;
	}

	public void setId_client(Integer id_client) {
		this.id_client = id_client;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getStringToHash(){
		return this.id_client.toString() + this.product.getName()
				+ this.product.getPrice() + this.quantity.toString();
	}
	@Override
	public String toString() {
		return "Transaction [id_client=" + id_client + ", product=" + product + ", quantity=" + quantity
				+ ", timestamp=" + timestamp + "]";
	}
	
	
	
	
}
