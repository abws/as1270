import matplotlib.pyplot as plt

# Define the wind farm area
xmin = 0
xmax = 100
ymin = 0
ymax = 100

# Define the list of coordinates
coordinates = [[10, 20], [30, 40], [50, 60], [70, 80], [90, 100]]

# Create a scatter plot of the coordinates
x = [coord[0] for coord in coordinates]
y = [coord[1] for coord in coordinates]
plt.scatter(x, y)

# Set the y-axis limit to be equal to the greatest x or y coordinate
y_max = max(max(x), max(y))
plt.ylim([ymin-10, y_max+10])

# Draw a bounding box around the wind farm area
plt.plot([xmin, xmax], [ymin, ymin], 'k-')
plt.plot([xmin, xmax], [ymax, ymax], 'k-')
plt.plot([xmin, xmin], [ymin, ymax], 'k-')
plt.plot([xmax, xmax], [ymin, ymax], 'k-')

# Set the x-axis and y-axis limits to show the entire wind farm area
plt.xlim([xmin-10, xmax+10])

# Display the plot
plt.show()