package eu.dime.ps.controllers.trustengine.utils;

public class AdvisoryConstants {
		
	public final static double MIN_TRUST_VALUE = 0.01;
	public final static double MED_TRUST_VALUE = 0.5;
	public final static double MAX_TRUST_VALUE = 0.99;
	
	public final static double MIN_PRIVACY_VALUE = 0.01;
	public final static double MED_PRIVACY_VALUE = 0.5;
	public final static double MAX_PRIVACY_VALUE = 0.99;
	
	
	/* preset privacy values for levels */
	public final static double PV_ZERO = 0.0;
	public final static double PV_PUBLIC = 0.1; 
	public final static double PV_NEUTRAL = 0.3;
	public final static double PV_CONFIDENTIAL = 0.5;
	public final static double PV_PRIVATE = 0.7;
	public final static double PV_SECRET = 0.9; 
	public final static double PV_MAX = 1.0; 
	
	/* simple warnings defaults */
	
	public final static double PV_LOW = 0.0;
	public final static double PV_MED = 0.5;
	public final static double PV_HIGH = 1.0;
	
	public final static double TV_LOW = 0.0;
	public final static double TV_MED = 0.5;
	public final static double TV_HIGH = 1.0;
	
	// threshold value of number of shared files to start adaptive recommendations //
	public static final int TRUST_ADAPTION_TRIGGER = 2;	
	
	// ----  DEFAULTS ------
	public final static double DEFAULT_PRIVACY_LEVEL = PV_HIGH;
	public final static double DEFAULT_TRUST_VALUE = TV_MED;
	public static final int RESOURCE_WARNING_TRIGGER = 5;
	
}
