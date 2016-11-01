package recmain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.fayaz.recmain.rest.pojo.BaseResponse;
import com.fayaz.recmain.rest.pojo.PostRating;
import com.fayaz.recmain.rest.pojo.RatingItem;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class RatingServiceClient {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ClientConfig clientConfig = new DefaultClientConfig();              
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);     
		Client client = Client.create(clientConfig);

		WebResource webResource = client.resource("http://localhost:8070/RecMain/rating/add");
		RatingItem item = new RatingItem(12001,12001,3);
    	ArrayList<RatingItem> ratingsList = new ArrayList<RatingItem>();
    	ratingsList.add(item);
    	
    	PostRating postRatingRequest = new PostRating(2,"ecommerce",ratingsList);
		BaseResponse response = webResource.accept("application/json")
		                .type("application/json").post(BaseResponse.class, postRatingRequest);
		System.out.println(response.getStatus());
		System.out.println(response.getStatusMessage());
//		ObjectMapper mapper = new ObjectMapper();
//		String input = "{\"customerId\": 2,\"customerSecret\": \"ecommerce\",\"ratings\": [{\"userId\": 12003,\"productId\":"
//				+ " 12003,\"rating\": 4},{\"userId\": 12002,\"productId\": 12002,\"rating\": 3}]}";
//		PostRating rating = mapper.readValue(input, PostRating.class);
//		System.out.println(rating.getCustomerSecret());
//		System.out.println(rating.getCustomerId());
//		List<RatingItem> list2 = rating.getWrapper().getRatings();
//		for(RatingItem ite3m:list2){
//			System.out.println(ite3m.getProductId());
//		}

	}

}