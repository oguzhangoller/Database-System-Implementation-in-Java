import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Type {
	static String m_name = "null";
	static final int pageSize = 1024;
	static final int typeSize = 125;
	static int m_numberOfTypes = 0 ;
	static int m_numberOfFields = 0;
	static String m_fields[] = new String[10] ;
	public Type(){
		
	}
	public Type(String name,int numberOfFields, String fields[]){
		m_name = name;
		m_numberOfFields = numberOfFields;
		for(int i=0 ; i<numberOfFields ; i++){
			m_fields[i] = fields[i];
		}
	
		
	}
	
	public void createType(){
		try {
			File sys = new File("Syscat.txt");
			RandomAccessFile ra = new RandomAccessFile(sys,"rw");
			ra.seek(0);
			if(sys.length()==0){
				ra.writeInt(1);			//1 if this is last Page- 0 otherwise
				ra.writeInt(0);			//address of next page- currently null
				ra.writeInt(pageSize-24);
				ra.writeInt(1);			//page id
				ra.writeInt(m_numberOfTypes);			//number of types in this page 
				ra.writeInt(typeSize);
				byte b[] = new byte[1000];
				for(int k=0; k<1000;k++){
					b[k] = 0 ;
				}
				ra.write(b);
			}
			ra.seek(0);
			int isLast = ra.readInt();
			
			
			long address = 0;		//for initializing variable
		//	System.out.println("here");
			ra.seek(address+8);
			int spaceLeft = ra.readInt();
			ra.seek(4);
			while(isLast == 0){
				if(spaceLeft>typeSize)
					break;
				address = (ra.readInt()-1)*1024;
				ra.seek(address);
				isLast = ra.readInt();
				ra.seek(address+8);
				spaceLeft = ra.readInt();
			}
			ra.seek(address+8);
			spaceLeft = ra.readInt();
			if(spaceLeft<typeSize){
				ra.seek(address+12);
				int pageId = ra.readInt();
				ra.seek(address);
				ra.writeInt(0);
				ra.writeInt(pageId+1);
				ra.seek(address+1024);
				ra.writeInt(1);			//1 if this is last Page- 0 otherwise
				ra.writeInt(0);			//address of next page- currently null
				ra.writeInt(pageSize-24);
				ra.writeInt(pageId+1);			//page id
				ra.writeInt(m_numberOfTypes);			//number of types in this page 
				ra.writeInt(typeSize);
				byte b[] = new byte[1000];
				for(int k=0; k<1000;k++){
					b[k] = 0 ;
				}
				ra.write(b);
				address = address + 1024 ;
			}
			ra.seek(address+8);
			spaceLeft = ra.readInt();
			
			
			
			ra.seek(address+8);
			ra.writeInt(spaceLeft-typeSize);
			ra.readInt();		//for skipping page id
			m_numberOfTypes = ra.readInt();
			ra.seek(address+16);
			ra.writeInt(++m_numberOfTypes);
			ra.seek(address+24);
			for(int i=24 ; i<1024; i=i+125){
				ra.seek(address+i);
				
				if(ra.readInt()==0){
					ra.seek(address+i);
					ra.writeInt(1);
					ra.writeBytes(m_name);
					ra.seek(address+i+25);
					for(int j=0 ; j<m_numberOfFields ; j++){
						ra.seek(address+i+25+j*10);
						ra.writeBytes(m_fields[j]);
					}
					break;
					
				}
				
			}
			
			File f = new File(m_name+".txt");
			ra = new RandomAccessFile(f,"rw");
			ra.writeInt(1);			//1 if this is last Page- 0 otherwise
			ra.writeInt(0);			//address of next page- currently null
			ra.writeInt(pageSize-24);
			ra.writeInt(1);			//page id
			ra.writeInt(0);			//number of records in this page 
			ra.writeInt(typeSize);
			byte b[] = new byte[1000];
			for(int k=0; k<1000;k++){
				b[k] = 0 ;
			}
			ra.write(b);
			ra.seek(28);
			ra.writeInt(1);     	//id of first record to be written
					
			ra.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public static void deleteType(String typeName){
		try {
			File sys = new File("Syscat.txt");
			RandomAccessFile ra = new RandomAccessFile(sys,"rw");
			ra.seek(0);
			int isLast = ra.readInt();
			long address = 0 ;
			String name = "";
			boolean typeFound = false;
			while(true){
				ra.seek(address+12);
				System.out.println("Searching page no :" + ra.readInt());
				for(int i=24 ; i<1024 ; i=i+125){
					ra.seek(address+i);
					int isFull = ra.readInt();
					name = "";
					if(isFull==1){
						while(ra.readByte()!=0){
							ra.seek(ra.getFilePointer()-1);
							char b = (char) ra.readByte();
							name = name + b ;
						}
						
					
					if(name.equals(typeName)){
						typeFound = true;
						ra.seek(address+8);
						int spaceLeft = ra.readInt();
						ra.seek(address+8);
						ra.writeInt(spaceLeft+typeSize);
						ra.seek(address+16);
						int noOfRecords = ra.readInt();
						ra.seek(address+16);
						ra.writeInt(noOfRecords-1);
						ra.seek(address+i);
						ra.writeInt(0);
						int k = 120;
						while(k-->0){
							ra.writeByte(0);
						}
						File fi = new File(typeName + ".txt");
						if(fi.exists())
							fi.delete();
						System.out.println("Record has been succesfully deleted!");
						break;
					}
				}
				}
				if(isLast == 1){
					if(!typeFound)
						System.out.println("Type not found!");
					break;
				}
				
				address = address + 1024 ;
				ra.seek(address);
				isLast = ra.readInt();
				
			}
			
			
			}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public static String[] returnFieldsOfType(String typeName){
		String [] fields = new String[10];
		try {
			File sys = new File("Syscat.txt");
			RandomAccessFile ra = new RandomAccessFile(sys,"rw");
			ra.seek(0);
			int isLast = ra.readInt();
			long address = 0 ;
			String name = "";
			boolean typeFound = false;
			while(true){
				for(int i=24 ; i<1024 ; i=i+125){
					ra.seek(address+i);
					int isFull = ra.readInt();
					name = "";
					if(isFull==1){
						while(ra.readByte()!=0){
							ra.seek(ra.getFilePointer()-1);
							char b = (char) ra.readByte();
							name = name + b ;
						}
						
					
					if(name.equals(typeName)){
						typeFound = true;
						for(int t=0 ; t<10 ; t++){
						ra.seek(address+i+25+t*10);
						
						String fieldName = "";
						while(ra.readByte()!=0){
							ra.seek(ra.getFilePointer()-1);
							char b = (char) ra.readByte();
							fieldName = fieldName + b ;
							fields[t] = fieldName ;
						}
						}
						break;
					}
				}
				}
				if(isLast == 1){
					break;
				}
				if(typeFound)
					break;
				address = address + 1024 ;
				ra.seek(address);
				isLast = ra.readInt();
				
			}
			
			
			}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return fields;
	}
	public static void listAllTypes(){
		try {
			File sys = new File("Syscat.txt");
			RandomAccessFile ra = new RandomAccessFile(sys,"rw");
			if(sys.length() == 0){
				System.out.println("Currently there are no types!");
				ra.close();
				return ;
			}
			int isLast = ra.readInt();
			int pageCounter = 1 ;
			int typeCounter = 1 ;
			long address = 0;
			while(true){
				System.out.println("Reading page " + pageCounter);
				address=address + 28 ;
				for(int i=0 ; i<8 ; i++){
					ra.seek(address + 125*i-4);
					
					String typeName = "";
					if(ra.readInt() ==1){
						
						while(ra.readByte()!=0){
							ra.seek(ra.getFilePointer()-1);
							char b = (char) ra.readByte();
							typeName = typeName + b ;
						}
						
						
					
					if(typeName != ""){
						System.out.print("Type " + typeCounter++ + " : " + typeName + "   ");
						String [] fields = returnFieldsOfType(typeName);
						
						
						for(int k=0 ; k<fields.length ; k++){
							if(fields[k]!=null)
								System.out.print("field " + (k+1) + " : " + fields[k]+ " ");
						}
						System.out.println();
					}
				}
			}
				
				if(isLast == 1)
					break;
				else{
				pageCounter++;
				address = address + 996;
				
				ra.seek(address);
				isLast = ra.readInt();
				
				}
				
			}
			
			ra.close();
			System.out.println();
			
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
