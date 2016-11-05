package com.fayaz.recmain.rest.pojo;

public class RecommendationItem {
	private long productId;
	private double score;
	
	public RecommendationItem(){}
	
	public RecommendationItem(long productId,double score){
		this.productId = productId;
		this.score = score;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
