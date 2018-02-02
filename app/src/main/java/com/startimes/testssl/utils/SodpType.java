package com.startimes.testssl.utils;

public final class SodpType {
	// SODP Action Type
	public  enum SodpActionType {
		SODP_ACTION_CMD_REQUEST_TYPE (1),
		SODP_ACTION_CMD_RESPONSE_TYPE (2),
		SODP_ACTION_CMD_RESPONSE_RESAULT_TYPE (3),
		SODP_ACTION_EVENT_TYPE(4),
		SODP_ACTION_HEARTBEAT_TYPE(5),
		SODP_ACTION_DATA_TYPE (6),
		SODP_ACTION_TS_STREAM_TYPE (7),
		SODP_ACTION_LOGIN_REQUEST_TYPE (8),
		SODP_ACTION_LOGIN_RESPONSE_TYPE (9),
		SODP_ACTION_LOGOUT_REQUEST_TYPE (10),
		SODP_ACTION_LOGOUT_RESPONSE_TYPE (11),
		SODP_ACTION_INVALID_TYPE (0xff);
		
		private  int actionType;
		private SodpActionType(int type) {
			actionType = type;
		}
		
		public static SodpActionType valueOf(int type) {
			switch (type) {
			case 1: return SODP_ACTION_CMD_REQUEST_TYPE;
			case 2:	return SODP_ACTION_CMD_RESPONSE_TYPE;
			case 3:	return SODP_ACTION_CMD_RESPONSE_RESAULT_TYPE;
			case 4:	return SODP_ACTION_EVENT_TYPE;
			case 5:	return SODP_ACTION_HEARTBEAT_TYPE;
			case 6:	return SODP_ACTION_DATA_TYPE;
			case 7:	return SODP_ACTION_TS_STREAM_TYPE;
			case 8: return SODP_ACTION_LOGIN_REQUEST_TYPE;
			case 9: return SODP_ACTION_LOGIN_RESPONSE_TYPE;
			case 10: return SODP_ACTION_LOGOUT_REQUEST_TYPE;
			case 11: return SODP_ACTION_LOGOUT_RESPONSE_TYPE;
			case 0xff: return SODP_ACTION_INVALID_TYPE;
			default: return null;
			}
		}

		public  byte toByte() {
			return (byte) actionType;
		}
		
		public String toString() {
			return String.valueOf(actionType);
		}
	}
	//SODP Server Type
	public enum SodpServiceType {
		SODP_SERVICE_SYSTEM_TYPE (0),
		SODP_SERVICE_LIVEVIDEO_TYPE (1),
		SODP_SERVICE_FILEVIDEO_TYPE (2),
		SODP_SERVICE_IR_TYPE (3),
		SODP_SERVICE_UPG_TYPE (4),
		SODP_SERVICE_DVB_SETTING_TYPE(5),
		SODP_SERVICE_DVB_SEARCH_TYPE(6),
		SODP_SERVICE_DVB_AVPLAY_TYPE(7),
		SODP_SERVICE_DVB_EPG_TYPE(8),
		SODP_SERVICE_DVB_EVT_TYPE(9),
		SODP_SERVICE_DVB_CA_TYPE(10),
		SODP_SERVICE_DVB_CHANNEL_LIST_TYPE(11),
		SODP_SERVICE_DVB_COMMON_TYPE(12),
		SODP_SERVICE_INVALID_TYPE (0xff);

		private int serviceType;
		private SodpServiceType(int type){
			serviceType = type;
		}

		public static SodpServiceType valueOf(int type) {
			switch (type) {
			case 0: return SODP_SERVICE_SYSTEM_TYPE;
			case 1: return SODP_SERVICE_LIVEVIDEO_TYPE;
			case 2:	return SODP_SERVICE_FILEVIDEO_TYPE;
			case 3:	return SODP_SERVICE_IR_TYPE;
			case 4:	return SODP_SERVICE_UPG_TYPE;
			case 5: return SODP_SERVICE_DVB_SETTING_TYPE;
			case 6: return SODP_SERVICE_DVB_SEARCH_TYPE;
			case 7: return SODP_SERVICE_DVB_AVPLAY_TYPE;
			case 8: return SODP_SERVICE_DVB_EPG_TYPE;
			case 9: return SODP_SERVICE_DVB_EVT_TYPE;
			case 10: return SODP_SERVICE_DVB_CA_TYPE;
			case 11: return SODP_SERVICE_DVB_CHANNEL_LIST_TYPE;
			case 12: return SODP_SERVICE_DVB_COMMON_TYPE;
			case 0xff: return SODP_SERVICE_INVALID_TYPE;
			default: return null;
			}
		}

		public  byte toByte() {
			return (byte) serviceType;
		}
		
		public String toString(){
			return String.valueOf(serviceType);
		}
	}
}
