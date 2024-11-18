# CSE 360 HELP SYSTEM SETUP

This document outlines the steps required to set up the necessary dependencies for running the Java project, including the H2 Database, JRE System Library, JavaFX SDK, and BouncyCastle.

## Table of Contents
- [Dependencies](#dependencies)
- [Setup Instructions](#setup-instructions)
  - [H2 Database](#h2-database)
  - [JRE System Library](#jre-system-library)
  - [JavaFX SDK](#javafx-sdk)
  - [BouncyCastle](#bouncycastle)
- [Verifying Project Setup](#verifying-project-setup)
- [Compiling and Running the Project](#compiling-and-running-the-project)

## Dependencies
The following dependencies need to be added to ensure the project runs correctly:
1. H2 Database
2. JRE System Library (JRE 21.0.4)
3. JavaFX SDK (Version 21.0.4)
4. BouncyCastle (Latest Version)

## Setup Instructions

### H2 Database
1. **Download H2 Database:**
   - Visit the [H2 Database website](https://www.h2database.com/html/main.html) and download the zipped H2 Database file.
  
2. **Unzip H2:**
   - Unzip the file and navigate to your root directory.

3. **Move h2 File:**
   - Move the unzipped H2 file into the `/User/Username` directory. (replace Username with your computer's actual Username)
   - Rename the H2 file so it has a lowercase h (h2). The filepath should resemble `/User/Username/h2`.

### JRE System Library
1. **Ensure JRE Installation:**
   - Make sure JRE version 21.0.4 is installed on your machine.
  
2. **Link JRE System Library in Eclipse:**
   - Right-click on your project and select **Build Path** > **Configure Build Path**.
   - In the **Libraries** tab, check if the **JRE System Library** is listed. If not, click **Add Library...** and select **JRE System Library**.

### JavaFX SDK
1. **Download JavaFX SDK:**
   - Visit the [Gluon website for JavaFX](https://gluonhq.com/products/javafx/) and download the SDK for version 21.0.4.

2. **Add JavaFX to Eclipse (if not already present):**
   - If you don't have JavaFX configured in Eclipse, follow these steps:
     - Go to **Help** > **Eclipse Marketplace**.
     - Search for "e(fx)clipse" and install it. This will provide support for JavaFX in Eclipse.

3. **Create User Library in Eclipse:**
   - Open Eclipse and go to **Window** > **Preferences**.
   - Expand **Java** and select **Build Path** > **User Libraries**.
   - Click **New** to create a new user library (e.g., `JavaFX`).

4. **Add JAR Files to User Library:**
   - After creating the library, click on **Add External JARs...** and navigate to the location where you extracted the JavaFX SDK.
   - Select all the necessary JAR files from the `lib` directory in the JavaFX SDK.

5. **Add User Library to Your Project:**
   - Right-click on your project and select **Build Path** > **Configure Build Path**.
   - In the **Libraries** tab, click **Add Library...**, choose **User Library**, and select the JavaFX library you just created.

### BouncyCastle
1. **Download BouncyCastle:**
   - Visit the [BouncyCastle website](https://www.bouncycastle.org/download/bouncy-castle-java/#latest) and download the latest stable JAR file.

2. **Add BouncyCastle JAR to Project:**
   - Copy the downloaded JAR file to a known location in your project directory (e.g., a `lib` folder).

3. **Link BouncyCastle to Eclipse:**
   - Right-click on your project and select **Build Path** > **Configure Build Path**.
   - In the **Libraries** tab, click **Add External JARs...** and select the BouncyCastle JAR file.


## Verifying Project Setup
1. Ensure that all required libraries (H2, JRE System Library, JavaFX SDK, and BouncyCastle) are listed in the **Libraries** tab under **Java Build Path**.
2. Clean and rebuild your project to ensure all dependencies are recognized.

## Compiling and Running the Project
1. Compile your project to ensure that it works without any errors.
2. Check the file path in the Database.java file to ensure it is working correctly with your OS:
   - `"jdbc:h2:~/firstDatabase"` only works for Unix/Linux.
   - Replace with `"jdbc:h2:C:\\\\Users\\\\YourUserNameHere\\\\h2\\\\firstDatabase"` if using Windows.
3. When running your JavaFX application, ensure you include the necessary JavaFX module options in your run configuration if needed, particularly for Java 11 and above.

---

By following these instructions, you should have all the necessary dependencies set up correctly in your Java project. If you encounter any issues, please refer to the official documentation or seek assistance.

