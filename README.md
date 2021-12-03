# Samples of optimization

```
Disclaimer:  The following code is the intellectual property of Alex Chang and is original in every
way.  If any lines of code are duplicated, Alex reserves all rights of originality.  The code 
specifies generic business use cases for any business and is not related to any specific company.  
Alex may use his code here to test developers in data structure optimization knowledge and framework 
code for helping microservice developers build a foundation of knowledge.
```


## com.alex.optimization.problem.AccountOwnership

### Use-case: 

Identifying joint account ownership by secondary account owners, across millions of accounts


### Scenario: 

An account and account owner, in any business domain may have multiple relationships with other entities.  One interesting scenario is when accounts have multiple ownership.  Let's relate to this by using a limited financial institution model as an example.  

Known dataset characteristics:
1.  Account owners have checking, savings, credit card, and money market accounts
2.  Account owners may have joint ownership with other account owners such as family, etc.
3.  No PII information is available.  Simply account ids, account type, account owner id, and joint account owner id.

A common need is to run a batch job against all the bank accounts to perform some sort of action such as sending an email or assembling marketing metrics.  In this example, we want to identify programmatically which accounts are owned by any joint account owners.

In a relational database, we would set a foreign key as the secondary owner account and index account ids.  What if we didn't use a database?  Below is a way to manually join in memory account datasets.

### Outcome:
We need a resultset of only account objects owned solely by the account owners who are joint owners of a different account sorted against most recently created account ids at the top.


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

Processing 50 million accounts takes ~18 seconds and is under the 1 minute threshold of a typical financial batch.  The code is condensed into one file for quick view.  

Set maximum available memory for the JVM to 4000 Megabyte. Constrain in testing to not use more heap memory than defined via parameter: -Xmx4000m

Typically, batch job runtimes in the wild are in Java 7.  With Java 8+, metaspace auto-sizing replaces permgen.

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
# Running 64-bit HotSpot VM.
# Using compressed oop with 3-bit shift.
# Using compressed klass with 3-bit shift.
# WARNING | Compressed references base/shifts are guessed by the experiment!
# WARNING | Therefore, computed addresses are just guesses, and ARE NOT RELIABLE.
# WARNING | Make sure to attach Serviceability Agent to get the reliable addresses.
# Objects are 8 bytes aligned.
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]

com.alex.optimization.problem.AccountOwnership$Account object internals:
OFF  SZ               TYPE DESCRIPTION                VALUE
  0   8                    (object header: mark)      0x0000000000000005 (biasable; age: 0)
  8   4                    (object header: class)     0x0016f6d2
 12   4   java.lang.String Account.accountUser        (object)
 16   4   java.lang.String Account.jointAccountUser   (object)
 20   4   java.lang.String Account.accountType        (object)
Instance size: 24 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
24 bytes x 49999500 accounts = 1.199988 GB
````

![VisualVM](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.49.39%20PM.png)

![VisualVM-Meta](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.50.02%20PM.png)

![VisualVM-main-thread](https://github.com/alexoms/optimization/blob/main/images/Screen%20Shot%202021-12-02%20at%201.50.48%20PM.png)