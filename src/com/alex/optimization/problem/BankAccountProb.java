package com.alex.optimization.problem;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankAccountProb {

	private static final long MEGABYTE = 1024L * 1024L;
	
	public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    
	// retrieves list of accounts which are owned by the joint owners of other accounts
	public static List<Account> getJointOwnerAccountList(List<Account> accountList){
		List<Account> answer = new ArrayList<Account>();
		
		int minJointAccountUser = 0;
		int maxJointAccountUser = 0;
		
		Instant start = Instant.now();
		System.out.println("getJointOwnerAccountList() - Starting first loop of " + accountList.size() + " accounts");
		
		// iterate through list and identify joint account owners
		List<String> jointAccountUserList = new ArrayList<String>();
		for (Account accountListItem : accountList) {
			if (!accountListItem.getJointAccountUser().equals("0")){
				// populate unsorted joint account user list
				jointAccountUserList.add(accountListItem.getJointAccountUser());
				// ensure we update know the minimum joint account number so we can use it for an int array for speed
				if (minJointAccountUser == 0 || minJointAccountUser > Integer.parseInt(accountListItem.getJointAccountUser())) {
					minJointAccountUser = Integer.parseInt(accountListItem.getJointAccountUser());
				}
				// ensure we update know the max joint account number so we can use it for an int array for speed
				if (maxJointAccountUser == 0 || maxJointAccountUser < Integer.parseInt(accountListItem.getJointAccountUser())) {
					maxJointAccountUser = Integer.parseInt(accountListItem.getJointAccountUser());
				}

			}
		}

		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("getJointOwnerAccountList() - Completed first loop of accounts in " + timeElapsed + " MILLIS");
		
		start = Instant.now();
		System.out.println("getJointOwnerAccountList() - Starting int conversion of  " + jointAccountUserList.size() + " joint user accounts");
		
		
		// sort jointAccountUserList descending for speed
		//Collections.sort(jointAccountUserList);
		
		// convert string list to int array for O(n) comparison use
		int[] jointAccountIntArray = convertStringListToIntegerArray(jointAccountUserList, minJointAccountUser, maxJointAccountUser);
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("getJointOwnerAccountList() - Completed int conversion of joint user accounts in " + timeElapsed + " MILLIS");
		
		start = Instant.now();
		System.out.println("getJointOwnerAccountList() - Starting final account list comparison of  " + accountList.size());
		
		// iterate through list and add accts primarly owned by the joint account owner
		for (Account accountListItem : accountList) {
			// Check if each account is one primarily owned by the joint account user
			int currentAccountInt = Integer.parseInt(accountListItem.getAccountUser());
			if (minJointAccountUser < currentAccountInt && currentAccountInt < maxJointAccountUser ) {
				// This is O(n)
				if(jointAccountIntArray[currentAccountInt] == 1){
					answer.add(accountListItem);
				}
			}

			// This is O(n log n)			
			//if(jointAccountUserList.contains(accountListItem.getAccountUser())) {
			//	answer.add(accountListItem);
			//}
		}
		
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("getJointOwnerAccountList() - Completed final account list comparison in " + timeElapsed + " MILLIS");
		
		// used Comparable interface to define natural order.  Overrode compareTo method in Account class
		Collections.sort(answer);
		
		// The equivalent would be inline as:
		//Collections.sort(answer, new Comparator<Account>(){  
		//    @Override  
		//    public int compare(Account a1, Account a2){  
		//         return Integer.parseInt(a1.getAccountUser()) - Integer.parseInt(a2.getAccountUser());  
		//    }  
		//}); 
		
		return answer;
	}
	
	// Helper class so we can just look at an integer array position's value where the position is the same number as the account/joint owner account id.
	// This way we don't have to keep iterating through the joint owner account list.  When there is a joint account owner id, we store the value as 1 for quick
	// lookup.
	public static int[] convertStringListToIntegerArray(List<String> accountNumberStringList, int min, int max){
		
		int[] intArray = new int[max + 1];
		for (String stringListItem : accountNumberStringList) {
			intArray[Integer.parseInt(stringListItem)] = 1;
		}
		
		// so if the array number's value is 1, we have a joint account user with that number.  If it is 0 (default int val as int is primitive), 
		// we dont have a joint account with that number.  This speeds up the lookup so it is no longer O(n log n).  Also it is already sorted.
		return intArray;
	}
	
	
	public static void main(String[] args) {
		// generate 5M to test speed
		int min = 500;
		int max = 50000000; // 50M
		Instant start = Instant.now();
		Instant startTotalRun = Instant.now();
		System.out.println("Starting generation of " + String.valueOf(max - min) + " accounts");
		
		// Populate list for test case 1
		List<Account> testCase1AccountList = new ArrayList<Account>();
		testCase1AccountList.add(new Account("400","0","Checking"));
		testCase1AccountList.add(new Account("411","412","Savings"));
		testCase1AccountList.add(new Account("412","0","Credit Card"));
		testCase1AccountList.add(new Account("411","0","Money Market"));
		testCase1AccountList.add(new Account("404","0","Checking"));
		testCase1AccountList.add(new Account("405","0","Checking"));
		testCase1AccountList.add(new Account("406","407","Checking"));
		testCase1AccountList.add(new Account("407","0","Savings"));
		testCase1AccountList.add(new Account("407","403","Credit Card"));
		testCase1AccountList.add(new Account("407","0","Money Market"));
		testCase1AccountList.add(new Account("399","407","Savings"));
		testCase1AccountList.add(new Account("355","0","Savings"));		
		testCase1AccountList.add(new Account("314","315","Checking"));
		testCase1AccountList.add(new Account("315","0","Savings"));

		
		for (int i = min; i < max; i++) {
			if (i%1000 == 0) {
				testCase1AccountList.add(new Account(String.valueOf(i),String.valueOf((int) ((Math.random() * (max - min)) + min)),"Savings"));
			} else {
				testCase1AccountList.add(new Account(String.valueOf(i),"0","Checking"));
			}
			
		}
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("Completed generation of accounts in " + timeElapsed + " millis");
		
		// Display initial list for test case 1
		//System.out.println("Initial list of accounts for Test case 1:");
		//for (Account item : testCase1AccountList) {
		//	System.out.println(item.getAccountUser() + " " + item.getJointAccountUser() + " " + item.getAccountType());
		//}
		
		System.out.println("Starting getJointOwnerAccountList()");
		start = Instant.now();
		// Display processed list for test case 1
		List<Account> testCase1ResultList = getJointOwnerAccountList(testCase1AccountList);
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("Completed getJointOwnerAccountList in " + timeElapsed + " MILLIS");
		
		System.out.println("Result output of accounts for Test case 1 showing only accounts owned by joint account owners:");
		//for (Account item : testCase1ResultList) {
			//System.out.println(item.getAccountUser() + " " + item.getJointAccountUser() + " " + item.getAccountType());
		//}
		
		timeElapsed = Duration.between(startTotalRun, finish).toMillis();
		System.out.println("Completed ALL PROCESSING of " + String.valueOf(max - min) + " accounts in " + timeElapsed + " MILLIS!");
		
		// Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: " + bytesToMegabytes(memory));
	}

	public static class Account implements Comparable<Object> {
		private String accountUser;
		private String jointAccountUser;
		private String accountType;
		
		Account(String accountUser, String jointAccountUser, String accountType) {
			this.setAccountUser(accountUser);
			this.setJointAccountUser(jointAccountUser);
			this.setAccountType(accountType);
		}
		
		public void setAccountUser(String accountNumber) {
			this.accountUser = accountNumber;
		}
		
		public String getAccountUser() {
			return this.accountUser;
		}
		
		public void setJointAccountUser(String accountNumber) {
			this.jointAccountUser = accountNumber;
		}
		
		public String getJointAccountUser() {
			return this.jointAccountUser;
		}
		
		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}
		
		public String getAccountType() {
			return this.accountType;
		}

		@Override
		public int compareTo(Object o) {
			Account e = (Account) o;
	        return Integer.parseInt(getAccountUser()) - Integer.parseInt(e.getAccountUser());  
		}
	}
}
