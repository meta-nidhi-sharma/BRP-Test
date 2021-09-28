package com.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class BRPService implements Serializable {  
	private static final String GET_ATTACHMENT_BODY_PATH = "GetAttachmentBody";
	private static final String POST_RESPONSE_SF_PATH = "BRPResponse";	
	private BRPFileDetailWrapper brpFileDetailObj;
	
	private static final Header PRETTY_PRINT_HEADER = new BasicHeader("X-PrettyPrint", "1");
	private Header OAUTH_HEADER;
	private String LOGIN_INSTANCE_URL;
	private String LOGIN_ACCESS_TOKEN;
	
	private BRPResponseWrapper brpResponseObj;
	
	private String BP_USERNAME;// = "nidhi.sharma@bp.dev04.com";
	private String BP_PASSWORD;// = "Welcome@1234";
	
	public BRPService() throws Exception {
	}
	public BRPService(String orgname) throws Exception {
		this.init(orgname);
	}
	
	public void init(String orgname) throws Exception {
		this.setUsernameAndPassword(orgname);
		this.init(BP_USERNAME, BP_PASSWORD);
	}
	
	public BRPService(String sfUsername, String sfPassword) throws Exception {
		this.init(sfUsername, sfPassword);
	}

	public void init(String sfUsername, String sfPassword) throws Exception {
		BP_USERNAME = sfUsername.replace("#","%23");
		BP_PASSWORD = sfPassword.replace("#","%23");	
		
		System.out.println("BP_USERNAME :" + BP_USERNAME + " and BP_PASSWORD : " + BP_PASSWORD);
		Map<String, String> authorizationDataMap = new AuthorizationUtil().getAuthorizationToken(BP_USERNAME, BP_PASSWORD);
		LOGIN_ACCESS_TOKEN = authorizationDataMap.get("LOGIN_ACCESS_TOKEN");
		LOGIN_INSTANCE_URL = authorizationDataMap.get("LOGIN_INSTANCE_URL");
		OAUTH_HEADER = new BasicHeader("Authorization", "OAuth " + LOGIN_ACCESS_TOKEN);
	}
	
	private void setUsernameAndPassword(String orgname) throws Exception {
		if(orgname != null) {
			String usernameKey = (orgname + "_USERNAME").toUpperCase();
			String passwordKey = (orgname + "_PASSWORD").toUpperCase();
			
			System.out.println("usernameKey " + usernameKey + " passwordKey " + passwordKey);
			if(Credentials.credentialsKeyToValueMap.containsKey(usernameKey)) {
				BP_USERNAME = Credentials.credentialsKeyToValueMap.get(usernameKey);
				BP_PASSWORD = (Credentials.credentialsKeyToValueMap.get(passwordKey)).replace("#","%23");				
			} else {
				throw new Exception("Credentials missing for orgname " + orgname);
			}
		} else {
			throw new Exception("Orgname cannot be blank");
		}
	}
	
	public void fetchFileAndPostToBRP(JSONObject brpFileDetailJson) throws Exception{
		brpResponseObj = new BRPResponseWrapper();
		try {
			brpResponseObj.RequestParamStr = brpFileDetailJson.toString();
			System.out.println("Requested parameters - " + brpResponseObj.RequestParamStr);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			brpFileDetailObj = (BRPFileDetailWrapper)gson.fromJson(brpFileDetailJson.toString(), BRPFileDetailWrapper.class);
			brpFileDetailObj.Namespace = brpFileDetailObj.Namespace != null ? brpFileDetailObj.Namespace + '/': "";
			brpResponseObj.FileName = brpFileDetailObj.FileName;
					
			deleteFile();
			createRequestPayload(brpFileDetailObj.AttachmentIdList);
			System.out.println("Payload created");
			String requestbody = new String(Files.readAllBytes(Paths.get(brpFileDetailObj.FileName)));
			doPostToBRP(requestbody);
		} catch (Exception e) {
			brpResponseObj.ErrorResponse = e.getMessage();
			brpResponseObj.IsJavaSuccess = false;
			System.out.println("Exception " + e.getMessage());
		}
		System.out.println("IsJavaSuccess " +brpResponseObj.IsJavaSuccess);
		System.out.println("ErrorResponse " +brpResponseObj.ErrorResponse);
		deleteFile();
		
		
		JSONObject requestJson = new JSONObject();
		requestJson.put("postResponse", brpResponseObj.toJSON());
		doPostToSF(brpFileDetailObj.Namespace + POST_RESPONSE_SF_PATH, requestJson.toString());
	}
	
	private void deleteFile() {
		String folderName = "."; // Give your folderName
		File[] listFiles = new File(folderName).listFiles();
		
		for (int i = 0; i < listFiles.length; i++) {
		    if (listFiles[i].isFile()) {
		        String fileName = listFiles[i].getName();
		        if (fileName.startsWith(brpFileDetailObj.FileName)) {
		            listFiles[i].delete();
		            System.out.println("Deleted " +fileName);
		        }
		    }
		}
	}
	
	public String compress(String str) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(str.getBytes("UTF-8"));
	    os.close();
	    gos.close();
	    return Base64.getEncoder().encodeToString(os.toByteArray());
	  }

	
	public void createRequestPayload(List<String> attchIdList) throws Exception {
		System.out.println("creating header");
		createFileHeader();
		if(attchIdList != null && attchIdList.size() > 0) {
			System.out.println("Fetching files with attchment ids..." + attchIdList);
			String attachmentData = "";
			for(String attchRecId : attchIdList) {
				attachmentData = doGetToSF(brpFileDetailObj.Namespace + GET_ATTACHMENT_BODY_PATH + "/" + attchRecId);
				writeUsingFileWriter(attachmentData);
			}
		}
		System.out.println("creating footer");
		//End tags in xml file
		createFileFooter();
	}
	
	public String doGetToSF(String requestPath) throws Exception {
		String responseStr = null;
		try {
			String requestURI = LOGIN_INSTANCE_URL + "/services/apexrest/" + requestPath;
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(requestURI);
			
			System.out.println("Request Uri " + requestURI);
			
			httpGet.addHeader(OAUTH_HEADER);
			httpGet.addHeader(PRETTY_PRINT_HEADER);

			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseStr = EntityUtils.toString(response.getEntity()).replaceAll("\"", "");
				responseStr = new String(Base64.getDecoder().decode(responseStr)); 
			} else {
				throw new Exception(requestURI + " status : " + statusCode);
			}
		} catch (Exception e) {
			throw e;
		}
		return responseStr;
	}
	
	private void createFileHeader() {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<star:ProcessPartsInventory releaseID=\"5.8.1\" languageCode=\"en-US\" xsi:schemaLocation=\"http://www.starstandard.org/STAR/5 file:/C:/STAR/5.8.1/BODs/Standalone/ProcessPartsInventory.xsd\" xmlns:star=\"http://www.starstandard.org/STAR/5\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<star:ApplicationArea>" +
				"<star:Sender>"+
				"<star:CreatorNameCode>Blackpurl</star:CreatorNameCode>"+
				"<star:SenderNameCode>BP</star:SenderNameCode>"+
				"<star:DealerNumberID>" + brpFileDetailObj.DealerId + "</star:DealerNumberID>"+
				"<star:ServiceID>ProcessPartsInventory</star:ServiceID>"+
				"</star:Sender><star:CreationDateTime>" + brpFileDetailObj.CreationDateTime + "</star:CreationDateTime>"+
				"<star:BODID>" + brpFileDetailObj.BODId + "</star:BODID>"+
				"<star:Destination><star:DestinationNameCode>BD</star:DestinationNameCode></star:Destination>"+
				"</star:ApplicationArea>"+
				"<star:ProcessPartsInventoryDataArea><star:Process /><star:PartsInventory><star:PartsInventoryHeader>"+
				"<star:DocumentDateTime>" + brpFileDetailObj.CreationDateTime + "</star:DocumentDateTime>"+
				"<star:DocumentIdentificationGroup>"+
				"<star:DocumentIdentification><star:DocumentID>ProcessPartsInventory</star:DocumentID>"+
				"<star:AgencyRoleCode>Dealer</star:AgencyRoleCode></star:DocumentIdentification></star:DocumentIdentificationGroup>"+
				"<star:InventoryTypeCode>Full</star:InventoryTypeCode></star:PartsInventoryHeader>";
		writeUsingFileWriter(header);
    }
	private void createFileFooter() {
		String footer = "</star:PartsInventory></star:ProcessPartsInventoryDataArea></star:ProcessPartsInventory>";
		writeUsingFileWriter(footer);
    }
	
	private void writeUsingFileWriter(String data) {
        File file = new File(brpFileDetailObj.FileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(data);
        } catch (IOException e) {
        	brpResponseObj.ErrorResponse = e.getMessage();
			brpResponseObj.IsJavaSuccess = false;
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void doPostToBRP(String requestBody) {
		String responseStr = null;
		try {
			// Construct the objects needed for the request
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(brpFileDetailObj.Endpoint);
			httpPost.addHeader("Accept", "application/xml");
			httpPost.addHeader("Accept-Language", "en-US");
			httpPost.addHeader("Accept-Encoding", "gzip");
			httpPost.addHeader("Accept-Charset", "UTF-8");
			httpPost.addHeader("Content-Type", "application/xml");
			Client api = new Client();
			api.signRequest(httpPost, brpFileDetailObj.Username, brpFileDetailObj.Password);
			
			StringEntity body = new StringEntity(requestBody);
			requestBody = null;
			httpPost.setEntity(body);
			body = null;
			HttpResponse response = httpClient.execute(httpPost);

			int statusCode = response.getStatusLine().getStatusCode();
			responseStr = EntityUtils.toString(response.getEntity());
			throw new Exception("Failed to deliver synchronous message: senderChannel 'fb297c8c8bf63dd0abef39e19d7627fa': Catching exception calling messaging system: XIServer:CX_ID_PLSRV: (Software version: 1.0.25)");
			//System.out.print("Status Code  " + statusCode);
			//brpResponseObj.StatusCode = statusCode;
			//brpResponseObj.ResponseXMLString = responseStr;
			//brpResponseObj.IsJavaSuccess = true;
		} catch (Exception e) {
			brpResponseObj.ErrorResponse = e.getMessage();
			brpResponseObj.IsJavaSuccess = false;
			System.out.println("Exception while posting to BRP " + e.getMessage());
		}
	}
	
	public String doPostToSF(String requestPath, final String requestBody) throws Exception {
		String responseStr = null;
		try {
			final String requestUri = LOGIN_INSTANCE_URL + "/services/apexrest/" + requestPath;
			// Construct the objects needed for the request
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(requestUri);
			httpPost.addHeader(OAUTH_HEADER);
			httpPost.addHeader(PRETTY_PRINT_HEADER);

			System.out.print("doPostToSF "+ requestBody);
			// The message we are going to post
			StringEntity body = new StringEntity(requestBody);
			body.setContentType("application/json");
			httpPost.setEntity(body);

			// Make the request
			HttpResponse response = httpClient.execute(httpPost);

			// Process the results
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("StatusCode for posting BRP reposne to SF- " + statusCode);
			if (statusCode == 200) {
				responseStr = EntityUtils.toString(response.getEntity());
			} else {
				throw new Exception("BRP Service response status : " + statusCode);
			}
		} catch (Exception e) {
			System.out.println("Exception while posting BRP reposne to SF " + e.getMessage());
			throw e;
		}
		return responseStr;
	}

	public class BRPFileDetailWrapper implements java.io.Serializable  {
		public String Orgname;
		public String Endpoint;
		public String Username;
		public String Password;
		public String FileHeader;
		public String FileName;
		public String FileFooter;
		public List<String> AttachmentIdList;
		public String Namespace;
		public String CreationDateTime;
		public String BODId;
		public String DealerId;
		public String SFUsername;
		public String SFPassword;
		public String ExportType;
		public String DateTimeValue;
		public Boolean IsRetryFilePosting;
	}
	
	public class PostResponseWrapper implements java.io.Serializable  {
		public BRPResponseWrapper BRPWrapperObj;
	}
	public class BRPResponseWrapper implements java.io.Serializable  {
		public String ErrorResponse;
		public String RequestParamStr; 
		public Boolean IsJavaSuccess;
		public Integer StatusCode;
		public String FileName;
		public String ResponseXMLString;
		
		public JSONObject toJSON() throws JSONException{
	        JSONObject jsonObj = new JSONObject();
	        jsonObj.put("ErrorResponse", ErrorResponse);
	        jsonObj.put("RequestParamStr", RequestParamStr);
	        jsonObj.put("IsJavaSuccess", IsJavaSuccess);
	        jsonObj.put("StatusCode", StatusCode);
	        jsonObj.put("FileName", FileName);
	        jsonObj.put("ResponseXMLString", ResponseXMLString);
	        return jsonObj;
	    }
	}
} 
