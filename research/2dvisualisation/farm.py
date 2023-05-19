import matplotlib.pyplot as plt

# Example usage:
coordinates1 = [] 
coordinates2 = []

# enter coordinates
x_coords, y_coords = zip(*coordinates1)
x2_coords, y2_coords = zip(*coordinates2)

# Create a scatter plot of the turbine coordinates with specified marker size
# plt.scatter(x_coords, y_coords, c='darkorange', marker='o', s=3)
plt.scatter(x2_coords, y2_coords, c='dodgerblue', marker='o',s=3)


plt.gca().set_aspect('equal')
plt.gca().set_xticks([0, 7000])
plt.gca().set_yticks([0, 14000])
plt.xlabel('Width=3500m')
plt.ylabel('Length=16100m')
plt.title('Turbine Coordinates on Wind Farm')
plt.show()



