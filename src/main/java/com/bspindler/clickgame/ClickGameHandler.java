package com.bspindler.clickgame;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author bspindler
 * ref: http://docs.aws.amazon.com/lambda/latest/dg/java-gs.html
 */
public class ClickGameHandler {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	// default constructor for Jackson
	public ClickGameHandler() {
	}
	
	/**
	 * Consumes a click (writes to s3), returns a response
	 * @param click
	 * @param context
	 * @return
	 * @throws IOException 
	 */
	public Response handleClick(Click click, Context context) throws IOException {
		
		// grab logger
		LambdaLogger logger = context.getLogger();
		logger.log("starting handleClick()\n" ) ;
		
		// we'll wrap this in a Response
		PutObjectResult putObjectResult = null;
		BufferedInputStream bis = null;
		
		try {
			logger.log("instanstiating s3Client...\n"); 
			AmazonS3Client s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
			
			logger.log("s3Client instantiated...\n");
			if(s3Client.doesBucketExist("bspindler-example/clickgame")) {
				logger.log("bucket does exists! \n");
			}
			else {
				logger.log("bucket does not exists! \n");
			}
			// generate random id; this helps spread the data around s3 properly
			logger.log("generating id...\n"); 
			String id = UUID.randomUUID().toString();
			logger.log("id generated: " + id + "...\n");
			
			// set id on Click
			click.setId(id);
			
			// build up the data
			Map<String, Object> data = (null == click.getData()) ? new HashMap<String, Object>() : click.getData();
			if(context!=null) {
				data.put("awsRequestId", context.getAwsRequestId());
			}
			click.setData(data);;
			
			logger.log("generating bytes for inputstream\n");
			logger.log(objectMapper.writeValueAsString(click));
			byte[] valueAsBytes = objectMapper.writeValueAsBytes(click);
			logger.log("generated " + valueAsBytes.length + "...\n");
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(valueAsBytes);
			ObjectMetadata metaData = new ObjectMetadata();
			metaData.setContentLength(valueAsBytes.length);
			metaData.setContentType("application/json");
			
			// put it in S3
			logger.log("putting object...\n");
			
			bis = new BufferedInputStream(byteArrayInputStream, 
										com.amazonaws.RequestClientOptions.DEFAULT_STREAM_BUFFER_SIZE);
			
				
			putObjectResult = s3Client.putObject("bspindler-example/clickgame", id, bis, metaData);
			logger.log("object put: "); // eTag: " + putObjectResult.getETag() + "\n");
			
		
		
		} catch (AmazonServiceException ase) {
	        logger.log("Caught an AmazonServiceException, which " +
	                "means your request made it " +
	                "to Amazon S3, but was rejected with an error response" +
	                " for some reason.");
	        logger.log("Error Message:    " + ase.getMessage());
	        logger.log("HTTP Status Code: " + ase.getStatusCode());
	        logger.log("AWS Error Code:   " + ase.getErrorCode());
	        logger.log("Error Type:       " + ase.getErrorType());
	        logger.log("Request ID:       " + ase.getRequestId());
	    } catch (AmazonClientException ace) {
	        logger.log("Caught an AmazonClientException, which " +
	                "means the client encountered " +
	                "an internal error while trying to " +
	                "communicate with S3, " +
	                "such as not being able to access the network.");
	        logger.log("Error Message: " + ace.getMessage());
	        logger.log(ace.getStackTrace().toString());
	    }
		finally {
			if(bis != null) {
				bis.close();
			}
		}
		// wrap response
		return new Response(objectMapper.writeValueAsString(putObjectResult));
	}
	
	public static void main(String[] args) throws IOException {
		// Start console based execution of Lambda function
		//ClickGameHandler clickGameHandler = new ClickGameHandler();
		//Click click = new Click();
		//click.setSource("local:main");;
		
		//clickGameHandler.handleClick(click, null, null);
	}

}


