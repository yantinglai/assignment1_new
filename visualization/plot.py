import matplotlib.pyplot as plt

# Test data
go_tests = [
    (10, 5527.92),
    (20, 5238.48),
    (30, 5145.18)
]

java_tests = [
    (10, 5525.17),
    (20, 5242.46),
    (30, 5146.68)
]

# Extract x and y values for Go and Java tests
go_x, go_y = zip(*go_tests)
java_x, java_y = zip(*java_tests)

# Create a scatter plot with connected dots
plt.scatter(go_x, go_y, label='Go Server', marker='o', color='blue')
plt.plot(go_x, go_y, linestyle='-', color='blue', alpha=0.5)
plt.scatter(java_x, java_y, label='Java Server', marker='o', color='orange')
plt.plot(java_x, java_y, linestyle='-', color='orange', alpha=0.5)

# Set plot labels and title
plt.xlabel('Thread Groups')
plt.ylabel('Throughput (requests per second)')
plt.title('Performance Comparison: Go vs. Java Server on Client 1')

# Add a legend
plt.legend()

# Show the plot
plt.grid(True)
plt.show()
