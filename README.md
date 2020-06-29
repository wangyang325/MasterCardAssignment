######  MasterCard Assignment (Requirements) ------------
# Jersey Store

Jersey Store® is a hip new business that specializes in the trade of soccer jerseys (how cool is that!).

You've been hired as a Java developer to create a RESTful service for their jersey stock management. 

Your task is to finish the work that the previous developer already started and get the API up and running. 


## Getting Started

You are free to use any library you want to get the job done.

### Prerequisites

* Java 8 
* Maven 3

## Goals

* Finish the CRUD API for the JerseyController.
    * You're given 2 GET methods on the controller, you need to implement the [REST](https://media.giphy.com/media/SUeUCn53naadO/giphy.gif)...
* Fix the code on the File Loader.
    * The previous developer did a really poor job writing the CSV loader - highlight his mistakes with comments in the current file.
    * There's no point trying to save that awful code - could you implement a better solution in `GoodFileLoader`? 
    * [BONUS] There's been a report of a discrepancy between the warehouse stock (`warehouseStock.csv`) and the database. Can you find the missing jerseys?
* Write tests to ensure that the code is correct
* The SalesController currently uses a GET method to add a sale, and doesn't take an amount. Can you improve this method?
* Enhance Jersey to include a `material` which defaults to `COTTON`. Values allowed should be `COTTON` or `NYLON`.
* Fix any code-issues that you find (such as poorly scoped variables, etc.)
* Ensure that the code compiles and can be easily run when reviewing. Include any necessary instructions.

### Bonus

* **Security:** Some resources should be protected. Can you ensure that only the right users can delete data?
  * The user can be generated at runtime and doesn't have to be saved to the database
  * A large bonus here would be saving a user to the database, and taking the correct security precautions when doing so
* **Versioning:** Can you add a new controller to represent a version 2 without breaking the existing contracts?
  * Can you ensure that the original response is maintained without the additional `material` field?
* **Threading:** The SalesServiceTest is currently failing. The total sales don't match what is expected. Can you fix the issue?
  * The method can be rewritten as you see fit.
  
#  MasterCard Assignment (implementation by Yang Wang) 

## Deploy on Aws by using docker

Demo:  http://34.247.85.101/jersey/login

Springboot + Mysql + Redis on AWS (Linux: docker)  

## Function Implementation Summary

1. Add two web pages (Index and Login) to collect the input data.

2. Optimize the file load method. (read by caches, please read the other solutions in the BadFileLoader.java) 

3. Implement 4 functions:

   a) Check Csv and database: The function simulates making an inventory.
   
      ### Key issue: 
      
        When the CSV is huge, using too much memory.
        
        The file exist the same key records.
       
      ### Solution:
      
        Read the file by cache, merge the same key records into one record to reduce using of memory. 
      
      ### Problem:
      
        When the file is huge, because put all records into memory that may cause the out of memory error.
        Dividing the records into parts and reading by parts (flush the memory) is necessary.
        
      ### Function implementation: 
      
        Read the csv data, use the all columns (except Amount column) as key to check the database,
      
        Pattern 1: Good match (both have) -> Result: jersey id and amount columns have data.
      
        Pattern 2: exist in cvs, not exist in database -> Result: amount column has data.
      
        Pattern 3: not exist in cvs, exist in database -> Result: jersey id has data.
      
   b) Update database based csv: the function simulates updating the product and stock info by reading Csv.
      
      ### Key issue: 
        
        When the cvs file is huge, a part of data may be updated failed. How to keep consistency is a problem.
        
      ### Solution:
      
        Use the transaction to manage the updating.
        
        Use the batchUpdate to improve the efficiency. (Not implement) 
        
      ### Function implementation: 
      
        Read the csv data, use the all columns (except Amount column) as key to check the database,
       
        Pattern 1: Good match (both have) -> Update the stock table (Redis) by Jersey ID.
     
        Pattern 2: exist in cvs, not exist in database (Mysql) -> Add the Jersey info and Add the stock info into Jersey and stock table.
    
   c) Sales: the function simulates selling the Jersey and Checking total sales.
      
      ### Key issue: 
      
        When multiple microservices and processes update the same data (Total sales), keeping consistency is a problem.
      
      ### Solution: 
      
        Distributed Lock: (Redis + Lua) <- chose
           
           Use the Redis' s transaction and lua script to solve the high concurrency problem. 
           
           The provided solution has passed the 1000-thread test.
        
        Other solutions:
        
          1. Possitive lock: 
           
             Add a version column, before updating the data, check the version (timestamp). -> High IO for database
        
          2. Negative Lock: 
           
             use for update to lock the row that needs to update. -> easier to be a dead lock 
        
      ### Problem:
      
        The Redis cannot ensure the huge high concurrency, to solve the problem can use the message queue (Kafka or others).
        (Not implement)  
       
      ### Function implementation: 
      
        1. display all stock info of jersey on the index web page.
       
        2. input the id and amount to buy the jersey, check the stock.
       
        3. if amount > stock -> failed to buy, 
       
           if amount <= stock -> deduct the amount from stock and increase the total sales. 
    
   d) Permission management: (User -> SysRole -> SysPermission)
   
      ### Key issue: 
      
        How to manage the user authority info.
        
        How to limit the calls between the microservices.
        
        How to share the token info between the microservices.
      
      ### Solution: 
      
        Use the Shiro to make the token and check user. 
        
        Use the Shiro to limit the access of restful api. [@RequiresPermissions]
        
        Use the session (store into Redis) to share the token and user info between the microservices.
        
        Use the user -> role -> permission model to implement the permission management.
        
      ### Function implementation:
      
        Login: check the input data and create token, save the info into session (Redis).
       
        API permission: use the token to check the permission before accessing the restful API.

## Task 1: * Finish the CRUD API for the JerseyController.

1. ./controller/IndexController:

   1) The front page services provide an index page and login page.
   
   2) Login page: Check the user info and make authority token, save them into session.
      
      a) Function -> [showLogin] (/jersey/login): Initial the login page.
      
      b) Function -> [login] (/jersey/doLogin): Use the Shiro to check user and create token.
      
      c) Function -> [logout] (/jersey/doLogout): Delete the token and session data.
      
   3) Index page: collect the input data.
      
      a) Function -> [showIndex] (/jersey/index): Initial the index page.
      
2. ./controller/JerseyController:
    
    1) Function -> [uploadCheck] (/jersey/upload/check): Check the difference between the cvs and database
    
    2) Function -> [uploadUpdate] (/jersey/upload/update): 
       
       a) Check the difference between the cvs and database.
       
       b) add the data when they do not exist in database and update the stock table (Redis).
        
3. ./controller/SalesController:
   
   1) Function -> [makeSale] (/rest/api/v1/sale): 
   
      a) update the stock amount by Jersey id.
      
      b) update the total amount.

## Task 2: * Fix the code on the File Loader.

1. Read the file by using cache.

2. Provided some solutions.

## Task 3: * Versioning. (not implement)

  ### Solution:
    
    1. Java: 
       
       make child classes for related classes, the new version uses the child class, the old version uses the father class.
    
    2. Database:
       
       Add new column into the same table. Do not change the existing data.
       
    3. Web page:
       
       Prepare two versions.

## Task 4: * Security.
  
  ### Solution: 
  
    1. Permission management.
    
    2. Use API Gateway (Sring cloud) to protect all microservices in the private network.  (Not implement, but can find the sample in my github)
    
    3. Use the eureka services (Spring cloud) to manage the microservices (cluser). (Not implement, but can find the sample in my github)
    
    4. Use a cluser of Redis and Mysql.
    
    5. Use different available zones (AWS) at different physical locations.
    
    6. Put the private services into different private subnet (AWS) to avoid accessing from the internet.   
    
     
