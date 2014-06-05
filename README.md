Motion Planner for Animated Character in Dynamic Environment

This is my undergraduate research project that tries to get a new approach to make an animated agent with a goal to reach and to act like a human plans a path in a changeable environment.

Dynamic Environment
- Movable obstacles(Add new obstacles, Remove old obstacles, Drag and move obstacles)- No moving obstacles with regular motion
Animated Character- The animated character: Agent- Being able to plan a path with a specific goal.- Adjust the path during approaching(moving toward) the goal.- Three degrees of freedom.- Only able to “walk” at a constant speed for now.
Reconfigurable Random Forest:
Maintain not just a tree in the road-map, but many. It keeps the information of road-map after each query and to make use of it in the next query. This is beneficial for the planner to do many times of planning.
Strategy- Separate the time consuming part: collision detection into every interval of frames.- In each intervals we do these things in priority:	1. Maintain the path’s accuracy 	2. Find a new path when the path becomes invalid	3. Maintain the road-map’s correctnessGoal
- Plan a path from initial configuration to goal configuration and adjust the path during moving when the path collides.- Key Point: Minimize the time replan the path during running, keeping the animation smoothly running.

Project Info:

1. Motion Planning (Potential Field) is the basic version of a motion planner in 3-dimensional freedom space.
2. Dynamic Motion Planner is an improved version with new algorithm recalculating a new route when the existing route is blocked
3. DMP in Game Scenario applies a different map with a 2d character who is able to perform different actions.