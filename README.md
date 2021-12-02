# optimization

## com.alex.optimization.problem.BankAccountProb


### Use-case: 

Manual joining of banking account information


### Scenario: 

Bank account owners of checking and savings accounts may have joint ownership with family, etc.  A common need is to run a batch job against all the bank accounts to perform some sort of action such as sending an email or assembling marketing metrics.  So we would need to identify programmatically which accounts are owned by the joint account owner.


### Components:

````List<Account> getJointOwnerAccountList(List<Account> accountList)````

This class demonstrates reduction of an O(n log n) problem to O(n).  First, we generate 50 million accounts with a random dispersement of joint ownership across some of the accounts.  This code can further be refactored into junit tests at a later date.  Then we convert the account string list to int array for comparison use.  Then iterate through list and add accts primarly owned by the joint account owner.

````int[] convertStringListToIntegerArray(List<String> accountNumberStringList, int min, int max)````

The helper class lets us look at an integer array position's value where the position is the same number as the account/joint owner account id.  This way we don't have to keep iterating through the joint owner account list.  When there is a joint account owner id, we store the value as 1 for quick lookup.  
In another word, if the array number's value is 1, we have a joint account user with that number.  If it is 0 (default int val as int is primitive), we dont have a joint account with that number.  
This speeds up the lookup so it is no longer O(n log n).  Also it is already sorted.

````class Account implements Comparable<Object>````

A sortable account entity representative of non-PII information.


### Results:

Processing 50 million accounts takes ~37 seconds and is under the 1-2 minute threshold of a typical financial batch.  The code is condensed into one file for quick view.  

Set maximum available memory for the JVM to 4000 Megabyte. Constrain in testing to not use more heap memory than defined via parameter: -Xmx4000m

````
Starting generation of 49999500 accounts
Completed generation of accounts in 18915 millis
Starting getJointOwnerAccountList()
getJointOwnerAccountList() - Starting first loop of 49999514 accounts
getJointOwnerAccountList() - Completed first loop of accounts in 2160 MILLIS
getJointOwnerAccountList() - Starting int conversion of  50004 joint user accounts
getJointOwnerAccountList() - Completed int conversion of joint user accounts in 13553 MILLIS
getJointOwnerAccountList() - Starting final account list comparison of  49999514
getJointOwnerAccountList() - Completed final account list comparison in 3100 MILLIS
Completed getJointOwnerAccountList in 18969 MILLIS
Result output of accounts for Test case 1 showing only accounts owned by joint account owners:
Completed ALL PROCESSING of 49999500 accounts in 37887 MILLIS!
Used memory (Mb): 3705
````

![VisualVM](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.49.39%20PM.png)

![VisualVM-Meta](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.50.02%20PM.png)

![VisualVM-main-thread](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.50.48%20PM.png)