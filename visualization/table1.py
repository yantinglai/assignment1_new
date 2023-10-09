import pandas as pd
import matplotlib.pyplot as plt

data = {
    'Metric': [
        'Wall Time (seconds)', 'Throughput (requests/sec)', 'Mean (ms)', 'Median (ms)', 
        '99% (ms)', 'Max (ms)', 'Min (ms)'
    ] * 3,
    'Load Test': ['10 clients'] * 7 + ['20 clients'] * 7 + ['30 clients'] * 7,
    'Java Server Client 2': [
        88.0, 1136.364, 38.199, 36.0, 83.0, 818.0, 11.0,
        139.0, 2158.273, 38.491, 36.0, 83.0, 610.0, 11.0,
        139.0, 2158.273, 38.491, 36.0, 83.0, 610.0, 11.0
    ],
    'Go Server Client 2': [
        96.0, 1041.667, 38.170, 36.0, 83.0, 818.0, 11.0,
        117.0, 1709.402, 38.279, 36.0, 83.0, 844.0, 11.0,
        147.0, 2040.816, 39.109, 36.0, 88.0, 1082.0, 11.0
    ]
}

df = pd.DataFrame(data)

fig, ax = plt.subplots(figsize=(10, 8)) 
ax.axis('tight')
ax.axis('off')
the_table = ax.table(cellText=df.values, colLabels=df.columns, cellLoc = 'center', loc='center', colWidths=[0.2, 0.2, 0.3, 0.3])

the_table.auto_set_font_size(False)
the_table.set_fontsize(8)
the_table.scale(1.2, 1.2)

plt.title("Java vs Go Server Client 2 Performance Comparison", fontsize=16)
plt.show()
plt.savefig("java_vs_go_comparison.png", dpi=300, bbox_inches='tight')
