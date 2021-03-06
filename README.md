# ERmapper

## Setup
The following are instructions of how to install and set up the software required to run the ERMapper application in Android studio.  
1. Install Android studio 3.0
  1. JRE: 1.8.0_152-release-9159b01
  2. JVMOpenJDK 64-bit Server VM  by JetBrains s.r.o
  
2. In Android studio go to File -> open an existing android studio project  
   A dialog will pop up, select the directory where you saved ERMapper, and select  **ERMapper->ERMapper->APP**. 

**The following are instructions on how to setup the program to run on an emulator in android studio**
  1. In Android studio go to Tools > Android > SDK Manager.
  2.	When the pop up screen opens select Nougat 7.0.0 or Nougat 7.1.1 ( The program requires a min API Level of 24).
  3.	In Android studio go to Tools > Android > AVD Manager.
  4.	When the pop up screen opens select create virtual device.    Go to Tablets > Nexus 10 then press Next.  
  5.	On the Next screen make sure the SDK that you choose from step 2 is selected and then click next and finish
  
**Enable USB Debugging on your android device**
  1. Open the Settings App.
  2. (Only on Android 8.0 or higher) Select System.
  3. Scroll to the bottom and select About phone.
  4. Scroll to the bottom and tap Model number 7 times.
  5. Return to the previous screen to find Developer options near the bottom. 
    
## Running the software
  Now that you have imported the code and set up all the necessary software you can run the program.
  Press the green run arrow depicted in the image bellow, it will launch a prompt that asks you to select a device, you may choose to     run on the android device or emulator and press OK.   This will install the software onto your android device or launch the emulator.   When it is ready the ERMapper will launch. 


## Create a New Drawing
From the initial screen select new ER Diagram, this will launch a new activity with a blank canvas, and some button along the bottom.

### Creating Objects
 Press either the Entity or Attribute button to create either object. They will appear in the Top left side of the canvas; an Entity will be square and an Attribute will be oval. Both can be moved around by clicking and dragging them around the screen.

 Press the Relationship object to create a relationship. Once the button is selected, click on the first object you want to connect, you will notice a line has been drawn from that object to your mouse, you may then move your mouse and click the second object to create the connection.    If for some reason your relationship is not created on the first try or the line is no longer available, click on the relationship button again. 

**Note** :

  1. Double clicking an Entity can set it to weak, 
  2. Every Foreign key must have an identifying primary key with the same name
  3. Double clicking and Attribute can set it to primary
  4. All objects have a name that can be edited by clicking it, however it uses an 
  onFocusChange listener, so after you edit the name you must click something else on the screen, or press tab or enter.
  5. Every Relationship between two Entity objects will have an edit text as a cardinality, the cardinality must be 1, N, or M.  The system does not understand any other parameters

  
### Saving the Diagram 
   By pressing save the diagram you will save the diagram in XML format to the android device 
    
## Normalize The Diagram 
Pressing Normalize will launch a new Activity that automatically converts all entity objects to relations, creating a relational Schema, finding all functional dependencies and normalizing the schema into third normal form. From here you will be able to see all attributes, functional dependencies, min cover, candidate keys, and lossless join /dependency preserving tables. 
  
### Create a Database
By pressing the create database button the activity will open a connection to an SQLite database and create each a new database inserting each relation from the schema as a table in the database.

# Running the Tests
The following tests check the functionality and usability of the program by running unit tests that compares the expected vs actual results of emulated tests which emulate the system and allows for user interaction, and Java tests which test the diagram and normalization functions.
  1. To run jUnit Emulated tests go to src->androidTests. Right click and press run
  2. To run jUnit Java tests go to src->tests. Right click and press run.
  
 **Note** : Emulated tests sometimes fail do to sequence of events when run as a test suite.  If this occurs you can successfully run each test individually. 
  
# Accessing Files
When saving XML files or a Database the files are saved to the android devices internal storage which can be accessed through android studio using view->tools window -> device file explorer.  Then in the resulting pop up going to data -> data -> ERMapper and then either files or database for the respectful file types. 
  
  
