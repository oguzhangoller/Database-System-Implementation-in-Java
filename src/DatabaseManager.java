import java.util.Scanner;

public class DatabaseManager {
	
	public DatabaseManager(){
		callMenu();
	}
	
	
	
	public void callMenu(){
		listOptions();
        
        Scanner sc = new Scanner(System.in);
        String input;
        while(!((input = sc.next()).equals("0"))){
        	
        	if(input.equals("1")){
        		System.out.println("Please enter type name :");
        		String typeName = sc.next();
        		System.out.println("Please enter number of fields this type has :");
        		int fieldNumber = Integer.parseInt(sc.next());
        		String fields[] = new String[10];
        		for(int i=0 ; i<fieldNumber ; i++){
        			System.out.println("Field " + (i+1) + " :");
        			fields[i] = sc.next();
        		}
        		Type t1 = new Type(typeName,fieldNumber,fields);
        		t1.createType();
        		System.out.println("Type has been succesfully added to database!\n");
        	}
        	if(input.equals("2")){
        		System.out.println("Enter name of the type to be deleted: ");
        		String typeName = sc.next();
        		Type.deleteType(typeName);
        	}
        	if(input.equals("3")){
        		Type.listAllTypes();
        	}
        	if(input.equals("4")){
        		System.out.println("Enter type name : ");
        		String typeName = sc.next();
        		String fields [] = Type.returnFieldsOfType(typeName);
        		int fieldInfo[] = new int[fields.length];
        		for(int i=0 ; i<fields.length ; i++){
        			if(fields[i]!=null){
        			System.out.println("Enter data for field " + (i+1) + " : " + fields[i]);
        		    fieldInfo[i] = Integer.parseInt(sc.next());
        			}
        		}
        		Record r1 = new Record(typeName,fieldInfo);
        		r1.insertRecord();
        		
        	}
        	if(input.equals("5")){
        		System.out.println("Enter type name : ");
        		String typeName = sc.next();
        		System.out.println("Enter key of record(id) to be deleted : ");
        		int key = Integer.parseInt(sc.next());
        		Record.deleteRecord(typeName, key);
        	}
        	if(input.equals("6")){
        		System.out.println("Enter type name : ");
        		String typeName = sc.next();
        		System.out.println("Enter key of record(id) to be updated: ");
        		int key = Integer.parseInt(sc.next());
        		Record.updateRecord(typeName, key);
        		
        	}
        	if(input.equals("7")){
        		System.out.println("Enter type name : ");
        		String typeName = sc.next();
        		System.out.println("Enter key of record(id) : ");
        		int key = Integer.parseInt(sc.next());
        		Record.searchRecord(typeName, key);
        	}
        	if(input.equals("8")){
        		System.out.println("What type's records you want to be listed : ");
        		String typeName = sc.next();
        		Record.listAllRecords(typeName);
        	}
        	listOptions();
        }
        
	}
	public void listOptions(){
    	System.out.println("\nWhich operation ");
		System.out.println("\nDDL Operations:\n ");
        System.out.println("1. Create a type");
        System.out.println("2. Delete a type");
        System.out.println("3. List all types");
        System.out.println("\nRecord Operations:\n");
        System.out.println("4. Insert a record");
        System.out.println("5. Delete a record");
        System.out.println("6. Update a record");
        System.out.println("7. Search for a record(by key)");
        System.out.println("8. List all records of a type\n");
        System.out.println("Press 0 for Exit!");
    }
}
