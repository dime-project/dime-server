package eu.dime.commons.notifications.user;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * 
 * {
       //[..]   //general fields as specified in _general user notification body_
       "unType":"merge_recommendation",
       "unEntry":{
           "sourceId":"<personId1>",
           "sourceName":"<personName1>",
           "targetId":"<personId2>",
           "targetName":"<personName2>",
           "similarity":<number>,
           "status":"accepted/dismissed/pending"
       }
   }
 * 
 * @author mplanguma
 *
 */
public class UNMergeRecommendation extends UserNotificationEntry {
	
	public static final String STATUS_ACCEPTED = "accepted";
	public static final String STATUS_DISMISSED = "dismissed";
	public static final String STATUS_PENDING = "pending";
	
	public UNMergeRecommendation(){
		this.put(UNTYPE_LABEL, DimeInternalNotification.UN_TYPE_MERGE_RECOMENDATION);
	}

	public UNMergeRecommendation(String sourceId, String sourceName, String targetId, String targetName, Double similarity, String status) {
		this();
		this.setSourceId(sourceId);
		this.setSourceName(sourceName);
		this.setTargetId(targetId);
		this.setTargetName(targetName);
		this.setSimilarity(similarity);
		this.setStatus(status);
	}

	public String getSourceId() {
		return (String) this.get("sourceId");
	}

	public void setSourceId(String sourceId) {
		this.put("sourceId", sourceId);
	}

	public String getSourceName() {
		return (String) this.get("sourceName");
	}

	public void setSourceName(String sourceName) {
		this.put("sourceName", sourceName);
	}

	public String getTargetId() {
		return (String) this.get("targetId");
	}
	
	public void setTargetId(String targetId) {
		this.put("targetId", targetId);
	}

	public String getTargetName() {
		return (String) this.get("targetName");
	}
	
	public void setTargetName(String targetName) {
		this.put("targetName", targetName);
	}
	
	public Double getSimilarity() {
		return (Double) this.get("similarity");
	}
	
	public void setSimilarity(Double similarity) {
		this.put("similarity", similarity);
	}
	
	public String getStatus() {
		return (String) this.get("status");
	}
	
	public void setStatus(String status) {
		this.put("status", status);
	}

}
