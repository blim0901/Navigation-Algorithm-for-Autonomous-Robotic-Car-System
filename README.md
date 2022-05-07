# Navigation-Algorithm-for-Autonomous-Robotic-Car-System
The aim of this project is to build a robotic system that can autonomously traverse a known area and detect the images in the the arena. The algorithm aims to find the most efficient path for getting to the images, and at the same time avoid the obstacles. This is a team effort, and my contribution is to develop the algorithm that can help the robot navigate the map autonomously. 

The algorithm that I helped to develop comprise of two main parts. The first part is to decide the sequence of visiting the images and the second part is to determine the path between two images. 

We have used the exhaustive search algorithm to determine the optimal sequence of images to be visited.

Next, we utilised A Star search to find the shortest path between the images. The algorithm is able to find the path by using movements such as moving forward, backwards, 90 degree turns, and on the spot turns.

The navigation instructions will be sent to the RPI module via a socket program.
