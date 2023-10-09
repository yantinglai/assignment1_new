import matplotlib.pyplot as plt

# Test data for Java and Go server load tests on Client 2
java_tests_client2 = [
    (10, 1136.36),
    (20, 2158.27),
    (30, 2158.27)
]

go_tests_client2 = [
    (10, 1041.67),
    (20, 1709.40),
    (30, 2040.82)
]

# Extract x and y values for Java and Go tests on Client 2
java_x_client2, java_y_client2 = zip(*java_tests_client2)
go_x_client2, go_y_client2 = zip(*go_tests_client2)

# Create a scatter plot with connected dots for Java and Go tests on Client 2
plt.scatter(java_x_client2, java_y_client2, label='Java Server (Client 2)', marker='o', color='orange')
plt.plot(java_x_client2, java_y_client2, linestyle='-', color='orange', alpha=0.5)
plt.scatter(go_x_client2, go_y_client2, label='Go Server (Client 2)', marker='o', color='blue')
plt.plot(go_x_client2, go_y_client2, linestyle='-', color='blue', alpha=0.5)

# Set plot labels and title
plt.xlabel('Thread Groups')
plt.ylabel('Throughput (requests per second)')
plt.title('Performance Comparison: Java vs. Go Server (Client 2)')

# Add a legend
plt.legend()

# Show the plot
plt.grid(True)
plt.show()
