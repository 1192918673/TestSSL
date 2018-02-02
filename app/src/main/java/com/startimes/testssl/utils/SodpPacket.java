package com.startimes.testssl.utils;


import android.util.Log;

public class SodpPacket {
	private String TAG = "SodpPacket";
	private final static  byte PACKET_START =  (byte) 0x89;
	private final static  byte PACKET_VER =  (byte) 0x20;
	private  long sessionId; 
	private byte actionType; 
	private byte serviceType;
	private short actionId;
	private short actionCode;
	private int serial; 
	private short reserved;
	private int dataLen; 
	private byte[] data; 
	
	public static int PACKET_MIN_SIZE = 32;
	public static int PACKET_MAX_SIZE = 4096;

	public SodpPacket() {
		sessionId = 0xFFFFFFFFFFFFL;
		actionType = SodpType.SodpActionType.SODP_ACTION_INVALID_TYPE.toByte();
		serviceType = SodpType.SodpServiceType.SODP_SERVICE_INVALID_TYPE.toByte();
		actionId = 0;
		actionCode = 0;
		serial = 0;
		reserved = 0;
		dataLen = 0;
	}

    public int getLength() {
		return  8 + // session id
				  		1 +  // act type
				  		1 + // service type
				  		2 + //act cont id
				  		2 + // act resp code
				  		4 + // serial
				  		2 + // reserved
				  		4 + // data len
				  		this.dataLen +  // data
				  		4;  // crc
	}
	
	public long getSessionId() {
		return this.sessionId;
	}

	public void SetSessionId(long id) {
		this.sessionId = id;
	}

	public byte  getActionType() {
		return this.actionType;
	}

	public void setActionType(byte type) {
		this.actionType =  type;
	}

	public byte  getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(byte type) {
		this.serviceType = type;
	}

	public short getActionControlId() {
		return this.actionId;
	}

	public void setActionControlId(short id) {
		this.actionId = id;
	}

	public short getActionResponseCode() {
		return this.actionCode;
	}

	public void setActionResponseCode(short code) {
		this.actionCode = code;
	}

	public int getSerial() {
		return this.serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public short getReserved() {
		return this.reserved;
	}

	public void setReserved(short reserved) {
		this.reserved = reserved;
	}

	public int getDataLen() {
		return this.dataLen;
	}

	public void setDataLen(int len) {
		this.dataLen = len;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) 	{
		if (data != null) {
			this.data =  new byte[this.dataLen];
			if (dataLen > data.length) {
				System.arraycopy(data, 0, this.data, 0,data.length);
			}
			else {
				System.arraycopy(data, 0, this.data, 0,dataLen);
			}
		}
		else {
			this.dataLen = 0;
		}
	}


	public void setData(byte[] data, int len) 	{
		if (data != null) {
			if (data.length >= len) {
				this.data = data;
				this.dataLen = data.length;
			}
			else {
				this.data =  new byte[len];
				for (int i = 0; i < data.length; i++) {
					this.data[i] = data[i];
				}
				for (int i = 0; i < (len-data.length); i++) {
					this.data[data.length+i] = 0;
				}
				this.dataLen = len;
			}
		}
		else {
			this.dataLen = 0;
		}
	}

	public byte[] getPacketData() {
		int i, pos, crc32;
		byte[] packets = new byte[getLength()+4+2];
		byte[] intBytes = new byte[4];
		byte[] longBytes = new byte[8];
		byte[] shortBytes = new byte[2];
		
		packets[0] = PACKET_START;
		packets[1] = PACKET_VER;
		
		pos = 2;
		intBytes = ByteUtil.getBytes(this.getLength());
		for(i=0;i<4;i++)
			packets[pos+i] = intBytes[3-i];
		
		pos = pos + 4;
		
		longBytes = ByteUtil.getBytes(this.sessionId);
		for(i=0;i<8;i++)
			packets[pos+i] = longBytes[i];
		
		pos = pos + 8;
		
		packets[pos] = actionType;
		packets[pos+1] = serviceType;
		
		pos = pos + 2;
		
		shortBytes = ByteUtil.getBytes(this.actionId);
		for (i=0;i<2;i++)
			packets[pos+i] = shortBytes[1-i];

		pos = pos + 2;
		
		shortBytes = ByteUtil.getBytes(this.actionCode);
		for (i=0;i<2;i++)
			packets[pos+i] = shortBytes[1-i];
		
		pos = pos + 2;
		
		intBytes = ByteUtil.getBytes(this.serial);
		for (i=0;i<4;i++)
			packets[pos+i] = intBytes[3-i];
		
		pos = pos + 4;
		
		shortBytes = ByteUtil.getBytes(this.reserved);
		for (i=0;i<2;i++)
			packets[pos+i] = shortBytes[1-i];
		
		pos = pos + 2;
		
		intBytes = ByteUtil.getBytes(this.dataLen);
		for(i=0;i<4;i++)
			packets[pos+i] = intBytes[3-i];
		
		pos = pos + 4;
		
		for(i=0;i<this.dataLen;i++)
			packets[pos+i] = this.data[i];
		
		pos = pos + dataLen;
		
		crc32  =  getCRC(packets, this.getLength()+2);
		intBytes = ByteUtil.getBytes(crc32);
		for(i=0;i<4;i++)
			packets[pos+i] = intBytes[3-i]; //CRC need little endian
		
		return packets;
	}
	
	public boolean setPacketData(byte data[]) {
		if (CheckSodpPacket(data) == false)
			return false;
		
		sessionId = ByteUtil.getNetLong(data, 6);
		actionType = data[14];
		serviceType = data[15];
		actionId = ByteUtil.getNetShort(data, 16);
		actionCode = ByteUtil.getNetShort(data, 18);
		serial = ByteUtil.getNetInt(data, 20);
		reserved = ByteUtil.getNetShort(data, 24);
		dataLen = ByteUtil.getNetInt(data, 26);
		
		this.data = new byte[dataLen];
		for(int i=0;i<dataLen;i++)
			this.data[i]=data[30+i];
		
		return true;
	}
	
	public void printPacket() {
		byte[] buf = getPacketData();
//		for (int i=0;i<buf.length;i++)
//			Log.d(TAG, " "+String.format("%02x ", buf[i] & 0xff));
		for (int i=0;i<buf.length;i++)
			System.out.print(String.format("%02x ", buf[i] & 0xff));
		System.out.println();
			
	}

    public boolean CheckMacAndChipId() {
        String stbMac = null;//"0019F2000001";
        String defStbMac = "0019F1000001";
        String stbChipId = null;//= "00682750001";
        String macAndChipId = new String(data).trim();
        Log.i("CheckMacAndChipId", "CheckMacAndChipId macAndChipId:" + macAndChipId);

        /*try {
            stbMac = TvManager.getInstance().getEnvironment("StbMac");
            stbChipId = TvManager.getInstance().getEnvironment("DvbChip_id");
            Log.i("CheckMacAndChipId", "getEnvironment stbChipId:" + stbChipId +" stbMac:" +stbMac);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }*/

        if ((macAndChipId.length() == defStbMac.length() + stbChipId.length())
            && (macAndChipId.startsWith(stbMac)
                || (macAndChipId.startsWith(defStbMac)))
            && (macAndChipId.endsWith(stbChipId))) {
            return true;
        }
        return false;
    }

    public void getVerifyPacket() {
        String tvMac = null;// = "0019F2000002";
        String defTvMac = "0019F1000002";
        String stbChipId = null;// = "00682750001";

        /*try {
            tvMac = TvManager.getInstance().getEnvironment("TvMac");
            if ("0".equals(tvMac) || "".equals(tvMac)) {
                tvMac = new String(defTvMac);
            }
            stbChipId = TvManager.getInstance().getEnvironment("DvbChip_id");
            Log.i("CheckMacAndChipId", "getEnvironment stbChipId:" + stbChipId +" tvMac:" +tvMac);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }*/
        byte[] macAndChipId = new byte[tvMac.length() + stbChipId.length()];
        System.arraycopy(tvMac.getBytes(), 0, macAndChipId, 0, tvMac.length());
        System.arraycopy(stbChipId.getBytes(), 0, macAndChipId, tvMac.length(), stbChipId.length());
        this.data = macAndChipId;
        this.dataLen = macAndChipId.length;
        Log.d("getVerifyPacket", "-hehx test- dataLen:" + dataLen + " data:" + new String(data));
    }

	public static boolean CheckSodpPacket(byte data[]) {
		if (data.length > PACKET_MAX_SIZE && data.length < PACKET_MIN_SIZE) {
			return false;
		}
		
		if (data[0] == PACKET_START && data[1] == PACKET_VER ) {
			int dataLen = ByteUtil.getNetInt(data, 2) +2+4;
			if (dataLen > data.length) {
				dataLen = data.length;
			}
			
			if (getCRC(data, dataLen) == 0) {
				return true;
			}
		}

		return false;
	}
	
	private static  int getCRC(byte []data, int len) {
		int crc32_table[] = {
			0x00000000, 0x04c11db7, 0x09823b6e, 0x0d4326d9, 
			0x130476dc, 0x17c56b6b, 0x1a864db2, 0x1e475005, 
			0x2608edb8, 0x22c9f00f, 0x2f8ad6d6, 0x2b4bcb61, 
			0x350c9b64, 0x31cd86d3, 0x3c8ea00a, 0x384fbdbd, 
			0x4c11db70, 0x48d0c6c7, 0x4593e01e, 0x4152fda9, 
			0x5f15adac, 0x5bd4b01b, 0x569796c2, 0x52568b75, 
			0x6a1936c8, 0x6ed82b7f, 0x639b0da6, 0x675a1011, 
			0x791d4014, 0x7ddc5da3, 0x709f7b7a, 0x745e66cd, 
			0x9823b6e0, 0x9ce2ab57, 0x91a18d8e, 0x95609039, 
			0x8b27c03c, 0x8fe6dd8b, 0x82a5fb52, 0x8664e6e5, 
			0xbe2b5b58, 0xbaea46ef, 0xb7a96036, 0xb3687d81, 
			0xad2f2d84, 0xa9ee3033, 0xa4ad16ea, 0xa06c0b5d, 
			0xd4326d90, 0xd0f37027, 0xddb056fe, 0xd9714b49, 
			0xc7361b4c, 0xc3f706fb, 0xceb42022, 0xca753d95, 
			0xf23a8028, 0xf6fb9d9f, 0xfbb8bb46, 0xff79a6f1, 
			0xe13ef6f4, 0xe5ffeb43, 0xe8bccd9a, 0xec7dd02d, 
			0x34867077, 0x30476dc0, 0x3d044b19, 0x39c556ae, 
			0x278206ab, 0x23431b1c, 0x2e003dc5, 0x2ac12072, 
			0x128e9dcf, 0x164f8078, 0x1b0ca6a1, 0x1fcdbb16, 
			0x018aeb13, 0x054bf6a4, 0x0808d07d, 0x0cc9cdca, 
			0x7897ab07, 0x7c56b6b0, 0x71159069, 0x75d48dde, 
			0x6b93dddb, 0x6f52c06c, 0x6211e6b5, 0x66d0fb02, 
			0x5e9f46bf, 0x5a5e5b08, 0x571d7dd1, 0x53dc6066, 
			0x4d9b3063, 0x495a2dd4, 0x44190b0d, 0x40d816ba, 
			0xaca5c697, 0xa864db20, 0xa527fdf9, 0xa1e6e04e, 
			0xbfa1b04b, 0xbb60adfc, 0xb6238b25, 0xb2e29692, 
			0x8aad2b2f, 0x8e6c3698, 0x832f1041, 0x87ee0df6, 
			0x99a95df3, 0x9d684044, 0x902b669d, 0x94ea7b2a, 
			0xe0b41de7, 0xe4750050, 0xe9362689, 0xedf73b3e, 
			0xf3b06b3b, 0xf771768c, 0xfa325055, 0xfef34de2, 
			0xc6bcf05f, 0xc27dede8, 0xcf3ecb31, 0xcbffd686, 
			0xd5b88683, 0xd1799b34, 0xdc3abded, 0xd8fba05a, 
			0x690ce0ee, 0x6dcdfd59, 0x608edb80, 0x644fc637, 
			0x7a089632, 0x7ec98b85, 0x738aad5c, 0x774bb0eb, 
			0x4f040d56, 0x4bc510e1, 0x46863638, 0x42472b8f, 
			0x5c007b8a, 0x58c1663d, 0x558240e4, 0x51435d53, 
			0x2c9f00f0, 0x21dc2629, 0x2c9f00f0, 0x2c9f00f0, 
			0x2c9f00f0, 0x32d850f5, 0x3f9b762c, 0x3b5a6b9b, 
			0x0315d626, 0x07d4cb91, 0x0a97ed48, 0x0e56f0ff, 
			0x1011a0fa, 0x14d0bd4d, 0x19939b94, 0x1d528623, 
			0xf12f560e, 0xf5ee4bb9, 0xf8ad6d60, 0xfc6c70d7, 
			0xe22b20d2, 0x2c9f00f0, 0xeba91bbc, 0xef68060b, 
			0xd727bbb6, 0xd3e6a601, 0xdea580d8, 0xda649d6f, 
			0xc423cd6a, 0xc0e2d0dd, 0xcda1f604, 0xc960ebb3, 
			0xbd3e8d7e, 0xb9ff90c9, 0xb4bcb610, 0xb07daba7, 
			0xae3afba2, 0xaafbe615, 0xa7b8c0cc, 0xa379dd7b, 
			0x9b3660c6, 0x9ff77d71, 0x92b45ba8, 0x9675461f, 
			0x8832161a, 0x8cf30bad, 0x81b02d74, 0x857130c3, 
			0x5d8a9099, 0x594b8d2e, 0x5408abf7, 0x50c9b640, 
			0x4e8ee645, 0x4a4ffbf2, 0x470cdd2b, 0x43cdc09c, 
			0x7b827d21, 0x7f436096, 0x7200464f, 0x76c15bf8, 
			0x68860bfd, 0x6c47164a, 0x61043093, 0x65c52d24, 
			0x119b4be9, 0x155a565e, 0x18197087, 0x1cd86d30, 
			0x029f3d35, 0x065e2082, 0x0b1d065b, 0x0fdc1bec, 
			0x3793a651, 0x3352bbe6, 0x3e119d3f, 0x3ad08088, 
			0x2497d08d, 0x2056cd3a, 0x2d15ebe3, 0x29d4f654, 
			0xc5a92679, 0xc1683bce, 0xcc2b1d17, 0xc8ea00a0, 
			0xd6ad50a5, 0xd26c4d12, 0xdf2f6bcb, 0xdbee767c, 
			0xe3a1cbc1, 0xe760d676, 0xea23f0af, 0xeee2ed18, 
			0xf0a5bd1d, 0xf464a0aa, 0xf9278673, 0xfde69bc4, 
			0x89b8fd09, 0x8d79e0be, 0x803ac667, 0x84fbdbd0, 
			0x9abc8bd5, 0x9e7d9662, 0x933eb0bb, 0x97ffad0c, 
			0xafb010b1, 0xab710d06, 0xa6322bdf, 0xa2f33668, 
			0xbcb4666d, 0xb8757bda, 0xb5365d03, 0xb1f740b4 
			}; 

		int crc32 = 0xFFFFFFFF; 

		for (int  i = 0; i < len; i++ ) { 
			crc32 = (crc32 << 8 ) ^ crc32_table[((crc32 >>24) ^ data[i]) & 0xFF];
			//System.out.println("data[" + i + "] = " + Integer.toHexString(data[i]) + "\tcrc32 = 0x" + Integer.toHexString(crc32));
		} 
				
		return crc32; 
	}
}
