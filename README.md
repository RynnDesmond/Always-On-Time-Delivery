# Always-On-Time-Delivery
‘Always On Time’ Sdn Bhd

![](https://github.com/RynnDesmond/Always-On-Time-Delivery.git/src/res/images/Untitled-2.png)

[Always-On-Time-Delivery](https://github.com/RynnDesmond/Always-On-Time-Delivery.git) simulates the delivery process by finding the routes using different algorithm.

## Contributors
    - Desmond Tee Yu Wei
    - Boo Zhan Yi
    - Chong Jun Yi
    - Nigel

## Algorithms used
- Depth First Search
- Greedy Search
- MCTS algorihtm
- Best First Search

## To set up the project in IDE
We use Intellij IDEA as the example
1. Download and import the external libraries
   * Download
       * Make sure you have the JDK installed (Java 8 onwards will be compatible)
       * download [JavaFX SDK version 15.0.1](https://gluonhq.com/products/javafx/)
    
   * Include external libraries into IDE
       1) File > Project Structure > Libraries
       2) Project Settings > Libraries > + 
       - ![](https://www.jetbrains.com/help/img/idea/2020.3/javafx-install-sdk.png)
       3) Add the .jar file of the external libraries
       - Add the lib folder of JavaFX
       4) Run the program once, and you will see an error indicates JavaFX runtime component are missing
       5) Select Add configuration
       - ![](https://i.stack.imgur.com/eOaYu.png)
       6) From More options list, select Add VM options (include JavaFX runtime component)
       - ![](https://www.jetbrains.com/help/img/idea/2020.3/javafx-vm-options-add-field.png)
         * --module-path </your/path/to/javafx/sdk/lib> --add-modules javafx.controls,javafx.fxml 
         * e.g.:
            - --module-path "C:\Program Files\Java\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml
       7) There you go! You can run the program now 
