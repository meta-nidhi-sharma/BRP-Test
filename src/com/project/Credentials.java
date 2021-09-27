package com.project;

import java.util.HashMap;
import java.util.Map;

//keys(orgName) should be in upper case (format : <orgName>_USERNAME OR <orgName>_PASSWORD)

public class Credentials {
	
	@SuppressWarnings("serial")
	public static Map<String, String> credentialsKeyToValueMap = new HashMap<String, String>() {
		{
			put("GOLDEN_USERNAME", "bp.admin@bannerrecreation.com");
			put("GOLDEN_PASSWORD", "Welcome@1234567");
			
			put("VERNON_USERNAME", "bp.admin@bannerrecreationvernon.com");
			put("VERNON_PASSWORD", "Welcome@123");
			
			put("KELOWNA_USERNAME", "bp.admin@bannerrecreationkelowna.com");
			put("KELOWNA_PASSWORD", "Welcome@1234");
			
			put("FSR_USERNAME", "bp.admin@fullspeedrentals.com");
			put("FSR_PASSWORD", "Welcome@1234");
			
			put("RHD_USERNAME", "bp.admin@rockyhd.com");
			put("RHD_PASSWORD", "Welcome@123");
		 
			put("HHD_USERNAME", "bp.admin@horshamharley.com");
			put("HHD_PASSWORD", "Welcome@12345#");
			
			put("RTR_USERNAME", "bp.admin@rtrperformance.com");
			put("RTR_PASSWORD", "Welcome@12345#");
			
			put("STREAMRV_USERNAME", "bp.admin@streamrv.com");
			put("STREAMRV_PASSWORD", "Welcome@123456");
			
			put("LETHBRIDGE_USERNAME", "bp.admin@bannerrecreationlethbridge.com");
			put("LETHBRIDGE_PASSWORD", "Welcome@123*");
			
			put("WALLACETRAILERS_USERNAME", "bp.admin@wallacetrailers.com");
			put("WALLACETRAILERS_PASSWORD", "Welcome@1234");
			
			put("LINTLAWSEVICES_USERNAME", "bp.admin@lintlawservice.com");
			put("LINTLAWSEVICES_PASSWORD", "Welcome@123#");
			
			put("BUTTE_USERNAME", "bp.admin@buttemurdochsranch.com");
			put("BUTTE_PASSWORD", "Welcome@123");
			
			put("CAMROSEMOTORSPORTS_USERNAME", "bp.admin@camrosemotorsports.com");
			put("CAMROSEMOTORSPORTS_PASSWORD", "Welcome@123#");
			
			put("WetaskiwinMotorsports_USERNAME", "bp.admin@wetaskiwinmotorsports.com");
			put("WetaskiwinMotorsports_PASSWORD", "Welcome@123#");
			
			put("MISSOULA_USERNAME", "bp.admin@murdochsranch.com");
			put("MISSOULA_PASSWORD", "Welcome@123");
			
			put("DEV04_USERNAME", "nidhi.sharma@bp.dev04.com");
			put("DEV04_PASSWORD", "Welcome@1234");
			
			put("DEV01_USERNAME", "nidhi.sharma@dev1.com");
			put("DEV01_PASSWORD", "Welcome@1234");
			
			put("DS3_USERNAME", "hitesh.gupta@bptest.com");
			put("DS3_PASSWORD", "Welcome@1234");
			
			put("QA_USERNAME", "bp.admin@performance.com");
			put("QA_PASSWORD", "Welcome@1234");
			
			put("AUSUAT_USERNAME", "bp.admin@bpaustralia.com");
			put("AUSUAT_PASSWORD", "Welcome@123");
			
			put("USAUAT_USERNAME", "bp.admin@uat.com.usa");
			put("USAUAT_PASSWORD", "Welcome@123");
			
			put("INSTALLCP_USERNAME", "qaadmin.installpackage@bp.com");
			put("INSTALLCP_PASSWORD", "Welcome@1234");
			
			put("PRAIRIEWINDRECREATIONALLTD_USERNAME", "bp.admin@prairiewindrecreational.com");
			put("PRAIRIEWINDRECREATIONALLTD_PASSWORD", "Welcome@1234");
		}
	}; 
}
