# PowerGrab

This project contains the design and implementation of an Autonomous Agent (computer player) which plays against the human player in a game of PowerGrab. In the game, players complete in controlling a Drone around a Map in order to collect the most coins from randomly located Stations. A full description of the PowerGrab game, rules and specifications can be found in the 'PowerGrab project specifications.pdf'.

The strategy used by the Autonomous Agent is a variant of the Travelling Salesman Problem using a Greedy nearest-station algorithm and an A* informed search. Further optimisation with an 2-Opt Swap algorithm is possible. The final result was that the **Autonomous Agent managed to collect maximum (100%) of the coins on 99.5% of possible maps.**

## Software Architecture Description

Using the Separation of Concerns design principle, I have identified 3 main areas of the program, each separated further by distinct concerns. 15 Classes and 1 Interface were created in order to implement the project in a clean and accurate manner. 

* Control of the Program
  * **App**: responsible for initialising and coordinating all other Control classes
  * **InputOutputManager**: handling input/output operations
  * **Map**: loading the map features, storing and updating stations
  * **GameManager**: playing the game, checking the rules, responding to the drone's moves
* Drone Components
  * **Drone**: containing the drone's variables (model)
  * **DroneLogic**: interface describing the drone's decision-making process
  * **StatelessLogic** and **StatefulLogic** implementations of the Logic
  * **StatelessDrone** and **StatefulDrone** extensions of the Drone
* Spatial Features
  * **Station**: storing information about the stations on the Map
  * **Position**: describing the geographical coordinates of Drone and Stations on the Map
  * **Direction**: enumerating the 16 possible directions in which the Drone can move
  * **MapFunctions**: providing useful methods for calculating distances and directions


### Description of Files

* **PowerGrab project specifications.pdf** - contains the description of the game, task and scoring points
* **Project Report.pdf** - contains the high and low level descriptions of each class, the AI Strategy used and the results obtained
* **src folder** - contains the code of all the classes developed for the project (outlined in the software architecture description)

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Java JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - OOP Programming language
* [GeoJSON](https://geojson.org/) - an open standard format designed for representing simple geographical features

## Authors
**Valentine Dragan**
