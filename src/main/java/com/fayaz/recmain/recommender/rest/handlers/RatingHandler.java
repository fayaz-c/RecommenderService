package com.fayaz.recmain.recommender.rest.handlers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.fayaz.recmain.recommender.hibernate.dao.AlgorithmDAO;
import com.fayaz.recmain.recommender.hibernate.dao.CustomerConfigDAO;
import com.fayaz.recmain.recommender.hibernate.dao.CustomerDAO;
import com.fayaz.recmain.recommender.hibernate.dao.RatingsDAO;
import com.fayaz.recmain.recommender.hibernate.pojo.Algorithm;
import com.fayaz.recmain.recommender.hibernate.pojo.Customer;
import com.fayaz.recmain.recommender.hibernate.pojo.CustomerConfig;
import com.fayaz.recmain.recommender.hibernate.pojo.Rating;
import com.fayaz.recmain.recommender.AlgorithmType;
import com.fayaz.recmain.recommender.BaseRecommender;
import com.fayaz.recmain.recommender.ContentBasedRecommender;
import com.fayaz.recmain.recommender.HybridRecommender;
import com.fayaz.recmain.recommender.ItemItemCollabRecommender;
import com.fayaz.recmain.recommender.NonPersonalizedRecommender;
import com.fayaz.recmain.recommender.UserUserCollabRecommender;
import com.fayaz.recmain.recommender.rest.pojo.RatingItem;
import com.fayaz.recmain.recommender.rest.pojo.RecommendationItem;

public class RatingHandler {

	public int addRatings(List<RatingItem> ratings,long customerId,String customerSecret) throws RuntimeException{
		validateCustomer(customerId,customerSecret);	
		RatingsDAO dao = new RatingsDAO();
		int count = 0;
		for(RatingItem item:ratings){
			Rating newDbRating = new Rating(item.getUserId(),item.getProductId(),customerId,item.getRating());
			dao.addRating(newDbRating);
			count++;
		}
		return count;
	}
	
	public List<RecommendationItem> getRecommendations(long userId,long customerId, String customerSecret) {
		validateCustomer(customerId,customerSecret);
		BaseRecommender recommender = getRecommender(userId,customerId);
		int noOfRecommendations = getRecommendationCount(customerId);
		return recommender.getRecommendations(userId,customerId,noOfRecommendations);
	}
	
	private int getRecommendationCount(long customerId) {
		CustomerConfigDAO configDao = new CustomerConfigDAO();
		CustomerConfig config = configDao.getConfigByCustomerId(customerId);
		return (int) config.getReturnCount();
	}

	private BaseRecommender getRecommender(long userId,long customerId){
		CustomerConfigDAO configDao = new CustomerConfigDAO();
		AlgorithmDAO algoDao = new AlgorithmDAO();
		CustomerConfig config = configDao.getConfigByCustomerId(customerId);
		long algoId = config.getAlgoId();
		Algorithm algo = algoDao.getAlgorithmById(algoId);
		AlgorithmType algoType = AlgorithmType.getAlgorithmTypeInstance(algo.getAlgoName());
		return getRecommenderInstance(algoType);
		
	}
	
	public BaseRecommender getRecommenderInstance(AlgorithmType type){
		switch(type){
			case USER_COLLAB:
				return new UserUserCollabRecommender();
			case ITEM_COLLAB:
				return new ItemItemCollabRecommender();
			case HYBRID:
				return new HybridRecommender();
			case CONTENT:
				return new ContentBasedRecommender();
			case NON_PERSONAL:
			default:
				return new NonPersonalizedRecommender();
		}
	}
	
	
	public String getHashFromPassword(String saltPass) throws RuntimeException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		md.update(saltPass.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return sb.toString();
	}
	
	private void validateCustomer(long customerId,String customerSecret) throws RuntimeException{
		CustomerDAO customerDao = new CustomerDAO();
		Customer customer = customerDao.getCustomerById(customerId);
		String salt = customer.getSalt();
		String hashGenerated = getHashFromPassword(customerSecret+salt);
		String hashInDb = customer.getHash();
		if(!hashGenerated.equals(hashInDb))
			throw new RuntimeException("Customer Id - Secret COnfiguration mismatch");	
	}




}
