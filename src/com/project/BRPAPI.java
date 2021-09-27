package com.project;

import java.util.Base64;

import javax.ws.rs.GET; 
import javax.ws.rs.Path; 
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces; 
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONTokener;

@Path("/BRPPartInventoryAPI") 
public class BRPAPI { 
   @GET 
   @Path("/PostFileToBRP/{filePostingDetailObj}")  
   @Produces(MediaType.APPLICATION_XML) 
   public String fetchAndPostFileToBRP(@PathParam("filePostingDetailObj") String filePostingDetailObj) throws Exception{ 
	   try {
		   filePostingDetailObj = new String(Base64.getDecoder().decode(filePostingDetailObj));
		   JSONObject brpFileDetailJson = (JSONObject) new JSONTokener(filePostingDetailObj).nextValue();
		   
		   if(brpFileDetailJson.has("Orgname") && brpFileDetailJson.getString("Orgname") != null && Credentials.credentialsKeyToValueMap.containsKey((brpFileDetailJson.getString("Orgname") + "_USERNAME").toUpperCase())) {
			   BRPService serviceObj = new BRPService(brpFileDetailJson.getString("Orgname"));
			   serviceObj.fetchFileAndPostToBRP(brpFileDetailJson);
		   } else if(brpFileDetailJson.has("SFUsername") && brpFileDetailJson.has("SFPassword") && 
				   brpFileDetailJson.get("SFUsername") != null && !brpFileDetailJson.getString("SFUsername").isEmpty() && brpFileDetailJson.getString("SFUsername") != "null" && 
				   brpFileDetailJson.get("SFPassword") != null && !brpFileDetailJson.getString("SFPassword").isEmpty() && brpFileDetailJson.getString("SFPassword") != "null") {
			   System.out.println(brpFileDetailJson.getString("SFUsername"));
			   BRPService serviceObj = new BRPService(brpFileDetailJson.getString("SFUsername"), brpFileDetailJson.getString("SFPassword"));
			   serviceObj.fetchFileAndPostToBRP(brpFileDetailJson);
		   } else {
			   throw new Exception("Orgname is blank - " + filePostingDetailObj);
		   }
		   
		   return "Success";
	   } catch(Exception e) {
		   System.out.println(e.getMessage());
		   throw new Exception("Some error occured - " + e.getMessage());
	   }
   }
   
   /**@GET 
   @Path("/BRP/{param}") 
   @Produces(MediaType.APPLICATION_XML) 
   public void fetchAndPostFileToFTP(@PathParam("param") String param) throws Exception{ 
	   
	   BRPService serviceObj = new BRPService();
	   serviceObj.fetchFileAndPostToBRP_Test(new JSONObject());
	   System.out.println("param " + param);
   }**/
}
