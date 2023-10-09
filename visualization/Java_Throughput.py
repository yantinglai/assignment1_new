import pandas as pd
import matplotlib.pyplot as plt

# Specify the file path to your CSV file
file_path = '/Users/sundri/Desktop/output.csv'

# Read the CSV file
df = pd.read_csv(file_path, names=['Timestamp', 'Action', 'Throughput', 'Status Code'])

# Convert the 'Timestamp' column to datetime
df['Timestamp'] = pd.to_datetime(df['Timestamp'], unit='ms')

# Set the 'Timestamp' column as the DataFrame index
df.set_index('Timestamp', inplace=True)

# Resample data to one-second intervals and calculate the sum of throughput
resampled_df = df['Throughput'].resample('1S').sum()

# Create a line chart for throughput over time
plt.figure(figsize=(12, 6))
plt.plot(resampled_df.index, resampled_df.values)
plt.xlabel('Time (1-second intervals)')
plt.ylabel('Throughput')
plt.title('Throughput Over Time (1-Second Intervals) Java Server')
plt.grid(axis='y', linestyle='--', alpha=0.7)

# Show the plot
plt.tight_layout()
plt.show()
