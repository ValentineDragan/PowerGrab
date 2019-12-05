\documentclass{article}
\usepackage[utf8]{inputenc}
%% Sets page size and margins
\usepackage[a4paper,top=3cm,bottom=2cm,left=2cm,right=2cm,marginparwidth=1.75cm]{geometry}
\usepackage{hyperref}
\usepackage{listings}
\hypersetup{
    colorlinks=true,
    linkcolor=blue,
    filecolor=magenta,      
    urlcolor=cyan,
}
\setlength{\parindent}{0in}


\title{Informatics Large Practical 2019-20 - Coursework 2 \\ \bigskip
Implementing an Autonomous Agent  \protect\\ to play against a Human Agent in a location-based Strategy Game}
\author{Author: Valentine Dragan s1710228 }
\date{December 2019}

\usepackage{natbib}
\usepackage{graphicx}
\usepackage{caption}
\usepackage{subcaption}
\usepackage{float}


\begin{document}

\maketitle
\noindent
\tableofcontents
\newpage



\section{Introduction}

This document presents the design and implementation of the PowerGrab game framework, in which players (human or computer) compete in controlling a Drone around a Map in order to collect the most coins from randomly located Stations. A full description of the PowerGrab game, rules and specifications can be found in the \textit{PowerGrab project specifications.pdf} document. \\

Moreover, the document presents the implementation of two versions of an Autonomous Agent (computer player) which plays against the human player. The first version, referred to as Stateless Drone, is appropriate for playing against a novice human player. The second version, referred to as Stateful Drone, is appropriate for playing against an expert human player.
\\

\textbf{The project is available for use on GitHub:} \url{https://github.com/ValntinDragan/PowerGrab.git} \\
(Will be Private until the coursework marks are released) \\


This document captures the Software Architecture, Class Documentation and Implementation of the Stateful Drone, which should be read in order to understand and maintain the application.
\bigskip

\section{Software architecture description}
For this project I have decided to use the \textbf{Separation of Concerns}\textsuperscript{\href{https://en.wikipedia.org/wiki/Separation_of_concerns}{[1]}} design principle: separating the program into distinct sections such that each section addresses a separate concern. This principle was first introduced to me in the course \textit{Informatics 2C: Software Engineering at The University of Edinburgh}. This design concept provides several benefits such as: making the code easier to read, modify and test.
\\

\begin{figure}[H]
    \centering
    \includegraphics[width=.9\textwidth]{appCode.png}
    \caption{Example: easy to read code in the App.main() because of Separation of Concerns}
    \label{fig:appcode}
\end{figure}

Following this principle, I have identified 3 main areas of my program, each separated further by distinct concerns. 15 Classes and 1 Interface were created in order to implement the project in a clean and accurate manner. The software architecture can be described as follows:

\begin{itemize}
    \item Control of the Program
    \begin{itemize}
        \item \textbf{App}: responsible for initialising and coordinating all other Control classes
        \item \textbf{InputOutputManager}: handling input/output operations
        \item \textbf{Map}: loading the map features, storing and updating stations
        \item \textbf{GameManager}: playing the game, checking the rules, responding to the drone's moves
    \end{itemize}
    \medskip
    \item Drone Components
    \begin{itemize}
        \item \textbf{Drone}: containing the drone's variables (model)
        \item \textbf{DroneLogic}: interface describing the drone's decision-making process
        \item \textbf{StatelessLogic} and \textbf{StatefulLogic} implementations of the Logic
        \item \textbf{StatelessDrone} and \textbf{StatefulDrone} extensions of the Drone
    \end{itemize}
    \item Spatial Features
    \begin{itemize}
        \item \textbf{Station}: storing information about the stations on the Map
        \item \textbf{Position}: describing the geographical coordinates of Drone and Stations on the Map
        \item \textbf{Direction}: enumerating the 16 possible directions in which the Drone can move
        \item \textbf{MapFunctions}: providing useful methods for calculating distances and directions
    \end{itemize}
    Additionally, the \textbf{Debugger} class contains several useful methods for debugging the project. This class should not be added to a public release, but is very useful for debugging the code in a clean way.
\end{itemize}
\\

\subsection{Class Diagram}
The figure below presents the Class Diagram of the application. As it has been described above, each class addresses one of the 3 distinct sections: \textit{Control of the Program}, \textit{Drone Components} or \textit{Spatial Features}. A high-level description of the classes is presented in the next section.
\\

For ease of visualisation, some of the relationships have been truncated (i.e. the Drone, Station and Node all have relationships with the Position class). 
\\

\begin{figure}[H]
    \centering
    \includegraphics[width=.82\textwidth]{powergrab-UML.png}
    \caption{UML Class diagram}
    \label{fig:umldiagram}
\end{figure}

\subsection{Class Hierarchical Relationships}
\textbf{Drone} is the parent-class of the \textbf{StatelessDrone} and \textbf{StatefulDrone}. This is because both Stateless and Stateful drones have similar fields (e.g. current position, power, coins) and similar functionality (e.g. move, charge). It is therefore appropriate to define a parent-class which contains the common information, and then create Stateless and Stateful sub-classes of it with specific modifications.
\\

\textbf{StatelessLogic} and \textbf{StatefulLogic} are implementations of the \textbf{DroneLogic} Interface. Since both the Stateless and Stateful drone have the same decision goal: finding the best move, it is appropriate to create an Interface for the drone's logical component, and then provide separate Stateless and Stateful implementations of it.
\bigskip

\section{Class Documentation}
This section shows the definitions of class members and how to use them. It presents method signatures and descriptions, but not their internal implementations. It includes links between classes so that you can rapidly follow the logic of the code. \\

For code implementations, please open the .java class files. Each method contains comments and JavaDoc which should help in understanding how each method operates.

\begin{figure}[H]
    \centering
    \includegraphics[width=.8\textwidth]{chargeDroneCode.png}
    \caption{Example of JavaDoc contained in the code}
    \label{fig:chargedronecode}
\end{figure}
\bigskip

\subsubsection{App}
\label{sec:app}
\textbf{Description:}
This class is the main \textit{Control Component} which starts and manages the entire program. Control is further divided between \hyperref[sec:iomanager]{InputOutputManager}, \hyperref[sec:map]{Map} and \hyperref[sec:gamemanager]{GameManager}.
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{main(String[] args)}: Manages and runs the entire program
    \begin{enumerate}
        \item Tells \textit{InputOutputManager} to parse the input arguments.
        \item Tells the \textit{Map} to read the GeoJSON and load all the \hyperref[sec:station]{Stations}.
        \item Tells \textit{InputOutputManager} to open the text file.
        \item Initialises the {GameManager} which initialises the \textit{Drone} and starts the game.
        \item Tells \textit{InputOutputManager} to draw the drone path.
    \end{enumerate}
\end{itemize}
\bigskip


\subsubsection{InputOutputManager}
\label{sec:iomanager}
\textbf{Description:} This class is one of the \textit{Control Components}, where input and output messages are directed to. The inputs are the arguments entered by the \textit{User} when starting the \hyperref[sec:app]{App}. The outputs are the \hyperref[sec:drone]{Drone}'s moves and path written down in the text and geojson files.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{date}: the date of the generated \texit{Map}
    \item \textit{startingLat} and \textit{startingLong}: the \textit{Drone}'s starting position
    \item \textit{seed}: the seed of the Random Number Generator used by the \textit{Drone}
    \item \textit{droneType}: the type of the \textit{Drone}
    \item \textit{fileName}: the name of the text and geojson files
    \item \textit{textWriter}: the PrintWriter object used for writing in the text file
    \item \textit{geojsonWriter} : the PrintWriter object used for writing in the geojson file
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{parseArgs(String args[])} used to parse the user inputs into the fields mentioned above, which are necessary for running the rest of the \textit{Control Classes}.
    \item \textbf{openTextWriter} is called before writing the drone moves in the text file.
    \item \textbf{closeTextWriter} is called after all the moves have been written in the text file.
    \item \textbf{writeDroneMove(Drone drone, Direction direction)} is used to write down a \textit{Drone}'s move in the text file. This method is called by the \hyperref[sec:gamemanager]{GameManager} after the drone executes a move.
    \item \textbf{writeGeoJson(Drone drone)} is called by the \hyperref[sec:app]{App} after the game has ended to write down the drone's path. The \hyperref[sec:drone]{Drone}'s \textit{moveHistory} is transformed into a geojson feature and appended to the \hyperref[sec:map]{Map}'s \textit{feature_collection}.
\end{itemize}
\bigskip


\subsubsection{GameManager}
\label{sec:gamemanager}
\textbf{Description:} This class is one of the \texit{Control Components}, whose purpose is to start the game, interact with the \hyperref[sec:drone]{Drone} and respond to its moves. 
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{theDrone}: the \texit{Drone} (Stateless or Stateful) which is used for playing the game
    \item \textit{moveNumber}: keeps track of what move the \textit{Drone} is at
    \item \textit{moveAllowed}: represents whether or not the \textit{Drone} is allowed to \textit{move()}. This ensures that the \textit{Drone} doesn't try to cheat.
    \item \textit{chargeAllowed}: represents whether or not the \textit{Drone} is allowed to \textit{charge()}. This ensures that the \textit{Drone} doesn't try to cheat.
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{initialise()} is used for initialising \textit{theDrone} field and inspecting the starting \hyperref[sec:position]{Position}.
    \item \textbf{startGame()} is used to start playing the \textit{Drone}'s game. Each turn, the method will:
        \begin{enumerate}
            \item Get the \hyperref[sec:drone]{Drone}'s next move by calling its \textit{getNextMove()} method
            \item Move the drone in the specified direction
            \item Charge from the nearest station, if within range
            \item Write down the move in the output file
        \end{enumerate}
     (until \textit{moveNumber} reaches 250, or \textit{theDrone} runs out of power):
\end{itemize}
\bigskip


\subsubsection{Map}
\label{sec:map}
\textbf{Description:} This class is one of the \textit{Control Components}, responsible for loading the features from the GeoJSON website, storing and updating the Stations.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{feature\_collection}: the \textit{FeatureCollection} representing the GeoJSON code pulled from the url.
    \item \textit{stations}: the \textit{List} of \hyperref[sec:station]{Stations} present on the map.
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{loadMap(String urlString)}: calls the next two methods described below, which extract the GeoJSON from the website, load it into the \textit{feature\_collection} and initialise the \textit{stations}.
    \item \textbf{loadFeatureCollection(String mapString)}: connects to the specified URL and extracts the features into the \textit{feature\_collection} field.
    \item \textbf{loadStations()}: returns a \textit{List} of all the \hyperref[sec:station]{Stations} on the map.
    \item \textbf{updateStation(Station station, double coinsAmount, double powerAmount)}: is used to update a \textit{Station}'s fields after the \textit{Drone} has charged from it. This method is called by the \hyperref[sec:gamemanager]{GameManager} after charging the \hyperref[sec:drone]{Drone}.
\end{itemize}
\bigskip

\subsubsection{MapFunctions}
\label{sec:mapfunctions}
\textbf{Description:} This is a singleton class which provides useful methods for calculating distances and directions.
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNearestStation(Position origin)}: returns the nearest \hyperref[sec:station]{Station} from the origin. This method is used by the \hyperref[sec:gamemanager]{GameManager} to determine the \textit{Station} that's closest to the \hyperref[sec:drone]{Drone}.
    \item \textbf{getStationsByDistance(Position origin)}: returns a \textit{List} of all \hyperref[sec:station]{Stations} ordered by distance from the origin. This method makes use of the \hyperref[sec:distancecomparator]{DistanceComparator}.
    \item \textbf{directionToReach(Position origin, Position destination)}: return the \hyperref[sec:direction]{Direction} which would bring the origin Position (e.g. the \textit{Drone's position}) within range of the destination (e.g. a \textit{Station's position}). This is used by the \hyperref[sec:statelesslogic]{StatelessLogic} to determine which \textit{Direction} reaches the nearest positive station.
\end{itemize}
\bigskip

\subsubsection{Drone}
\label{sec:drone}
\textbf{Description:} This is a Parent-class which holds information about a drone's status and basic functionality. This class is essential for representing the drone's model.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{currentPos}: the drone's current \hyperref[sec:position]{Position} on the \hyperref[sec:map]{Map}
    \item \textit{power}: the drone's accumulated power
    \item \textit{coins}: the drone's accumulated coins
    \item \textit{rnd}: the Random Number Generator with the seed corresponding to the \textit{User}'s input
    \item \textit{moveHistory}: an \textit{ArrayList} of \hyperref[sec:position]{Positions} representing the \textit{Drone}'s \textit{Position} after each move 
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove()}: this method is designed to return the \hyperref[sec:direction]{Direction} where the drone wants to go. The method will be called by the \hyperref[sec:gamemanager]{GameManager} each turn in order to move and charge the drone. The method will be overridden by the \hyperref[sec:statefuldrone]{StatefulDrone} and \hyperref[sec:statelessdrone]{StatelessDrone} which use their own algorithms.
    \item \textbf{move(Direction direction)}: moves the drone in the specified direction by updating \textit{currentPos} and consuming 1.25 \textit{power}. Additionally, adds the move to \textit{moveHistory}. The drone can only move when allowed by the \hyperref[sec:gamemanager]{GameManager} (indicated by \textit{GameManager.moveAllowed)} and can't move outside the play area.
    \item \textbf{charge(double coinsAmount, double powerAmount)}: charges the drone with \textit{coins} and \textit{power}. The drone can only charge when the \hyperref[sec:gamemanager]{GameManager} allows it (indicated by GameManager.chargeAllowed).
\end{itemize}
\bigskip


\subsubsection{DroneLogic}
\label{sec:dronelogic}
\textbf{Description:} This is an interface representing the Drone's decision-making algorithm. Stateful and Stateless implementations of this interface are described below.
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove(Position currentPos)}: should return the \hyperref[sec:direction]{Direction} where the \textit{Drone} wants to move, based on its current \hyperref[sec:position]{Position}. 
\end{itemize}
\bigskip


\subsubsection{StatelessLogic}
\label{sec:statelesslogic}
\textbf{Description:} This class is the stateless implementation of \hyperref[sec:dronelogic]{DroneLogic}, and thus represents the \hyperref[sec:statelessdrone]{StatelessDrone}'s decision-making algorithm. It has no memory and thus only makes decisions based on what's around it.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{rnd}: The \hyperref[sec:drone]{Drone}'s Random Number Generator
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove(Position currentPos)} returns the \hyperref[sec:direction]{Direction} that the stateless drone considers to be the best. The algorithm performs the following steps:
    \begin{enumerate}
        \item Finds which \textit{Stations} are within 1 move away
        \item If there is at least one positive \textit{Station}, returns the \textit{Direction} which would reach the best \textit{bestNearbyStation()}.
        \item Otherwise, returns a random \textit{Direction} which avoids negative \textit{Stations}.
    \end{enumerate}
    A full description of the algorithm's strategy can be found in the \textit{StatelessLogic.java} file.
    \item \textbf{bestNearbyStation(List\textless Station\textgreater  nearbyStations)}: given a list of nearby \hyperref[sec:station]{Stations}, returns the \textit{Station} with the highest \textit{money} value. The \hyperref[sec:sortbymoney]{SortByMoney} Comparator is used for this method.
\end{itemize}
\bigskip

\subsubsection{StatelessDrone}
\label{sec:statelessdrone}
\textbf{Description:} This class represents the stateless drone. It is a sub-class of the \hyperref[sec:drone]{Drone}, which also contains a \hyperref[sec:statelesslogic]{StatelessLogic} component.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{droneLogic}: the \hyperref[sec:statelesslogic]{StatelessLogic} component used for deciding the next move.
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove()}: calls the \hyperref[sec:dronelogic]{droneLogic.getNextMove()} method to return the \hyperref[sec:direction]{Direction} where the stateless drone wants to move. This method is used by the \hyperref[sec:gamemanager]{GameManager} when requesting the drone's next move.
\end{itemize}
\bigskip


\subsubsection{StatefulLogic}
\label{sec:statefullogic}
\textbf{Description:} This class is the stateful implementation of \hyperref[sec:dronelogic]{DroneLogic}, and thus represents the \hyperref[sec:statefuldrone]{StatefulDrone}'s decision-making algorithm. It uses memory to plan a route which will visit all the positive \hyperref[sec:station]{Stations} on the \hyperref[sec:map]{Map} and avoid all negative \textit{Stations}. All the drone's moves are planned from initialisation. Please read the \hyperref[sec:statefuldronestrategy]{Stateful Drone Strategy} for an understanding of how the algorithm works.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{tempPos}: the \hyperref[sec:position]{Position} of the stateful drone as it simulates its moves around the \hyperref[sec:map]{Map}. Since the drone can only move when the \hyperref[sec:gamemanager]{GameManager} allows it, planning its moves requires using this variable to simulate moving.
    \item \textit{moveNumber}: keeps track of the drone's turns executed in the \hyperref[sec:gamemanager]{GameManager}
    \item \textit{rnd}: the Random Number Generator with the seed specified by the \textit{User}
    \item \textit{plannedMoves}: an \textit{ArrayList} of \hyperref[sec:direction]{Directions} containing all the pre-planned moves of the stateful drone. 
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove(Position currPos)}: returns the \hyperref[sec:direction]{Direction} where the stateful drone wants to go. Since all moves are pre-planned, the \textit{currPos} field is not used and the method returns the \textit{plannedMoves} element of index \textit{moveNumber}.
    \item \textbf{planRoute(Position startingPos)}: returns an \textit{ArrayList} containing all positive \hyperref[sec:station]{Stations} in the order they will be visited by the Drone. The algorithm used is a greedy nearest-station.
    \item \textbf{getPathToNextStation(Position startingPos, Station goalStation)}: returns an \textit{ArrayList} of \hyperref[sec:direction]{Directions} representing the drone's best path from its starting \hyperref[sec:position]{Position} to the goal \hyperref[sec:station]{Station}. The method uses an \href{https://www.geeksforgeeks.org/a-search-algorithm/}{A* Search Algorithm \textsuperscript{[2]}} and a \hyperref[sec:node]{Node} class in order to represent nodes in the search graph.
    \item \textbf{planAllMoves(Position startingPos, ArrayList\textless Station \textgreater route)}: returns an \textit{ArrayList} of \hyperref[sec:direction]{Directions} containing all the 250 moves that the stateful drone has pre-planned. The algorithm uses the \textit{route} obtained from \textit{planRoute()} and calls the \textit{getPathToNextStation()} for each \textit{Station} in the \textit{route}. If the drone finishes early, it will zigzag between its last move.
    \item \textbf{approximateMovesToStation(Position pos, Station station)}: returns approximately how many moves it takes to get from the specified \hyperref[sec:position]{Position} to the \hyperref[sec:station]{Station}. This method is used as a heuristic in the A* search.
    \item \textbf{getRandomDirection(Position currentPos)}: returns a random \hyperref[sec:direction]{Direction} for the drone to move to. This method is only used as a precaution for the A* search algorithm getting stuck.
    \item \textbf{getInverseDirection(Direction dir)}: returns the inverse of a \hyperref[sec:direction]{Direction} (e.g. NE - SW). This method is used by the drone when it finishes early, so it can zigzag until it finishes 250 moves.
\end{itemize}
\bigskip

\subsubsection{StatefulDrone}
\label{sec:statefuldrone}
\textbf{Description:} This class represents the stateful drone. It is a sub-class of the \hyperref[sec:drone]{Drone}, which also contains a \hyperref[sec:statefullogic]{StatefulLogic} component.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{droneLogic}: the \hyperref[sec:statefullogic]{StatefulLogic} component used for deciding the drone's moves
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{getNextMove()}: calls the \hyperref[sec:dronelogic]{droneLogic.getNextMove()} method to return the \hyperref[sec:direction]{Direction} where the stateful drone wants to move. In the \textit{StatefulLogic} component, all the moves are pre-computed. This method is used by the \hyperref[sec:gamemanager]{GameManager} when requesting the drone's next move.
\end{itemize}
\bigskip

\subsubsection{SortByMoney}
\label{sec:sortbymoney}
\textbf{Description:} A comparator used for comparating \hyperref[sec:station]{Stations} based on their \textit{money} field. This is used by the \hyperref[sec:statelesslogic]{StatelessLogic} class when determining the best \hyperref[sec:station]{Station} to move to. 
\bigskip

\subsubsection{Node}
\label{sec:node}
\textbf{Description:} This class is used for representing nodes in the A* search algorithm used in \hyperref[sec:statefullogic]{StatefulLogic}'s \textit{getPathToNextStation()} method. In this case, a Node represent a \hyperref[sec:position]{Position} where the drone could move on the map. They are necessary for finding the path from the drone to a \hyperref[sec:station]{Station}.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{position}: the \hyperref[sec:position]{Position} of the \textit{Node}
    \item \textit{parentNode}: the parent \textit{Node}
    \item \textit{directionFromParent}: the \hyperref[sec:direction]{Direction} in which the \textit{parentNode} had to move to reach the current \textit{Node}
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{equals(Object o)}: this method overrides the standard \textit{equals()} method. Two nodes are considered equal if they have the same \textit{position}. Returns true or false.
\end{itemize}
\bigskip

\subsubsection{Station}
\label{sec:station}
\textbf{Description:} This class is used for storing information about the stations on the \hyperref[sec:map]{Map} and updating them as the \hyperref[sec:drone]{Drone} charges around.
\\

\textbf{Fields:}
\begin{itemize}
    \item \textit{power}: the amount of power contained
    \item \textit{money}: the amount of money contained
    \item \textit{symbol}: the station's symbol ("danger" or "lighthouse")
    \item \textit{color}: the color of the corresponding feature on the GeoJSON map
    \item \textit{position}: the \hyperref[sec:position]{Position} of the station on the \hyperref[sec:map]{Map}
    \item \textit{id}: the id of the station
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{takePower(double amount)}: takes away an \textit{amount} of power after the \hyperref[sec:drone]{Drone} charges. This method is called by the \hyperref[sec:gamemanager]{GameManager} after the \textit{Drone} has charged. The \textit{amount} can be negative if the \textit{Station} is negative.
    \item \textbf{takeMoney(double amount)}: takes away an \textit{amount} of money after the \hyperref[sec:drone]{Drone} charges. This method is called by the \hyperref[sec:gamemanager]{GameManager} after the \textit{Drone} has charged. The \textit{amount} can be negative if the \textit{Station} is negative.
\end{itemize}
\bigskip

\subsubsection{Position}
\label{sec:position}
\textbf{Description:} This class describes the geographical coordinates of the \hyperref[sec:drone]{Drone} and \hyperref[sec:station]{Stations} on the \hyperref[sec:map]{Map}, as well as providing geographical functionality.
\\

\textbf{Fields:} 
\begin{itemize}
    \item \textit{latitude}: the latitude position
    \item \textit{longitude}: the longitude position
    \item \textit{directionChanges}: a \textit{HashMap} of \hyperref[sec:direction]{Direction} keys and \textit{ArrayList \textless double \textgreater} values, representing the changes in position from moving in each of the 16 \textit{Directions}. The amounts are pre-computed in a static final variable, since they are the same for all positions.
\end{itemize}
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{nextPosition(Direction direction)}: returns the next position of the \hyperref[sec:drone]{Drone} after it would move in the specified \hyperref[sec:direction]{Direction}.
    \item \textbf{inPlayArea()}: returns whether or not this position is in the play area.
    \item \textbf{inRange(Position destination)}: returns whether or not this Position lies within 0.00025 degrees of the destination. This is used for detecting whether a \hyperref[sec:drone]{Drone} is in range of a \hyperref[sec:station]{Station}.
    \item \textbf{getDist(Position destination)}: returns the euclidean distance between this position and the \textit{destination}. This is used for calculating the distance between the \hyperref[sec:drone]{Drone} and a \hyperref[sec:station]{Station}.
    \item \textbf{sinOfAngle(double angDeg)}: returns the sine value of an angle. Used in computing \textit{directionChanges}.
    \item \textbf{cosOfAngle(double angDeg)}: returns the cosine value of an angle. Used in computing \textit{directionChanges}.
\end{itemize}
\bigskip

\subsubsection{Direction}
\label{sec:direction}
\textbf{Description:} This is an Enumerator describing the 16 different compass directions in which the \textit{Drone} can move.
\bigskip

\subsubsection{DistanceComparator}
\label{sec:distancecomparator}
\textbf{Description:} this is a Comparator used for sorting \hyperref[sec:station]{Stations} based on their distance from a \textit{Position} of origin (usually the \hyperref[sec:drone]{Drone}'s position).
\bigskip

\subsubsection{Debugger}
\label{sec:debugger}
\textbf{Description:} This class contains several useful methods for debugging the project. Please note that this class \textbf{should not be added to a public release} and should only be used for debugging purposes.
\\

\textbf{Methods:}
\begin{itemize}
    \item \textbf{printMapFeatures()}: prints out all the features extracted from the GeoJSON map.
    \item \textbf{printArgs()}: prints out the input arguments: date, latitude, longitude, seed, droneType
    \item \textbf{printStations()}: prints out the latitude, longitude, money, power values of each station in the \hyperref[sec:map]{Map}.
    \item \textbf{printStationsByDistance(Position)}: prints out all \hyperref[sec:station]{Station}s' IDs, ordered by their distance from the origin.
    \item \textbf{printChargeMessage(Station)}: prints out whether the drone charged from a Positive, Negative or Empty station. This method is called by the \hyperref[sec:gamemanager]{GameManager} to track the \hyperref[sec:drone]{Drone}'s performance.
    \item \textbf{printMaxCoins()}: prints out the sum of coins from all positive \hyperref[sec:station]{Stations} on the \hyperref[sec:map]{Map}.
\end{itemize}
\newpage


\section{Stateful Drone Strategy}
\label{sec:statefuldronestrategy}

\subsection{Similarity with Travelling Salesman Problem}
The Traveling Salesman Problem (often called TSP) is a classic algorithmic problem in the field of computer science. The Travelling Salesman Problem describes a salesman who must travel between N cities. The order in which he does so is something he does not care about, as long as he visits each once during his trip. There are several variants of this problem, but the task focuses on optimization and finding the shortest (or in some cases cheapest) travel path.\textsuperscript{\href{https://simple.wikipedia.org/wiki/Travelling_salesman_problem}{[2]}}
\\

\begin{figure}[H]
     \centering
     \begin{subfigure}[b]{0.45\textwidth}
         \centering
         \includegraphics[width=\textwidth]{TSP.png}
         \caption{visual example of the TSP}
         \label{fig:tsp}
     \end{subfigure}
     \hfill
     \begin{subfigure}[b]{0.45\textwidth}
         \centering
         \includegraphics[width=\textwidth]{powergrabmap.png}
         \caption{a PowerGrab map}
         \label{fig:powergrabmap}
     \end{subfigure}
        \caption{Visual similarity between TSP and PowerGrab}
        \label{fig:three graphs}
\end{figure}

After looking at the PowerGrab map we can notice that our task is very similar to the Travelling Salesman Problem: finding a path that collects as many (preferably all) coins on the map. In other words, finding a path that goes through all positive stations.
\\

After testing the Stateless Drone, I have realised that most stations lie within less than 5 moves of another station. Since each map contains around 30 positive stations, I have anticipated that the drone should be able to visit all positive stations in less than 150 moves by using a greedy nearest-station algorithm. \\

In conclusion, the algorithm used for the Stateful Drone is an implementation of the Travelling Salesman Problem. It attempts to find a path which goes through all positive stations while avoiding negative stations. 
\bigskip

\subsection{The Strategy}
\bigskip

\subsubsection{Description}
The Stateful Drone algorithm (implemented in the \hyperref[sec:statefullogic]{StatefulLogic class}) plans all its 250 moves at initialisation. In summary, the algorithm is divided into two parts:
\begin{enumerate}
    \item \textbf{Construct a Route}: an order in which the positive stations will be visited by the drone. The route is similar to the \textit{TSP figure above}: it represents a graph of stations, which the drone will use to travel from station to station. This part is implemented through a \href{https://en.wikipedia.org/wiki/Greedy_algorithm}{Greedy (nearest-station) algorithm}.
    \item \textbf{Plan a path from Station to Station}: a set of moves that the drone will execute to move from Station to Station in the planned Route. Planning a path is necessary for the drone to move efficiently and avoid negative stations. This part is implemented through an \href{https://www.geeksforgeeks.org/a-search-algorithm/}{A* search algorithm}.
\end{enumerate}
This strategy should result in a large performance improvement over the Stateless Drone. As compared to moving randomly and 'seeing' only what's surrounding it, the Stateful Drone memorises a Route, simulates its movements to construct a Path, and memorises all its 250 moves from initialisation.

\subsubsection{Step by Step Implementation}
\begin{enumerate}
    \item \textbf{Construct the Route (Greedy nearest-station)}
    \begin{enumerate}
        \item Take the drone's starting position
        \item Find the nearest positive station, add it to the Route
        \item Find the next nearest positive station, add it to the Route. Repeat until all positive stations have been added
    \end{enumerate}
    \item \textbf{Plan a path to each Station in the Route (A* search)} \\ We will need a \href{sec:node}{Node} class (containing position, f-value, parent node, direction from the parent) to represent nodes in the search. \\
    We will also need a List of nodes which are/will be visited  \textit{(called visitedList)} - this prevents nodes from being visited twice - and a List of nodes to expand in the search algorithm \textit{(called nodeList)}.
    \begin{enumerate}
        \item Take the drone's starting position
        \item Create a Node containing the starting position (we can initialise f to 0). Add it to the nodeList and visitedList.
        \item Get the node with the least f-value. Remove it from the nodeList.
        \item Generate the node's successors (16 Nodes corresponding to moving in the 16 Directions)
        \item Remove successors which go outside the play area, or are contained in the visitedList
        \item For each remaining successor:
        \begin{itemize}
            \item If the successor reaches the goal Station, stop the search
            \item Calculate the successor's f-value. \\
            g = 1000 if the Node is within range of a negative station, 1 otherwise. This ensures the drone avoids negative stations. \\
            h = approximate moves to reach the Station = (Station's position - Node's position) / 0.00025
            f = g + h
        \end{itemize}
        \item Add the successors to the nodeList
        \item Repeat from step 2(c) until the destination is reached
        \item Return the path (\textit{List of Directions}) by backtracking through the Node's \textit{parentNode} field
    \end{enumerate}
    \item \textbf{Add each path to a the list of \textit{plannedMoves}}
    \item \textbf{If all positive stations have been visited in less than 250 moves, add ZigZaggin moves to the list of plannedMoves until it contains 250 moves}
    \item \textbf{The plannedMoves list now contains all 250 pre-planned moves, which the Drone can execute}
\end{enumerate}
\\

When implementing the code for the algorithm described above, a few modifications have been made:
\begin{itemize}
    \item A \textit{TreeMap \textless Integer, ArrayList\textless Node\textgreater\textgreater} has been used to represent the \textit{nodeList} containing Nodes and their respective f-values. This representating allows us to find the node with the least f-value in logarithmic time, rather than search through the entire list (which expands at a branching factor of 16). \\
    The \textit{Integer} key represents to the f-value. The \textit{ArrayList\textless Node \textgreater} contains all the Nodes which have the corresponding f-value.
    \item To ensure the algorithm doesn't enter an infinite-loop, the A* search will stop after 100 iterations and will return a random legal Direction.
\end{itemize}
\newpage



\subsection{Results}
Testing on the \href{https://www.learn.ed.ac.uk/bbcswebdav/pid-4305720-dt-content-rid-10939441_1/xid-10939441_1}{Powergrab Evaluator} (Author: Bora M Alper), the Stateful Drone manages to collect 100\% of the coins on 727 Maps, and more than 96\% of coins on the remaining 3 Maps.
\\

\textbf{The drone collects maximum coins on 99.5\% of the Maps.}\\
The drone usually finishes collecting all coins in less than 150 moves (The rest are ZigZags).\\
The average runtime of the program is around 0.40 seconds (0.60 seconds on DICE).

\begin{figure}[H]
    \centering
    \includegraphics[width=.8\textwidth]{evaluator.png}
    \caption{Result on the Evaluator: Collects all the coins on 99.5\% of the Maps}
    \label{fig:evaluatorresult}
\end{figure}

The figure below shows the comparison between the Stateless and Stateful Drone playing on the same map.

\begin{figure}[H]
     \centering
     \begin{subfigure}[b]{0.45\textwidth}
         \centering
         \includegraphics[width=\textwidth]{stateless_comparison.png}
         \caption{Stateless Drone performance}
         \label{fig:tsp}
     \end{subfigure}
     \hfill
     \begin{subfigure}[b]{0.45\textwidth}
         \centering
         \includegraphics[width=\textwidth]{stateful_comparison.png}
         \caption{Stateful Drone performance}
         \label{fig:powergrabmap}
     \end{subfigure}
        \caption{Comparison between Stateless and Stateful Drone, on 04/04/2019 Map}
        \label{fig:drone comparison}
\end{figure}

As we can see the Stateless Drone has a jittery path with quite a lot of back-and-forth motion (because, being memoryless, it cannot remember where it has been). It attempts to avoid negative stations while visiting positive stations, but leaves many stations uncharged and manages to \textbf{collect only 900 coins} (39\% of the maximum). \\
In comparison, the Stateful Drone manages to visit all positive stations in only 116 Moves, \textbf{collecting all 2274 coins} and avoiding all negative stations.

\subsection{Further Improvements}
While the Stateful Drone's performance is very good, it is also clear that it could be improved even further. For example, the path it follows on certain maps does not seem optimal (see figure below). This is because the Greedy nearest-station route (described in the algorithm above) does not necessarily make the best decisions.

\begin{figure}[H]
    \centering
    \includegraphics[width=.6\textwidth]{pathexample.png}
    \caption{Stateful Drone's path on the 10/10/2019 Map}
    \label{fig:pathexample}
\end{figure}

One way to improve this would be to implement a \href{https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/}{2-Opt Swap Optimization Algorithm \textsuperscript{[3]}} on the initial greedy route to rearrange it into an even shorter route. To improve even further, we could run the 2 Opt-Swap optimization algorithm several times and pick the shortest path overall, but this would increase runtime. While this solution will not be a global optima, it should be noticeably better than the initial greedy solution. \\

However, the Drone manages to collect all coins in less than 150 moves in almost all the maps, so for the purpose of this project the current implementation seems to be sufficient. \\
\bigskip

\section{Personal Conclusions}
I found this project to be very enjoyable, mainly because I challenged myself to abide by the \textit{Separation of Concerns} design principle. Implementing an efficient Stateful Drone was not as difficult as I expected. Initially, I planned on using the 2-Opt Swap algorithm to optimize the Drone's route, but decided not to after seeing that the Greedy algorithm is good enough.
\\

Overall I am happy that I learned new things and I feel more confident in my Software Engineering capabilities.
\newpage

\section{References}
[1] \href{https://en.wikipedia.org/wiki/Separation_of_concerns}{Separation of Concerns desgin principle - Wikipedia}\\

[2] \href{https://www.geeksforgeeks.org/a-search-algorithm/}{A* Search Algorithm - geeksforgeeks.org}\\

[3] \href{https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/}{2-Opt Swap Optimization Algorithm - technical-recipes.com}
\\

\bigskip

This project wouldn't have been possible wihtout the following resources: \\

\url{https://stackoverflow.com/} - It features questions and answers on a wide range of topics in computer programming. It's a great place to find answers when you're stuck. \\

\url{https://www.geeksforgeeks.org} - A computer science portal for geeks. It contains well written, well thought and explained programming articles and algorithms. It's a great place to learn how to use methods, libraries or algorithms. \\

\url{https://docs.oracle.com/javase/7/docs/api/j} - Oracle's API specification for the Java™ Platform, Standard Edition \\

\url{https://www.youtube.com/watch?v=SC5CX8drAtU&t=10s} - A visually comparison of the Greedy, Local Search, and Simulated Annealing strategies for addressing the Traveling Salesman problem. [Author: \href{https://www.youtube.com/channel/UC0eUEIKsRhcTRD5melye6Tg}{n Sanity channel}] \\

\url{https://piazza.com/} - Used by the Classroom for answering questions related to the coursework. Professor Stephen Gilmore has been incredibely helpful. \\

Lecture Slides from Professors Stephen Gilmore and Paul Jackson (School of Informatics, The University of Edinburgh) \\

Bora M. Alper's Powergrab Visualiser and Powergrab Evaluator \\


\end{document}

