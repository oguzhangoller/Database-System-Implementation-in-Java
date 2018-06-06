import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Record {
static int recordSize = 48;
static int pageSize = 1024;
String m_typeName;
int isFull = 0;
int m_fields[] = new int[10];

	public Record(String typeName,int fields[]){
		//m_id = id ;
		m_typeName = typeName ;
		for(int i=0 ; i<fields.length ; i++){
			m_fields[i] = fields[i];
		}
		
	}
	
	public void insertRecord(){
		try {
			File f = new File(m_typeName + ".txt");
			RandomAccessFile ra = new RandomAccessFile(f, "rw");
			long address = 0;
			ra.seek(0);
			int spaceLeft = 1000;
			int pageId = 1;
			int isLast = ra.readInt();
			while(isLast == 0){
				ra.seek(address+8);
				spaceLeft = ra.readInt();
				System.out.println("Accessing page : " + pageId);
				if(spaceLeft>recordSize){
					break;
				}
				pageId = ra.readInt();
				address = address + 1024;
				ra.seek(address);
				isLast = ra.readInt();
			}
			
			ra.seek(address+8);
			spaceLeft = ra.readInt();
			if(spaceLeft < recordSize){
				ra.seek(address);
				ra.writeInt(0);
				ra.writeInt(pageId+1);
				address = address + 1024;
				ra.seek(address);
				ra.writeInt(1);			//1 if this is last Page- 0 otherwise
				ra.writeInt(0);			//address of next page- currently null
				ra.writeInt(pageSize-24);
				ra.writeInt(pageId+1);			//page id
				ra.writeInt(0);			//number of records in this page 
				ra.writeInt(recordSize);
				byte b[] = new byte[1000];
				for(int k=0; k<1000;k++){
					b[k] = 0 ;
				}
				ra.write(b);
				ra.seek(address+28);
				ra.writeInt((pageId)*20+1);			//id of record at start of each page
			}
			ra.seek(address+8);
			spaceLeft = ra.readInt();
			spaceLeft = spaceLeft - recordSize;
			ra.seek(address+8);
			ra.writeInt(spaceLeft);
			ra.seek(address+16);
			int noOfRecords = ra.readInt();
			ra.seek(ra.getFilePointer()-4);
			ra.writeInt(++noOfRecords);
			address = address+24 ;
			System.out.println("Writing to page : " + pageId);
			ra.seek(address+4);
			int recordId = ra.readInt();
			for(int i=0 ; i<960 ; i=i+48){
				ra.seek(address+i);
				int isFull = ra.readInt();
				if(isFull == 0){
					ra.seek(address+i);
					ra.writeInt(1);
					if(ra.readInt()==0){
						ra.seek(ra.getFilePointer()-4);
						ra.writeInt(recordId+1);
					}
					for(int j=0 ; j<m_fields.length ; j++){
						ra.writeInt(m_fields[j]);
					}
					System.out.println("Record succesfully inserted!");
					break;
				}
				recordId = ra.readInt();
				
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void deleteRecord(String typeName, int key){
		long address = searchRecord(typeName,key);
		if(address ==0)
			return;
		try {
			File f = new File(typeName + ".txt");
			RandomAccessFile ra = new RandomAccessFile(f, "rw");
			ra.seek(address);
			ra.writeInt(0);
			ra.seek(address+8);
			byte b[] = new byte[40];
			for(int k=0; k<40;k++){
				b[k] = 0 ;
			}
			ra.write(b);
			System.out.println("Record succesfully deleted!");
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public static void updateRecord(String typeName, int key){
		long address = searchRecord(typeName,key);
		if(address == 0)
			return ;
		try {
			File f = new File(typeName + ".txt");
			RandomAccessFile ra = new RandomAccessFile(f, "rw");
			String fields[] = Type.returnFieldsOfType(typeName);
			for(int i=0 ; i<fields.length ; i++){
				if(fields[i]!=null){
					System.out.println("Enter new value of " + fields[i] + " to be updated ");
					Scanner sc = new Scanner(System.in);
					int value = Integer.parseInt(sc.next());
					ra.seek(address+8+4*i);
					ra.writeInt(value);
				}
				
				
			}
			searchRecord(typeName,key);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Record updated!");
	}
	/*
	 * returns address of that record
	 */
	public static long searchRecord(String typeName , int key){
		long address = 0 ;
		try {
			File f = new File(typeName + ".txt");
			RandomAccessFile ra = new RandomAccessFile(f, "rw");
			String fields[] = Type.returnFieldsOfType(typeName);
			int pageId = key/20;		//since there are 20 records in each page, this value equals pageId-1
			int slot = key%20;
			if(slot==0){
				slot=20 ;
				pageId--;
			}
			System.out.println("Accessing page " + (pageId+1));
			ra.seek(1024*pageId+24+48*(slot-1));
			address = ra.getFilePointer();
			if(ra.readInt()==0){
				System.out.println("This record is empty!");
				return 0;
			}
			ra.seek(ra.getFilePointer()+4);
			
			System.out.print("Record " + key + " : ");
			for(int j=0 ; j<fields.length ; j++){
				if(fields[j]!=null)
					System.out.print(fields[j] + " -> " + ra.readInt() + "   ");
			}
			System.out.println();
			
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return address;
	}
	
	public static void listAllRecords(String typeName){
		
		try {
			File f = new File(typeName + ".txt");
			RandomAccessFile ra = new RandomAccessFile(f, "rw");
			String fields[] = Type.returnFieldsOfType(typeName);
			ra.seek(0);
			int isLast = ra.readInt();
			long address = 0;
			int pageId=1;
			while(true){
				System.out.println("Accessing page " + pageId);
				for(int i=0 ; i<960 ; i=i+48){
					ra.seek(address+i+24);
				//	System.out.println(address+i+24);
					if(ra.readInt()==1){
						int id = ra.readInt();
						System.out.print("Record " + id + " : ");
						for(int j=0 ; j<fields.length ; j++){
							if(fields[j]!=null)
								System.out.print(fields[j] + " -> " + ra.readInt() + "   ");
						}
						System.out.println();
					}
				}
				System.out.println("\n");
				if(isLast==1){
					break;
				}
				address = address + 1024 ;
				ra.seek(address);
				isLast = ra.readInt();
				pageId++;
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
